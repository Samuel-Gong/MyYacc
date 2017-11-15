package cn.edu.nju.entity.actions.action;

import cn.edu.nju.analysisTable.ActionType;
import cn.edu.nju.entity.actions.Action;

public class ReduceAction extends Action {

    /**
     * 规约产生式在产生式列表中的index
     */
    private int reduceProductionIndex;

    public ReduceAction(int reduceProductionIndex) {
        actionType = ActionType.REDUCE;
        this.reduceProductionIndex = reduceProductionIndex;
    }

    public int getReduceProduction() {
        return reduceProductionIndex;
    }

    @Override
    public String toString() {
        return "r" + reduceProductionIndex;
    }
}
