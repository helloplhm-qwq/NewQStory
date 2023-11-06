package top.linl.dexparser.util;

import java.io.IOException;
import java.io.InputStream;

public class Reader {
    private final InputStream inputStream;

    public Reader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 从数据流读出数据
     *
     * @param length 长度
     * @return 数据字节
     */
    public byte[] read(int length) throws IOException {
        byte[] result = new byte[length];
        int b = inputStream.read(result);
        if (b == -1) throw new IOException("read -1");
        else return result;
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
