package cn.edu.nju.fileUtil;

import cn.edu.nju.entity.ReductionInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ReductionsWriter {

    public void writeReductionInfo(ReductionInfo reductionInfo) {
        Scanner scanner = new Scanner(System.in);
        //读入文件内容
        System.out.println("请输入你想写入归约序列的文件名（相对路径）:");
        //判断文件存在
        String fileName = scanner.next();

        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + fileName;
        File destFile = new File(filePath);
        if (!destFile.exists()) try {
            destFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert destFile.exists();

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(destFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bw.write(reductionInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
