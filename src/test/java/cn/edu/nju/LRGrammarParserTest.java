package cn.edu.nju;

import cn.edu.nju.analysisTable.GrammarAnalysisTable;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Queue;

public class LRGrammarParserTest {

    File yaccFile;
    File srcFile;
    LRGrammarParser lrGrammarParser;

    @Before
    public void setUp() throws Exception {

        yaccFile = new File(Thread.currentThread().getContextClassLoader().getResource("yaccFile/firstTest.y").getPath());
        srcFile = new File(Thread.currentThread().getContextClassLoader().getResource("srcFile/input.txt").getPath());

        //Yacc文件解析器
        YaccFileParser yaccFileParser = new YaccFileParser();
        YaccFileInfo yaccFileInfo = yaccFileParser.getYaccFileInfo(yaccFile);

        //GOTO图的构建
        GOTOGraph gotoGraph = new GOTOGraph(yaccFileInfo);

        //语法分析表
        GrammarAnalysisTable grammarAnalysisTable = new GrammarAnalysisTable(gotoGraph);
        grammarAnalysisTable.printTable();
        //语法分析器
        lrGrammarParser = new LRGrammarParser(grammarAnalysisTable);

    }

    @Test
    public void parseGrammar() throws Exception {
        //源文件读取
        SrcFileReader srcFileReader = new SrcFileReader();
        Queue<TerminalSign> signs = srcFileReader.getSignSequence(srcFile);
        List<String> reduceProcedure = lrGrammarParser.parseGrammar(signs);
        System.out.println("**********************规约产生式************************");
        for (int i = 0; i < reduceProcedure.size(); i++) {
            System.out.println(reduceProcedure.get(i));
        }
    }

}