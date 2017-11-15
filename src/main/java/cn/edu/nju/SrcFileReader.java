package cn.edu.nju;

import cn.edu.nju.entity.sign.TerminalSign;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

/**
 * 读取需要解析的文件，将文件内容转换为字符串
 */
public class SrcFileReader {

    public Queue<TerminalSign> readSrcFile() {
        Scanner scanner = new Scanner(System.in);
        //读入文件内容
        System.out.println("请输入你想解析的文件名（相对路径）:");
        //判断文件存在
        String fileName = scanner.next();

        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + fileName;
        File srcFile = new File(filePath);

        assert srcFile.exists() : "指定需要解析的文件不存在或路径不正确";

        return getSignSequence(srcFile);
    }

    public Queue<TerminalSign> getSignSequence(File srcFile) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Queue<TerminalSign> signsSequence = new ArrayDeque<>();

        String line = "";

        try {
            while ((line = br.readLine()) != null) {
                //去掉开始的空格和最后的空格
                line = line.trim();
                //若为空行，则跳过
                if (line.equals("")) continue;

                String signStrs[] = line.split(" ");
                for (int i = 0; i < signStrs.length; i++) {
                    String str = signStrs[i];

                    assert !isNonterminalSign(str) : ": 输入中不能含有非终结符";

                    signsSequence.add(new TerminalSign(str));
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

        return signsSequence;

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
