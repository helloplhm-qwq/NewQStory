package top.linl.dexparser.bean.ids;

import top.linl.dexparser.DexParser;
import top.linl.dexparser.util.Utils;

/**
 * @author suzhelan
 * time 2023.9.5
 */
public class DexStringId extends BaseId {

    /**
     * 字符串
     *
     * @deprecated 字段已弃用 因为在查找体量达到8MB左右时内存占用会高且不会被回收造成堆溢出 改用 {@link #getString(DexParser)}
     */
    @Deprecated
    public String string_data;
    /**
     * 字节长度
     */
    public int string_byte_length;
    /**
     * 索引偏移
     * uint
     */
    public int string_data_off;

    @Deprecated
    public DexStringId(int string_data_off, String string_data) {
        this.string_data = string_data;
        this.string_data_off = string_data_off;
    }

    public DexStringId(int string_data_off, int string_byte_length) {
        this.string_data_off = string_data_off;
        this.string_byte_length = string_byte_length;
    }

    public DexStringId() {

    }

    public String getString(DexParser parser) {
        if (string_byte_length == 0 || string_data != null) return string_data;
        byte[] string_data = Utils.copyArrays(parser.dexData, string_data_off + 1, string_byte_length);
        return new String(string_data);
    }

    @Override
    public String toString() {
        return string_data;
    }
}
