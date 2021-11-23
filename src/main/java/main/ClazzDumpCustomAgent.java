package main;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ClazzDumpCustomAgent implements ClassFileTransformer {

    /**
     * 导出文件目录根目录, 以 -d 参数指定
     */
    private String exportBaseDir = "/tmp/";

    /**
     * 是否创建多级目录, 以 -r 参数指定
     */
    private final boolean packageRecursive;

    public ClazzDumpCustomAgent(String exportBaseDir) {
        if (exportBaseDir != null) {
            this.exportBaseDir = exportBaseDir;
        }
        this.packageRecursive = true;
    }

    /**
     * 入口地址
     *
     * @param agentArgs agent参数
     * @param inst      inst参数
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("agentArgs: " + agentArgs);
        String exportDir = null;
        if (agentArgs != null) {
            if (agentArgs.contains(";")) {
                String[] args = agentArgs.split(";");
                for (String param1 : args) {
                    String[] kv = param1.split("=");
                    if ("-d".equalsIgnoreCase(kv[0])) {
                        exportDir = kv[1];
                    }
                }
            }
        }
        inst.addTransformer(new ClazzDumpCustomAgent(exportDir));
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (needExportClass(className)) {
            int lastSeparatorIndex = className.lastIndexOf("/") + 1;
            String fileName = className.substring(lastSeparatorIndex) + ".class";
            String exportDir = exportBaseDir;
            System.out.println("导出文件目录"+exportDir);
            if (packageRecursive) {
                exportDir += className.substring(0, lastSeparatorIndex);
            }
            exportClazzToFile(exportDir, fileName, classfileBuffer);
            System.out.println(className + " --> EXPORTED");
        }
        return classfileBuffer;
    }

    /**
     * 检测是否需要进行文件导出
     *
     * @param className class名,如 com.xx.abc.AooMock
     * @return y/n
     */
    private boolean needExportClass(String className) {
        String[] str = className.split("/");
        String packageName = str[0];
        List<String> list = new ArrayList();

        // todo list 这里改为需要导出的文件夹/文件名称
        String a = "a";
        String b = "b";
        String c = "c";
        String client = "client";
        String configs = "configs";
        String constants = "constants";
        String d = "d";
        String scripts = "scripts";
        String server = "server";
        String tools = "tools";
        String Test = "Test";
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(client);
        list.add(configs);
        list.add(constants);
        list.add(d);
        list.add(scripts);
        list.add(server);
        list.add(tools);
        list.add(Test);
        String callback = "callback";
        String com = "com";
        String config = "config";
        String mode = "mode";
        String okhttp3 = "okhttp3";
        String okio = "okio";
        String org = "org";
        String util = "util";
        list.add(callback);
        list.add(com);
        list.add(config);
        list.add(okhttp3);
        list.add(okio);
        list.add(org);
        list.add(util);
        list.add(mode);

        //写文件
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf2.format(new Date());

        // todo list 这里改为对应文件名称

        File file = new File("D:/file/" + dateString + ".txt");
        BufferedWriter out = null;
        String conent1 = "className is: " + className;
        String conent2 = "packageName is: " + packageName;
        try {
            if (!file.exists()) {
                //初次写入
                file.createNewFile();
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file)));
            } else {
                //追加
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
            }

            out.write(conent1 + "       " + conent2 + ";");
            out.write("\r\n");
            ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (list.contains(packageName)) {
            System.out.println("packageName is: " + packageName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 执行文件导出写入
     *
     * @param dirPath  导出目录
     * @param fileName 导出文件名
     * @param data     字节流
     */
    private void exportClazzToFile(String dirPath, String fileName, byte[] data) {
        try {
            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(dirPath + fileName);
            if (!file.exists()) {
                System.out.println(dirPath + fileName + " is not exist, creating...");
                file.createNewFile();
            } else {

//                String os = System.getProperty("os.name");        // 主要针对windows文件不区分大小写问题
//                if(os.toLowerCase().startsWith("win")){
//                    // it's win
//                }
                try {
                    int maxLoop = 9999;
                    int renameSuffixId = 2;
                    String[] cc = fileName.split("\\.");
                    do {
                        long fileLen = file.length();
                        byte[] fileContent = new byte[(int) fileLen];
                        FileInputStream in = new FileInputStream(file);
                        in.read(fileContent);
                        in.close();
                        if (!Arrays.equals(fileContent, data)) {
                            fileName = cc[0] + "_" + renameSuffixId + "." + cc[1];
                            file = new File(dirPath + fileName);
                            if (!file.exists()) {
                                System.out.println("new create file: " + dirPath + fileName);
                                file.createNewFile();
                                break;
                            }
                        } else {
                            break;
                        }
                        renameSuffixId++;
                        maxLoop--;
                    } while (maxLoop > 0);
                } catch (Exception e) {
                    System.err.println("exception in read class file..., path: " + dirPath + fileName);
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            System.err.println("exception occur while export class.");
            e.printStackTrace();
        }
    }
}