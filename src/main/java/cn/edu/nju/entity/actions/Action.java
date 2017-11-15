package cn.edu.nju.entity.actions;

import cn.edu.nju.analysisTable.ActionType;

/**
 * Action表中的表项
 */
public abstract class Action {

    /**
     * 类型
     */
    protected ActionType actionType;

    /**
     * 获取Action的类型
     *
     * @return
     */
    public ActionType getActionType() {
        return actionType;
    }
}
