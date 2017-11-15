package cn.edu.nju;

import cn.edu.nju.analysisTable.GrammarAnalysisTable;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;

import java.util.Queue;

/**
 * 将所有功能集合起来：
 * 1.解析Yacc文件，生成产生式
 * 2.根据产生式构建GOTO函数
 * 3.构建LR语法分析表
 * 4.根据LR语法分析表生成语法分析器
 * 5.根据语法分析器解析输入的符号流
 */
public class MyYaccController {

    public void controll() {

        //Yacc文件解析器
        YaccFileParser yaccFileParser = new YaccFileParser();
        YaccFileInfo yaccFileInfo = yaccFileParser.parseYaccFile();

        //GOTO图的构建
        GOTOGraph gotoGraph = new GOTOGraph(yaccFileInfo);

        //语法分析表
        GrammarAnalysisTable grammarAnalysisTable = new GrammarAnalysisTable(gotoGraph);

        //源文件读取
        SrcFileReader srcFileReader = new SrcFileReader();
        Queue<TerminalSign> signs = srcFileReader.readSrcFile();

        //语法分析器
        LRGrammarParser lrGrammarParser = new LRGrammarParser(grammarAnalysisTable);
        lrGrammarParser.parseGrammar(signs);

    }


}
