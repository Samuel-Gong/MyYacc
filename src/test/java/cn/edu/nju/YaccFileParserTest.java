package cn.edu.nju;

import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.sign.SignType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class YaccFileParserTest {

    YaccFileParser yaccFileParser;
    File lfile;

    @Before
    public void setUp() throws Exception {
        yaccFileParser = new YaccFileParser();
        lfile = new File(Thread.currentThread().getContextClassLoader().getResource("yaccFile/firstTest.y").getPath());
    }

    @Test
    public void getProductions() throws Exception {
        List<Production> productions = yaccFileParser.getYaccFileInfo(lfile).productions;
        Production firstProduction = productions.get(0);
        Assert.assertEquals('B', firstProduction.getLeft().getNonTerminalSign());
        Assert.assertEquals(1, firstProduction.getRight().size());
        Assert.assertEquals(SignType.NON_TERMINAL, firstProduction.getRight().getLast().getSignType());

        Production fifthProduction = productions.get(4);
        Assert.assertEquals('A', fifthProduction.getLeft().getNonTerminalSign());
        Assert.assertEquals(1, fifthProduction.getRight().size());
        Assert.assertEquals(SignType.TERMINAL, fifthProduction.getRight().getFirst().getSignType());
    }

}