package cn.edu.nju;

import cn.edu.nju.analysisTable.ActionType;
import cn.edu.nju.analysisTable.GrammarAnalysisTable;
import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.actions.Action;
import cn.edu.nju.entity.actions.action.ReduceAction;
import cn.edu.nju.entity.actions.action.ShiftAction;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;

import java.util.*;

/**
 * LR语法分析器
 */
public class LRGrammarParser {

    /**
     * LR语法分析表
     */
    private GrammarAnalysisTable grammarAnalysisTable;

    /**
     * 存放状态的栈，状态由数字表示，数字对应语法分析表中的项集序列的index
     */
    private Stack<Integer> stateStack;


    public LRGrammarParser(GrammarAnalysisTable grammarAnalysisTable) {
        this.grammarAnalysisTable = grammarAnalysisTable;

        stateStack = new Stack<>();
        //初始状态为0状态
        stateStack.push(0);

    }

    /**
     * 对传入的输入队列进行语法分析
     *
     * @param inputSigns 终结符的输入队列
     * @return 自底向上的规约过程
     */
    public List<String> parseGrammar(Queue<TerminalSign> inputSigns) {
        //给输入队列最后加入一个结束符$
        inputSigns.add(GOTOGraph.DOLLAR);
        //自底向上规约的过程
        List<String> reduceProcedure = new ArrayList<>();

        //当指针未指向结束符$
        while (!inputSigns.peek().equals(GOTOGraph.DOLLAR)) {

            TerminalSign curSign = inputSigns.peek();
            int curState = stateStack.peek();
            Action action = grammarAnalysisTable.findAction(curState, curSign);

            switch (action.getActionType()) {
                //移入
                case SHIFT:
                    int shiftToState = ((ShiftAction) action).getShiftToState();
                    stateStack.push(shiftToState);
                    inputSigns.poll();
                    break;
                //规约
                case REDUCE:
                    Production reduceProduction = grammarAnalysisTable.getProduction(((ReduceAction) action).getReduceProduction());
                    LinkedList<Sign> right = reduceProduction.getRight();
                    //将r个状态符号弹出栈，r是该规约产生式的右部的长度
                    for (int i = 0; i < right.size(); i++) {
                        stateStack.pop();
                    }
                    curState = stateStack.peek();
                    //根据语法分析表找到下一个GOTO的状态
                    int gotoState = grammarAnalysisTable.findGotoState(curState, reduceProduction.getLeft());
                    stateStack.push(gotoState);

                    //向输出结果添加一条规约产生式
                    reduceProcedure.add(reduceProduction.toString());
                    break;
                //报错
                case ERROR:
                    assert false : "语法分析发现一个语法错误";
                    break;
            }
        }
        //输入队列的最后一个输入符为结束符
        assert inputSigns.size() == 1 && inputSigns.peek().equals(GOTOGraph.DOLLAR);

        //最后应该对$进行规约
        boolean canReduce = true;
        do {
            canReduce = false;


            TerminalSign curSign = inputSigns.peek();
            int curState = stateStack.peek();
            Action action = grammarAnalysisTable.findAction(curState, curSign);

            assert action.getActionType() == ActionType.REDUCE || action.getActionType() == ActionType.ACCEPT;

            switch (action.getActionType()) {
                //规约
                case REDUCE:
                    Production reduceProduction = grammarAnalysisTable.getProduction(((ReduceAction) action).getReduceProduction());
                    LinkedList<Sign> right = reduceProduction.getRight();
                    //将r个状态符号弹出栈，r是该规约产生式的右部的长度
                    for (int i = 0; i < right.size(); i++) {
                        stateStack.pop();
                    }
                    curState = stateStack.peek();
                    //根据语法分析表找到下一个GOTO的状态
                    int gotoState = grammarAnalysisTable.findGotoState(curState, reduceProduction.getLeft());
                    stateStack.push(gotoState);

                    //向输出结果添加一条规约产生式
                    reduceProcedure.add(reduceProduction.toString());
                    canReduce = true;
                    break;
                //接受
                case ACCEPT:
                    System.out.println("语法分析结束");
                    canReduce = false;
                    break;

                default:
                    assert false : ": 对于$不会存在移入操作, 输入文件不能被规约";
                    break;
            }
        } while (canReduce);

        //栈中还有两个状态，一个起始零状态，一个接受状态
        assert stateStack.size() == 2 : ": 语法分析栈中的状态大于两个";
        return reduceProcedure;
    }
}
