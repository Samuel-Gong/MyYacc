package cn.edu.nju;

import cn.edu.nju.analysisTable.GrammarAnalysisTable;
import cn.edu.nju.entity.ReductionInfo;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.fileUtil.SrcFileReader;
import cn.edu.nju.fileUtil.YaccFileParser;
import cn.edu.nju.gotoGraph.GOTOGraph;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class LRGrammarParserTest {

    File yaccFile;
    File srcFile;
    LRGrammarParser lrGrammarParser;

    @Before
    public void setUp() throws Exception {

        yaccFile = new File(Thread.currentThread().getContextClassLoader().getResource("yaccFile/firstTest.y").getPath());
        srcFile = new File(Thread.currentThread().getContextClassLoader().getResource("srcFile/errorInput.txt").getPath());

        //Yacc文件解析器
        YaccFileParser yaccFileParser = new YaccFileParser();
        YaccFileInfo yaccFileInfo = yaccFileParser.getYaccFileInfo(yaccFile);

        //GOTO图的构建
        GOTOGraph gotoGraph = new GOTOGraph(yaccFileInfo);

        //语法分析表
        GrammarAnalysisTable grammarAnalysisTable = new GrammarAnalysisTable(gotoGraph);
        System.out.println(grammarAnalysisTable.toString());
        //语法分析器
        lrGrammarParser = new LRGrammarParser(grammarAnalysisTable);

    }

    @Test
    public void parseGrammar() throws Exception {
        //源文件读取
        SrcFileReader srcFileReader = new SrcFileReader();
        List<TerminalSign> signs = srcFileReader.getSignSequence(srcFile);
        ReductionInfo reductionInfo = lrGrammarParser.parseGrammar(signs);
        System.out.println(reductionInfo.toString());
    }

}