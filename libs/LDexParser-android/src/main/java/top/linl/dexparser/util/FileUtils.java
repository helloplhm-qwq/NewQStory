package top.linl.dexparser.util;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class FileUtils {

    /**
     * 文件写入文本
     *
     * @param path     路径
     * @param content  内容
     * @param isAppend 是否追写 不是的话会覆盖
     */
    public static void writeTextToFile(String path, String content, boolean isAppend) {
        File file = new File(path);
        try {
            //先创建文件夹
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            //再创建文件 FileOutputStream会自动创建文件但是不能创建多级目录
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, isAppend), StandardCharsets.UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从文件读取对象
     */
    public static Object readFileObject(File path) throws Exception {
        if (path == null || !path.exists()) {
            if (path != null) {
                throw new IOException("path No exists(文件不存在) " + path.getAbsolutePath());
            } else {
                throw new IOException("Empty File object");
            }
        } else if (path.isDirectory()) {
            throw new IOException("Non-file type(这个File是文件夹而不是文件) :" + path.getAbsolutePath());
        }
        Object result;
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        Input input = new Input(new FileInputStream(path));
        result = kryo.readClassAndObject(input);
        input.close();
        return result;
    }

    public static void writeObjectToFile(String path, Object obj) throws IOException {
        writeObjectToFile(new File(path), obj);
    }

    public static Object readFiliObject(String path) throws Exception {
        return readFileObject(new File(path));
    }

    /**
     * 文件写入对象
     */
    public static void writeObjectToFile(File path, Object obj) throws IOException {
        try {
            if (!path.exists()) {
                if (!path.getParentFile().exists()) {
                    path.getParentFile().mkdirs();
                }
                if (!path.createNewFile()) {
                    return;
                }
            }
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            Output output = new Output(new FileOutputStream(path));
            kryo.writeClassAndObject(output, obj);
            output.close();
        } catch (IOException e) {
            //对象写入失败清空文件内容,防止下次写入读取出现问题
            writeTextToFile(path.getAbsolutePath(), "", false);
            throw e;
        }
    }


    /**
     * 计算文件大小
     * fileUrl：D:/download/山花遍野.jpg
     *
     * @return 1GB
     */
    public static String getSize(long size) {
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        try {
            // 格式化小数
            DecimalFormat df = new DecimalFormat("0.00");
            String resultSize;
            if (size / GB >= 1) {
                //如果当前Byte的值大于等于1GB
                resultSize = df.format(size / (float) GB) + "GB";
            } else if (size / MB >= 1) {
                //如果当前Byte的值大于等于1MB
                resultSize = df.format(size / (float) MB) + "MB";
            } else if (size / KB >= 1) {
                //如果当前Byte的值大于等于1KB
                resultSize = df.format(size / (float) KB) + "KB";
            } else {
                resultSize = size + "B";
            }
            return resultSize;
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] readAllByte(InputStream stream, int size) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            bos = new ByteArrayOutputStream(size);//输出容器
            in = new BufferedInputStream(stream);//输入容器
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }

}
