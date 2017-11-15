package cn.edu.nju.gotoGraph;

import cn.edu.nju.YaccFileParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class GOTOGraphTest {

    GOTOGraph gotoGraph;
    YaccFileParser yaccFileParser;
    File lfile;

    @Before
    public void setUp() throws Exception {
        yaccFileParser = new YaccFileParser();
        lfile = new File(Thread.currentThread().getContextClassLoader().getResource("yaccFile/graphTest.y").getPath());
        gotoGraph = new GOTOGraph(yaccFileParser.getYaccFileInfo(lfile));
    }

    @Test
    public void items() throws Exception {
        gotoGraph.items();
    }

}