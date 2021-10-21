package com.teamwork.util;

import java.io.*;
import java.util.ArrayList;

/**
 * @Author: Lsutin
 * @Date: 2021/10/21 20:27
 * @describe:
 */
public class FileUtil {

    /**
     * 写文件
     * @param fileName
     */
    public static void writeFile(String fileName, ArrayList<String> arrayList){
        File file = new File(fileName);
        BufferedWriter stream=null;
        if (!file.isFile()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            stream = new BufferedWriter(new FileWriter(file));
            for (String  s:arrayList){
                stream.write(s);
                stream.newLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(stream);
        }
    }

    public static void close(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写文件
     * @param fileName
     * @param outputStream
     * @throws FileNotFoundException
     */
    public static void writeFile(String fileName,OutputStream outputStream) throws FileNotFoundException {
        File file = new File(fileName);
        byte[] buffer = new byte[1024 * 10];
        InputStream inputStream=null;
        int len=0;
        try {
            inputStream=new BufferedInputStream(new FileInputStream(file));
            while ((len=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("文件："+fileName+" 不存在");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(inputStream);
            close(outputStream);
            if (file.exists()){
                file.delete();
            }
        }
    }

    public static ArrayList<String> readFile(InputStream stream){
        BufferedReader reader = null;
        String s=null;
        ArrayList<String> list = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            while ((s=reader.readLine())!=null){
                list.add(s.trim());
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            close(reader);
            close(stream);
        }
    }
}
