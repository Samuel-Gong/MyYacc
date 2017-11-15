package cn.edu.nju.entity.item;

import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.SignType;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;

import java.util.LinkedList;

/**
 * 项的数据结构
 */
public class Item {

    /**
     * 产生式
     */
    private Production production;

    /**
     * dot当前的位置，范围在0-right.size();
     */
    private int dotPos;

    /**
     * 该项的向前看符号
     */
    private TerminalSign predictiveSign;

    /**
     * 标记是否已经进行过内部扩展,以免在内部扩展时重复计算
     */
    private boolean hasInnerExtended;

    public Item(Production production, int dotPos, TerminalSign predictiveSign) {
        this.production = production;
        this.dotPos = dotPos;
        this.predictiveSign = predictiveSign;

        hasInnerExtended = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (dotPos != item.dotPos) return false;
        if (!production.equals(item.production)) return false;
        return predictiveSign.equals(item.predictiveSign);
    }

    @Override
    public int hashCode() {
        int result = production.hashCode();
        result = 31 * result + dotPos;
        result = 31 * result + predictiveSign.hashCode();
        return result;
    }

    public int getDotPos() {
        return dotPos;
    }

    public void setDotPos(int dotPos) {
        this.dotPos = dotPos;
    }

    public TerminalSign getPredictiveSign() {
        return predictiveSign;
    }

    public void setPredictiveSign(TerminalSign predictiveSign) {
        this.predictiveSign = predictiveSign;
    }

    public Production getProduction() {
        return production;
    }

    /**
     * 判断当前dot所在位置后面是不是指定sign符号
     *
     * @param sign 文法符号
     * @return
     */
    public boolean canMove(Sign sign) {
        LinkedList<Sign> right = production.getRight();
        //如果点不在最后 且紧跟点之后的文法符号跟要匹配的文法符号相同
        return dotPos < right.size() && right.get(dotPos).equals(sign);
    }

    /**
     * 判断当前dot所在位置后面是不是跟着一个非终结符
     *
     * @return 是否后面跟着一个非终结符
     */
    public boolean afterDotIsNonTerminal() {
        LinkedList<Sign> right = production.getRight();
        return dotPos < right.size() && right.get(dotPos).getSignType() == SignType.NON_TERMINAL;
    }

    /**
     * 获取紧跟dot后面的非终结符
     *
     * @return
     */
    public NonTerminalSign getNonTerminalAfterDot() {
        LinkedList<Sign> right = production.getRight();
        assert right.get(dotPos).getSignType() == SignType.NON_TERMINAL : ": dot后面不是非终结符";
        return (NonTerminalSign) right.get(dotPos);
    }

    /**
     * 计算FIRST(βa)的时候，获取β的值
     *
     * @return 文法序列
     */
    public LinkedList<Sign> getBetaA() {
        LinkedList<Sign> right = production.getRight();
        LinkedList<Sign> beta = new LinkedList<>();
        if (dotPos < right.size()) {
            beta.addAll(right.subList(dotPos + 1, right.size()));
        }
        beta.add(predictiveSign);
        return beta;
    }

    /**
     * 检查该项是否可被规约，或是可被接受，通过dot的位置判断，若dot的位置在产生式右部的最后，说明可被规约
     *
     * @return
     */
    public boolean canReduce() {
        return dotPos == production.getRight().size();
    }

    /**
     * 判断该项是否是可接受项，即dot位于产生式右部的最后，且产生式左部为增广文法的开始符号
     *
     * @return
     */
    public boolean isAccept() {
        return dotPos == production.getRight().size() && production.getLeft().equals(GOTOGraph.START)
                && predictiveSign.equals(GOTOGraph.DOLLAR);
    }

    /**
     * 获取其是否已经进行过内部扩展
     *
     * @return
     */
    public boolean hasInnerExtended() {
        return hasInnerExtended;
    }

    public void setHasInnerExtended(boolean hasInnerExtended) {
        this.hasInnerExtended = hasInnerExtended;
    }
}
