package cn.edu.nju.analysisTable;

import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.actions.Action;
import cn.edu.nju.entity.actions.action.AcceptAction;
import cn.edu.nju.entity.actions.action.ErrorAction;
import cn.edu.nju.entity.actions.action.ReduceAction;
import cn.edu.nju.entity.actions.action.ShiftAction;
import cn.edu.nju.entity.item.Item;
import cn.edu.nju.entity.item.ItemSet;
import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.SignType;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 语法分析表
 */
public class GrammarAnalysisTable {

    /**
     * ACTION表
     */
    private Action[][] actionTable;

    /**
     * GOTO表, 用整数表示下一个项集的index
     */
    private int[][] gotoTable;

    /**
     * 项集序列
     */
    private List<ItemSet> itemSetList;

    /**
     * 所有终结符
     */
    private List<TerminalSign> terminalSigns;

    /**
     * 所有非终结符
     */
    private List<NonTerminalSign> nonTerminalSigns;

    /**
     * 排好序的增广文法的产生式
     */
    private List<Production> productions;

    public GrammarAnalysisTable(GOTOGraph gotoGraph) {
        this.itemSetList = gotoGraph.getItemSets();
        this.nonTerminalSigns = gotoGraph.getNonTerminalSigns();
        this.productions = gotoGraph.getProductionList();

        //添加DOLLAR符号到Action的表的表头中
        this.terminalSigns = gotoGraph.getTerminalSigns();
        this.terminalSigns.add(GOTOGraph.DOLLAR);

        //构造LR语法分析表
        initTables();
    }

    /**
     * 初始化ACTION和GOTO表
     */
    private void initTables() {

        //初始化ACTION表，每个表项都设为error
        actionTable = new Action[itemSetList.size()][terminalSigns.size()];
        for (int i = 0; i < actionTable.length; i++) {
            for (int j = 0; j < actionTable[0].length; j++) {
                Action errorAction = new ErrorAction();
                actionTable[i][j] = errorAction;
            }
        }

        //初始化GOTO表，每个表项都设为-1
        gotoTable = new int[itemSetList.size()][nonTerminalSigns.size()];
        for (int i = 0; i < gotoTable.length; i++) {
            for (int j = 0; j < gotoTable[0].length; j++) {
                gotoTable[i][j] = -1;
            }
        }

        //构造一个LR语法分析表
        fillTheTables();
    }

    /**
     * 按照算法4.56，构造一个LR语法分析表
     */
    private void fillTheTables() {
        for (int i = 0; i < itemSetList.size(); i++) {
            ItemSet itemSet = itemSetList.get(i);

            //ACTION表和GOTO表中对应的行
            int row = i;

            //对每一条项集连接的边
            for (Map.Entry<Sign, ItemSet> entry : itemSet.getGotoMap().entrySet()) {
                Sign sign = entry.getKey();
                //GOTO(Ii, a) = Ij, a为终结符号, 动作为SHIFT

                if (entry.getKey().getSignType() == SignType.TERMINAL) {
                    int column = terminalSigns.indexOf(sign);
                    assert actionTable[row][column] instanceof ErrorAction
                            : "Action表状态:" + row + "对输入: " + ((TerminalSign) sign).getTerminalSign() + "产生冲突";
                    int shiftToState = itemSetList.indexOf(entry.getValue());
                    actionTable[row][column] = new ShiftAction(shiftToState);
                }
                //GOTO(Ii, A) = Ij, A为非终结符号, 动作为GOTO
                else {
                    int column = nonTerminalSigns.indexOf(sign);
                    assert gotoTable[row][column] == -1
                            : "Goto表（" + row + "," + nonTerminalSigns.indexOf(sign) + ") 不为-1";

                    int gotoState = itemSetList.indexOf(entry.getValue());
                    gotoTable[row][column] = gotoState;
                }
            }

            //检查项集中是否含有可规约项或者是可接受项
            Set<Item> itemsCanReduce = itemSet.getItemCanReduce();
            //不为空，向表中加入规约动作或者接受动作
            if (!itemsCanReduce.isEmpty()) {
                for (Item item : itemsCanReduce) {
                    int column = terminalSigns.indexOf(item.getPredictiveSign());

                    assert actionTable[row][column].getActionType() == ActionType.ERROR
                            || actionTable[row][column].getActionType() == ActionType.REDUCE
                            || actionTable[row][column].getActionType() == ActionType.SHIFT
                            : "Action表状态:" + row + "对向前看符号: " + item.getPredictiveSign().getTerminalSign() + "规约产生冲突";
                    //判断是否是接受状态
                    if (item.isAccept()) {
                        assert actionTable[row][column].getActionType() == ActionType.ERROR : ": ACTION表中接受状态不为ERROR";
                        assert productions.indexOf(item.getProduction()) == 0 : "接受状态对应的产生式不是0号产生式";
                        actionTable[row][column] = new AcceptAction(item.getProduction());
                    }
                    //不是接受状态，就用规约动作
                    else {

                        int newReduceIndex = productions.indexOf(item.getProduction());

                        //TODO 解决一个移入／归约冲突时，总是选择移入
                        if (!(actionTable[row][column].getActionType() == ActionType.SHIFT)) {
                            //TODO 解决一个归约／归约冲突时，选择在Yacc规约中列在前面的那个冲突产生式
                            if (actionTable[row][column].getActionType() == ActionType.REDUCE) {
                                int indexInTable = productions.indexOf(((ReduceAction) actionTable[row][column]).getReduceProduction());
                                if (newReduceIndex < indexInTable) {
                                    actionTable[row][column] = new ReduceAction(newReduceIndex);
                                }
                            } else actionTable[row][column] = new ReduceAction(newReduceIndex);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过当前状态和当前输入的终结符，找到ACTION表中对应的Action
     *
     * @param curState 当前状态
     * @param curSign  当前输入的终结符
     * @return ACTION表中对应的ACTION
     */
    public Action findAction(int curState, TerminalSign curSign) {
        return actionTable[curState][terminalSigns.indexOf(curSign)];
    }

    /**
     * 通过传入的当前状态和当前规约的产生式左部的非终结符，找到GOTO表中Goto的状态
     *
     * @param curState 当前状态
     * @param left     产生式的左部
     * @return GOTO表中对应的goto状态
     */
    public int findGotoState(int curState, NonTerminalSign left) {
        return gotoTable[curState][nonTerminalSigns.indexOf(left)];
    }

    /**
     * 打印LR语法分析表
     */
    public void printTable() {

        System.out.println("************************************************************");
        System.out.println("STATE*****         ACTION         ****       GOTO      *****");
        StringBuilder terminals = new StringBuilder();
        for (int i = 0; i < terminalSigns.size(); i++) {
            terminals.append("  " + terminalSigns.get(i).getTerminalSign() + "  ");
        }
        StringBuilder nonTerminals = new StringBuilder();
        for (int i = 0; i < nonTerminalSigns.size(); i++) {
            nonTerminals.append("  " + nonTerminalSigns.get(i).getNonTerminalSign() + "  ");
        }
        System.out.println("**********" + terminals.toString() + "****" + nonTerminals +
                "*****");

        for (int i = 0; i < itemSetList.size(); i++) {
            StringBuilder row = new StringBuilder();
            row.append("  " + i + "  ");
            row.append("****");
            for (int j = 0; j < terminalSigns.size(); j++) {
                row.append("  " + actionTable[i][j]);
            }
            row.append("  ****");
            for (int j = 0; j < nonTerminalSigns.size(); j++) {
                row.append("  " + gotoTable[i][j]);
            }
            row.append("  *****");
            System.out.println(row.toString());
        }
        System.out.println("************************************************************");
    }

    /**
     * 通过下标获取增广文法产生式列表中的产生式
     *
     * @param index 下标
     * @return 产生式
     */
    public Production getProduction(int index) {
        return productions.get(index);
    }
}
