package cn.edu.nju.analysisTable;

/**
 * ACTION表中的状态,移入，规约，接受
 */
public enum ActionType {
    /**
     * 移入
     */
    SHIFT,
    /**
     * 规约
     */
    REDUCE,
    /**
     * 接受
     */
    ACCEPT,
    /**
     * 错误
     */
    ERROR
}
