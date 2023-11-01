package top.linl.dexparser.util;

/**
 * 将一组byte变成流的读取方法 每个字节只能被读一次 读完自动更新当前字节下标
 * 但是更改了流只能顺序读一次的特性
 * 类似于{@link Reader}的实现方式
 */
public class ByteReader {
    private final byte[] srcData;
    private int position = 0;

    public ByteReader(byte[] srcByte) {
        this.srcData = srcByte;
    }

    public void setStartPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public byte[] read(int length) {
        byte[] b = Utils.copyArrays(srcData, position, length);
        position += length;
        return b;
    }

    public int readUnsignedLeb128() {
        int result = 0;
        int count = 0;
        int cur;
        do {
            cur = srcData[position];
            cur &= 0xff;
            result |= (cur & 0x7f) << count * 7;
            count++;
            position++;
        } while ((cur & 0x80) == 128 && count < 5);
        return result;
    }
}
