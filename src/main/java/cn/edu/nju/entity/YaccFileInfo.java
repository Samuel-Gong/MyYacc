package cn.edu.nju.entity;

import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.TerminalSign;

import java.util.List;
import java.util.Set;

/**
 * 包含文件中Production集合，以及所有合法非终结符，所有合法终结符
 */
public class YaccFileInfo {

    /**
     * Production列表
     */
    public List<Production> productions;

    /**
     * 所有合法的非终结符集合
     */
    public Set<NonTerminalSign> nonTerminalSigns;

    /**
     * 所有合法的终结符集合
     */
    public Set<TerminalSign> terminalSigns;

    public YaccFileInfo(List<Production> productions, Set<NonTerminalSign> nonTerminalSigns, Set<TerminalSign> terminalSigns) {
        this.productions = productions;
        this.nonTerminalSigns = nonTerminalSigns;
        this.terminalSigns = terminalSigns;
    }
}
