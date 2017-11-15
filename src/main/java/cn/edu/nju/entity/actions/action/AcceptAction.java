package cn.edu.nju.entity.actions.action;

import cn.edu.nju.analysisTable.ActionType;
import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.actions.Action;

public class AcceptAction extends Action {

    /**
     * 零号产生式在产生式列表中的index
     */
    private Production acceptProduction;

    public AcceptAction(Production acceptProduction) {
        actionType = ActionType.ACCEPT;
        this.acceptProduction = acceptProduction;
    }

    @Override
    public String toString() {
        return "acc";
    }
}
