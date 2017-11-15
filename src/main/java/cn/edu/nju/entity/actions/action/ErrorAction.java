package cn.edu.nju.entity.actions.action;

import cn.edu.nju.analysisTable.ActionType;
import cn.edu.nju.entity.actions.Action;

public class ErrorAction extends Action {

    public ErrorAction() {
        actionType = ActionType.ERROR;
    }

    @Override
    public String toString() {
        return "error";
    }
}
