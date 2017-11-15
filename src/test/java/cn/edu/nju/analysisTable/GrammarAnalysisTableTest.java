package cn.edu.nju.analysisTable;

import cn.edu.nju.YaccFileParser;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.gotoGraph.GOTOGraph;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class GrammarAnalysisTableTest {
    //语法分析表
    GrammarAnalysisTable grammarAnalysisTable;
    File file;

    @Before
    public void setUp() throws Exception {
        //Yacc文件解析器
        YaccFileParser yaccFileParser = new YaccFileParser();
        file = new File(Thread.currentThread().getContextClassLoader().getResource("yaccFile/firstTest.y").getPath());
        YaccFileInfo yaccFileInfo = yaccFileParser.getYaccFileInfo(file);

        //GOTO图的构建
        GOTOGraph gotoGraph = new GOTOGraph(yaccFileInfo);

        grammarAnalysisTable = new GrammarAnalysisTable(gotoGraph);
    }

    @Test
    public void printTable() throws Exception {

        grammarAnalysisTable.printTable();

    }

}