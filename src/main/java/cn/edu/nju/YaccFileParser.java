package cn.edu.nju;

import cn.edu.nju.entity.Production;
import cn.edu.nju.entity.YaccFileInfo;
import cn.edu.nju.entity.sign.NonTerminalSign;
import cn.edu.nju.entity.sign.Sign;
import cn.edu.nju.entity.sign.SignType;
import cn.edu.nju.entity.sign.TerminalSign;
import cn.edu.nju.gotoGraph.GOTOGraph;

import java.io.*;
import java.util.*;

/**
 * Yacc文件的解析器，将结合性、优先级、产生式的信息
 */
public class YaccFileParser {

    public YaccFileInfo parseYaccFile() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入需要读取的.y文件的文件名（相对路径）:");

        //判断文件存在
        String fileName = scanner.next();

        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + fileName;
        File lFile = new File(filePath);

        assert lFile.exists() : "指定.y文件不存在";
        assert lFile.getName().endsWith(".y") : "文件不是.y文件";

        return getYaccFileInfo(lFile);
    }

    /**
     * 获取文件中所有的产生式,非终结符集合，终结符集合
     *
     * @return .y文件信息实体
     */
    public YaccFileInfo getYaccFileInfo(File lFile) {

        List<Production> productions = new ArrayList<>();
        Set<NonTerminalSign> nonTerminalSigns = new HashSet<>();
        Set<TerminalSign> terminalSigns = new HashSet<>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(lFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;

        NonTerminalSign lastNonTerminalSign = null;
        try {
            while ((line = br.readLine()) != null) {
                //若为空行，则跳过
                if (line.equals("")) continue;
                //去掉开始的空格和最后的空格
                line = line.trim();
                //开始是|，则为右部
                if (line.startsWith("|")) {
                    String right = line.substring(1).trim();
                    assert lastNonTerminalSign != null : ": .y文件不符合标准";

                    //右部为epsilon
                    if (right.length() == 0) {
                        LinkedList<Sign> rightSequence = new LinkedList<>();
                        rightSequence.add(GOTOGraph.EPSILON);
                        productions.add(new Production(lastNonTerminalSign, rightSequence));
                    }
                    //添加新的产生式
                    else productions.add(new Production(lastNonTerminalSign, parseRight(right)));
                }
                //左部和右部
                else {
                    int index = line.indexOf(":");
                    assert index > 0 : ": 左部和右部之间应该用:分开";
                    String left = line.substring(0, index).trim();
                    assert left.length() == 1 : "：非终结符的长度不为1";
                    assert isNonterminalSign(left) : ": 左部符号不属于非终结符";
                    NonTerminalSign leftSign = new NonTerminalSign(left.charAt(0));

                    String right = line.substring(index + 1).trim();
                    //添加新的产生式
                    productions.add(new Production(leftSign, parseRight(right)));

                    //更新上一个非终结符
                    lastNonTerminalSign = leftSign;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //添加非终结符集合
        for (int i = 0; i < productions.size(); i++) {
            nonTerminalSigns.add(productions.get(i).getLeft());
        }
        //添加终结符集合
        for (Production production : productions) {
            LinkedList<Sign> right = production.getRight();
            for (int i = 0; i < right.size(); i++) {
                Sign sign = right.get(i);
                if (sign.getSignType() == SignType.NON_TERMINAL) {
                    assert nonTerminalSigns.contains(sign)
                            : ": .y中的文法不是上下文无关文法，非终结符" + ((NonTerminalSign) sign).getNonTerminalSign()
                            + "未出现在产生式左部";
                } else terminalSigns.add((TerminalSign) sign);
            }
        }

        //移除epsilon
        terminalSigns.remove(GOTOGraph.EPSILON);

        return new YaccFileInfo(productions, nonTerminalSigns, terminalSigns);
    }

    /**
     * 将产生式右部字符串解析为产生式右部的符号链表
     *
     * @param right 右部字符串
     * @return
     */
    private LinkedList<Sign> parseRight(String right) {
        String rightSequence[] = right.split(" ");
        LinkedList<Sign> rightList = new LinkedList<>();
        for (int i = 0; i < rightSequence.length; i++) {
            String sign = rightSequence[i];
            if (isNonterminalSign(sign)) {
                assert sign.length() == 1 : "非终结符的长度不为1";
                NonTerminalSign nonTerminalSign = new NonTerminalSign(sign.charAt(0));
                rightList.add(nonTerminalSign);
            } else {
                assert rightList.indexOf("|") < 0 : ": 产生式右部中不能含有|";
                rightList.add(new TerminalSign(sign));
            }
        }
        return rightList;
    }

    /**
     * 判断是否是非终结符
     *
     * @param sign 需要判断的文法符号
     * @return
     */
    private boolean isNonterminalSign(String sign) {
        if (sign.length() > 1) return false;
        char c = sign.charAt(0);
        assert c != 'Z' : ": 非终结符不能为Z，Z已经被定义为增广文法中0号产生式的左部";
        return Character.isUpperCase(c);
    }
}
