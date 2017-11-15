package cn.edu.nju.entity;

import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.SignType;

import java.util.LinkedList;

/**
 * 产生式
 */
public class Production {

    /**
     * 产生式左部
     */
    private NonTerminalSign left;

    /**
     * 产生式右部
     */
    private LinkedList<Sign> right;

    public Production(NonTerminalSign left, LinkedList<Sign> right) {
        this.left = left;
        this.right = right;
    }

    public NonTerminalSign getLeft() {
        return left;
    }

    public void setLeft(NonTerminalSign left) {
        this.left = left;
    }

    public LinkedList<Sign> getRight() {
        return right;
    }

    public void setRight(LinkedList<Sign> right) {
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Production{");
        sb.append(left);
        sb.append("------>");
        for (int i = 0; i < right.size(); i++) {
            sb.append(right.get(i));
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * 判断右部第一个符号是否是终结符
     *
     * @return
     */
    public boolean firstRightIsTerminal() {
        return right.getFirst().getSignType() == SignType.TERMINAL;
    }
}
