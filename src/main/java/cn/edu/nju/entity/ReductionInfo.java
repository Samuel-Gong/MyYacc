package cn.edu.nju.entity;


import cn.edu.nju.entity.sign.TerminalSign;

import java.util.List;

/**
 * 规约的信息，包括规约运用的产生式和规约的过程
 */
public class ReductionInfo {

    /**
     * 原产生式
     */
    public List<Production> productions;

    /**
     * 输入字符流
     */
    public List<TerminalSign> inputSigns;

    /**
     * 规约的过程
     */
    public List<String> reductions;

    /**
     * 每一步运用的规约产生式
     */
    public List<String> reduceProductions;

    /**
     * Constructor
     *
     * @param reductions        规约的过程
     * @param reduceProductions 规约产生式
     */
    public ReductionInfo(List<String> reductions, List<String> reduceProductions) {
        this.reductions = reductions;
        this.reduceProductions = reduceProductions;
        assert reductions.size() == reduceProductions.size() : "规约产生式和规约过程的数量不同";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("********************产生式********************\n");
        for (int i = 0; i < productions.size(); i++) {
            sb.append(productions.get(i).toString() + "\n");
        }

        sb.append("********************输入字符流********************\n");
        for (int i = 0; i < inputSigns.size(); i++) {
            sb.append(inputSigns.get(i).toString() + " ");
        }
        sb.append("\n");

        sb.append("********************规约过程********************\n");
        for (int i = 0; i < reductions.size(); i++) {
            sb.append(reduceProductions.get(i));
            sb.append("             ");
            sb.append(reductions.get(i));
            sb.append("\n");
        }

        return sb.toString();
    }
}
