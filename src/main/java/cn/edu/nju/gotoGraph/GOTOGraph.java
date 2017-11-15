package cn.edu.nju.gotoGraph;

import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.entity.item.Item;
import cn.edu.nju.entity.item.ItemSet;
import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.SignType;
import cn.edu.nju.entity.sign.TerminalSign;

import java.util.*;

/**
 * GOTO函数
 */
public class GOTOGraph {

    /**
     * 定义Z为保留的0号产生式的右部
     */
    public static NonTerminalSign START = new NonTerminalSign('Z');

    /**
     * 定义的零号产生式的向前看符号
     */
    public static TerminalSign DOLLAR = new TerminalSign("$");

    /**
     * 定义的epsilon终结符
     */
    public static TerminalSign EPSILON = new TerminalSign("\0");

    /**
     * 项集序列
     */
    private List<ItemSet> itemSets;

    /**
     * 所有产生式
     */
    private List<Production> productionList;

    /**
     * 所有文法符号
     */
    private List<Sign> allSigns;

    /**
     * 所有终结符
     */
    private List<TerminalSign> terminalSigns;

    /**
     * 所有非终结符
     */
    private List<NonTerminalSign> nonTerminalSigns;

    /**
     * 非终结符及其FIRST的集合
     */
    private Map<NonTerminalSign, Set<TerminalSign>> firstMap;

    public GOTOGraph(YaccFileInfo yaccFileInfo) {
        //初始化yacc文件的信息，产生式，非终结符集合，终结符集合
        productionList = yaccFileInfo.productions;
        terminalSigns = new ArrayList<>();
        terminalSigns.addAll(yaccFileInfo.terminalSigns);
        nonTerminalSigns = new ArrayList<>();
        nonTerminalSigns.addAll(yaccFileInfo.nonTerminalSigns);

        allSigns = new ArrayList<>();
        allSigns.addAll(nonTerminalSigns);
        allSigns.addAll(terminalSigns);

        itemSets = new ArrayList<>();

        //增广文法
        augmentGrammar();

        //初始化所有非终结符号及其FIRST集合的映射
        initFirstMap();

        //构造项集
        items();
    }

    /**
     * 增广文法
     */
    private void augmentGrammar() {
        //初始化零号产生式
        LinkedList<Sign> topRightSign = new LinkedList<>();
        //TODO 需要假设当前文法第一个产生式的左部是文法的开始符号
        topRightSign.add(productionList.get(0).getLeft());
        Production zero = new Production(GOTOGraph.START, topRightSign);
        productionList.add(0, zero);
    }

    /**
     * 构造项集的主例程
     */
    private void items() {
        //将C初始化为{closure}({S'->.S,$})
        Set<Item> zeroItemSet = new HashSet<>();
        Production zeroProduction = productionList.get(0);
        assert zeroProduction.getLeft().equals(START) : ": 增广文法的零号产生式的左部不为新的开始符号" + START;
        zeroItemSet.add(new Item(zeroProduction, 0, DOLLAR));
        ItemSet initItemSet = closure(new ItemSet(zeroItemSet));
        itemSets.add(initItemSet);

        List<ItemSet> addedItemSet = new ArrayList<>();
        //一直循环直到不再有新的项集加入到C中
        do {
            addedItemSet.clear();
            //C中每个项集I
            for (ItemSet itemSet : itemSets) {
                //每个文法符号X
                for (Sign sign : allSigns) {
                    ItemSet gotoSet = gotoFunc(itemSet, sign);
                    //如果不为空
                    if (!gotoSet.getItemSet().isEmpty()) {
                        itemSet.addEdge(sign, gotoSet);
                        //如果不在GOTO图中，添加该项集
                        if (!itemSets.contains(gotoSet)) addedItemSet.add(gotoSet);
                    }
                }
            }
            itemSets.addAll(addedItemSet);
        } while (!addedItemSet.isEmpty());
    }

    /**
     * 项集的外部扩展，向前看符号不变
     *
     * @param itemSet 源项集
     * @param sign    文法符号
     * @return
     */
    private ItemSet gotoFunc(ItemSet itemSet, Sign sign) {
        Set<Item> newSet = new HashSet<>();
        //I中的每个项[A->⍺.Xβ,a]
        for (Item item : itemSet.getItemSet()) {
            //判断dot是否能向后移动
            if (item.canMove(sign)) {
                int newDotPos = item.getDotPos();
                newDotPos++;
                newSet.add(new Item(item.getProduction(), newDotPos, item.getPredictiveSign()));
            }
        }
        return closure(new ItemSet(newSet));
    }

    /**
     * 项集的内部扩展
     *
     * @param itemSet 初始项集
     */
    private ItemSet closure(ItemSet itemSet) {
        Set<Item> originItemSet = itemSet.getItemSet();

        Set<Item> newAddItemSet = new HashSet<>();

        do {
            originItemSet.addAll(newAddItemSet);
            newAddItemSet.clear();

            for (Item item : originItemSet) {
                LinkedList<Sign> needToCalculateFirst = item.getBetaA();
                Set<TerminalSign> firstSet = FIRST(needToCalculateFirst);
                //如果之后是一个非终结符，且该item还没进行过扩展
                if (item.afterDotIsNonTerminal() && !item.hasInnerExtended()) {
                    NonTerminalSign nonTerminal = item.getNonTerminalAfterDot();
                    //added数组中下标为left的为false，说明该非终结符的非内核项还为加入项集
                    //G'中的每个产生式B->𝛾
                    for (Production production : productionList) {
                        if (production.getLeft().equals(nonTerminal)) {
                            //FIRST(βa)中的每个终结符号b
                            for (TerminalSign b : firstSet) {
                                //将[B->𝛾, b]加入到集合I中
                                newAddItemSet.add(new Item(production, 0, b));
                            }
                        }
                    }
                }
                item.setHasInnerExtended(true);
            }
        }
        //当项集中包含新添加的项集时，循环退出
        while (!originItemSet.containsAll(newAddItemSet));
        return new ItemSet(originItemSet);
    }

    /**
     * 计算X1...Xn串的FIRST集合
     *
     * @param signLinkedList 文法符号串
     * @return
     */
    private Set<TerminalSign> FIRST(LinkedList<Sign> signLinkedList) {
        Set<TerminalSign> terminalSigns = new HashSet<>();
        if (signLinkedList.size() == 1) {
            Sign sign = signLinkedList.getFirst();
            assert sign.getSignType() == SignType.TERMINAL : "向前看符号不是终结符";
            terminalSigns.add((TerminalSign) sign);
        } else {
            boolean allHasEpsilon = true;
            boolean hasEpsilon = true;
            int curIndex = 0;
            while (hasEpsilon && curIndex < signLinkedList.size()) {
                Set<TerminalSign> thisTerminalSigns = FIRST(signLinkedList.get(curIndex++));
                if (thisTerminalSigns.contains(EPSILON)) {
                    hasEpsilon = true;
                    thisTerminalSigns.remove(EPSILON);
                } else {
                    hasEpsilon = false;
                    allHasEpsilon = false;
                }
                //加入F(X1)的所有非ℇ符号
                terminalSigns.addAll(thisTerminalSigns);
            }
            //如果对于所有的i，epsilon都在FIRST(Xi)中，将epsilon加入到FIRST(X1...Xn)中
            if (allHasEpsilon) terminalSigns.add(EPSILON);
        }
        return terminalSigns;
    }

    /**
     * 对于单个文法符号计算FIRST集合
     *
     * @param sign
     * @return
     */
    private Set<TerminalSign> FIRST(Sign sign) {
        Set<TerminalSign> firsts = new HashSet<>();
        //如果X是一个终结符号，那么FIRST(X) = X
        if (sign.getSignType() == SignType.TERMINAL) {
            firsts.add((TerminalSign) sign);
        } else {
            return firstMap.get(sign);
        }
        return firsts;
    }

    /**
     * 初始化非终结符的FIRST集合
     */
    private void initFirstMap() {
        firstMap = new HashMap<>();

        //记录当前是否有延迟计算的FIRST集合
        boolean newFirstFound = false;

        //只加入产生式右部有终结符的非终结符的集合
        for (int i = 0; i < nonTerminalSigns.size(); i++) {
            Set<TerminalSign> terminalSigns = new HashSet<>();
            NonTerminalSign nonTerminalSign = nonTerminalSigns.get(i);
            for (Production production : productionList) {
                if (production.getLeft().equals(nonTerminalSign)) {
                    LinkedList<Sign> right = production.getRight();
                    //如果右部的第一位是终结符，则先加入first的终结符集合
                    if (production.firstRightIsTerminal()) {
                        terminalSigns.add((TerminalSign) right.get(0));
                    } else {
                        newFirstFound = true;
                    }
                }
            }
            //加入非终结符及其FIRST集合的映射
            firstMap.put(nonTerminalSign, terminalSigns);
        }

        //直到没有一个FIRST集合有更新的时候，结束循环
        while (newFirstFound) {
            //假设该次循环中没有需要延迟计算的FIRST集合
            newFirstFound = false;

            for (int i = 0; i < nonTerminalSigns.size(); i++) {
                Set<TerminalSign> terminalSigns = new HashSet<>();
                NonTerminalSign nonTerminalSign = nonTerminalSigns.get(i);

                for (Production production : productionList) {
                    //左部为该终结符的表达式
                    if (production.getLeft().equals(nonTerminalSign)) {
                        LinkedList<Sign> right = production.getRight();

                        //标记右部是否都含有epsilon
                        boolean allHasEpsilon = true;

                        // 右部第一位为终结符的情况在之前的循环中已经计算完毕
                        // 现在计算右部的第一位不是终结符的情况
                        if (!production.firstRightIsTerminal()) {
                            int index = 0;
                            //该index指向的产生式右部的符号
                            Sign rightSign = right.get(index);
                            assert rightSign.getSignType() == SignType.NON_TERMINAL : ": 此处产生式右部第一位应该为非终结符";

                            //当index在产生式右部的范围内时
                            while (index < right.size()) {
                                rightSign = right.get(index);
                                //非终结符
                                if (rightSign.getSignType() == SignType.NON_TERMINAL) {
                                    //如果映射中含有该非终结符的FIRST集合，则加入该FIRST集合
                                    if (firstMap.containsKey(rightSign)) {
                                        Set<TerminalSign> firstSet = new HashSet<>();
                                        firstSet.addAll(firstMap.get(rightSign));

                                        //如果含有epsilon，继续计算产生式的下一个符号
                                        if (firstSet.contains(EPSILON)) {
                                            firstSet.remove(EPSILON);
                                            terminalSigns.addAll(firstSet);
                                            index++;
                                        } else {
                                            terminalSigns.addAll(firstSet);
                                            allHasEpsilon = false;
                                            break;
                                        }
                                    } else {
                                        allHasEpsilon = false;
                                        //需要延迟计算
                                        newFirstFound = true;
                                        break;
                                    }
                                }
                                //终结符
                                else {
                                    allHasEpsilon = false;
                                    //直接添加
                                    terminalSigns.add((TerminalSign) rightSign);
                                }
                            }
                            if (allHasEpsilon) {
                                terminalSigns.add(EPSILON);
                            }
                        }
                    }
                }
                //当前非终结符及其FIRST集合的映射已存在，则更新其FIRST集合
                if (firstMap.containsKey(nonTerminalSign)) {
                    Set<TerminalSign> firstSet = firstMap.get(nonTerminalSign);
                    if (!firstSet.containsAll(terminalSigns)) {
                        firstSet.addAll(terminalSigns);
                        newFirstFound = true;
                        firstMap.put(nonTerminalSign, firstSet);
                    }
                }
                //map中不存在该非终结符的映射，加入映射
                else {
                    firstMap.put(nonTerminalSign, terminalSigns);
                    newFirstFound = true;
                }
            }
        }
    }

    public List<ItemSet> getItemSets() {
        return itemSets;
    }

    public List<Production> getProductionList() {
        return productionList;
    }

    public List<TerminalSign> getTerminalSigns() {
        return terminalSigns;
    }

    public List<NonTerminalSign> getNonTerminalSigns() {
        return nonTerminalSigns;
    }
}
