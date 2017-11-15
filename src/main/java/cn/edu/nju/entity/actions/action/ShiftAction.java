package cn.edu.nju.entity.actions.action;

import cn.edu.nju.analysisTable.ActionType;
import cn.edu.nju.entity.actions.Action;

public class ShiftAction extends Action {

    /**
     * 移入的状态
     */
    private int shiftToState;

    /**
     * @param shiftToState
     */
    public ShiftAction(int shiftToState) {
        this.actionType = ActionType.SHIFT;
        this.shiftToState = shiftToState;
    }

    public int getShiftToState() {
        return shiftToState;
    }

    @Override
    public String toString() {
        return "S" + shiftToState;
    }
}
