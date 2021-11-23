package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class readTxt {

    public static List<String> ReadExportPackname() {
        ArrayList<String> list = new ArrayList<String>();
        String path = "D:\\java_pro\\javaagent\\src\\main\\java\\data\\needExportClass.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),
                    "UTF-8"));
            String lineTxt;
            while ((lineTxt = br.readLine()) != null) {
                String[] names = lineTxt.split("\r\n");
                list.addAll(Arrays.asList(names));
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        return list;
    }
}