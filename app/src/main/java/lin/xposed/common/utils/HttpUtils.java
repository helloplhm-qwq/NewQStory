package lin.xposed.common.utils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {


    public static File fileDownload(String urlStr, String downloadDir) {
        File file = new File(downloadDir);
        if (!file.exists()) {
            //如果文件夹不存在，则创建新的的文件夹
            file.getParentFile().mkdirs();
        }
        try {
            // 统一资源
            URL url = new URL(urlStr);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection conn = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            conn.setRequestMethod("GET");
            // 设置字符编码
            conn.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            conn.connect();
            //打开缓冲输入流
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
            BufferedOutputStream out =new BufferedOutputStream( new FileOutputStream(file));
            int size;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            out.flush();
            out.close();
            bin.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString;
            if (param != null && !param.equals("")) urlNameString = url + "?" + param;
            else urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
            result.delete(result.length() - 1, result.length());
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String sendPost(String url, JSONObject param) throws Exception {
        BufferedOutputStream out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        StringBuilder params = new StringBuilder();
        Iterator<String> iterator = param.keys();
        while (iterator.hasNext()) {
            if (param.length() > 0) params.append("&");
            String key = iterator.next();
            params.append(key).append("=").append(param.get(key));
        }
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new BufferedOutputStream(conn.getOutputStream());
            // 发送请求参数
            out.write(params.toString().getBytes());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
            if (result.length() > 0) result.delete(result.length() - 1, result.length());
        } finally {
            //使用finally块来关闭输出流、输入流finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
            result.delete(result.length() - 1, result.length());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 可传入多张图片和参数
     *
     * @param actionUrl 发送地址
     * @param params    文本参数 Key : Value
     * @param files     文件参数 文件名 : File对象
     * @return 服务器响应
     * @throws IOException
     */
    public static String sendFileList(String actionUrl, Map<String, String> params, Map<String, File> files) throws IOException {
        StringBuilder result = new StringBuilder();

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", end = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(end);
            sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(end);
            sb.append("Content-Type: text/plain; charset=").append(CHARSET).append(end);
            sb.append("Content-Transfer-Encoding: 8bit").append(end);
            sb.append(end);
            sb.append(entry.getValue());
            sb.append(end);
        }
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        //发送文本参数
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> file : files.entrySet()) {
                String fileInfo = PREFIX + BOUNDARY + end + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + end + "Content-Type: application/octet-stream; charset=" + CHARSET + end + end;
                System.out.println(fileInfo);
                outStream.write(fileInfo.getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                //发送结束再追加个结束符
                outStream.write(end.getBytes());
            }
        }
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + end).getBytes();
        outStream.write(end_data);
        outStream.flush();
        outStream.close();
        //打开服务器响应流
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String text;
        while ((text = br.readLine()) != null) {
            result.append(text).append("\n");
        }
        br.close();
        isr.close();
        is.close();
        conn.disconnect();
        result.delete(result.length() - 1, result.length());
        return result.toString();
    }
}

