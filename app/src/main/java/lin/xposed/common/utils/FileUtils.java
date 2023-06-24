package lin.xposed.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static final int BYTE_SIZE = 1024;

    /**
     * 复制文件夹
     *
     * @param sourceDir 原文件夹
     * @param targetDir 复制后的文件夹
     */
    public static void copyDir(File sourceDir, File targetDir) {
        if (!sourceDir.isDirectory()) {
            return;
        }
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File[] files = sourceDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                copyDir(f, new File(targetDir.getPath(), f.getName()));
            } else if (f.isFile()) {
                try {
                    copyFile(f, new File(targetDir.getPath(), f.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) file.delete();
        File[] files = file.listFiles();
        if (files == null) return;
        //遍历该目录下的文件对象
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);//目录下有文件夹调用本方法删除(递归)
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                if (children == null) return 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                return file.length();
            }
        } else {
            return 0;
        }
    }


    public static String readFileText(String filePath) throws IOException {
        File path = new File(filePath);
        if (!path.exists()) {
            throw new IOException("path No exists :" + path.getAbsolutePath());
        } else if (path.isDirectory()) {
            throw new IOException("Non-file type :" + path.getAbsolutePath());
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        }
        if (stringBuilder.length() >= 1)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

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

    public static void writeObjectToFile(String path, Object obj) throws IOException {
        writeObjectToFile(new File(path), obj);
    }

    /**
     * 文件写入对象
     */
    public static void writeObjectToFile(File path, Object obj) throws IOException {
        writeTextToFile(path.getAbsolutePath(), "", false);
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            if (!path.exists()) {
                if (!path.getParentFile().exists()) {
                    path.getParentFile().mkdirs();
                }
                if (!path.createNewFile()) {
                    return;
                }
            }
            fileOutputStream = new FileOutputStream(path);
            outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(obj);
        } catch (IOException e) {
            //对象写入失败清空文件内容,防止下次写入读取出现问题
            writeTextToFile(path.getAbsolutePath(), "", false);
            throw e;
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException ignored) {

            }
        }
    }

    public static Object readFiliObject(String path) throws Exception {
        return readFileObject(new File(path));
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
        FileInputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        Object results;
        try {
            inputStream = new FileInputStream(path);
            objectInputStream = new ObjectInputStream(inputStream);
            results = objectInputStream.readObject();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return results;
    }

    public static void copyFile(String sourceFile, String targetPath) throws IOException {
        File file = new File(sourceFile);
        if (!file.exists()) {
            throw new IOException("path No exists(源文件不存在) : " + file.getAbsolutePath());
        } else if (file.isDirectory()) {
            throw new IOException("Not a file, but a directory(不是文件) : " + file.getAbsolutePath());
        }
        copyFile(new FileInputStream(file), new File(targetPath));
    }

    public static void copyFile(File sourceFile, File target) throws IOException {
        copyFile(new FileInputStream(sourceFile), target);
    }

    public static void copyFile(InputStream inputStream, File target) throws IOException {
        if (target == null) {
            throw new IOException("targetFile , Empty File object");
        }
        if (!target.exists()) {
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }
            if (!target.createNewFile()) {
                throw new IOException("create File Fail :" + target.getAbsolutePath());
            }
        }
        try (
                inputStream;
                BufferedInputStream sourceFile = new BufferedInputStream(inputStream);
                BufferedOutputStream destStream = new BufferedOutputStream(new FileOutputStream(target))
        ) {
            byte[] bytes = new byte[BYTE_SIZE];
            while ((sourceFile.read(bytes) != -1)) {
                destStream.write(bytes);
            }
        }
    }

}
