package top.linl.dexparser.bean;

import java.nio.charset.StandardCharsets;

import top.linl.dexparser.util.ByteReader;
import top.linl.dexparser.util.ConversionUtils;

/**
 * @author suzhelan
 * time 2023.9.25
 * dex文件头格式 参照 <a href="https://source.android.google.cn/docs/core/runtime/dex-format?hl=zh-cn#items">...</a>
 * <p>
 * 字段doc用处与格式 如未标记则该项为4字节对齐的uint
 */
public class DexHeader {
    /**
     * ubyte[8] = DEX_FILE_MAGIC
     */
    public String magic;
    /**
     * 文件剩余内容（除 magic 和此字段之外的所有内容）的 adler32 校验和；用于检测文件损坏情况
     */
    public long checkSum;

    /**
     * 文件剩余内容（除 magic、checksum 和此字段之外的所有内容）的 SHA-1 签名（哈希）；用于对文件进行唯一标识
     * ubyte[20]
     */
    public String signature;

    /**
     * 整个文件大小
     */
    public int file_size;//整个文件的大小

    /**
     * 头文件（整个区段）的大小，以字节为单位。此项允许至少一定程度的向后/向前兼容性，而不会使格式失效。
     * uint = 0x70
     */
    public int header_size;

    /**
     * 字节序标记
     * uint = ENDIAN_CONSTANT
     */
    public int endian_tag;

    /**
     * 链接区段的大小；如果此文件未进行静态链接，则该值为 0
     */
    public int link_size;

    /**
     * 从文件开头到链接区段的偏移量
     */
    public int link_off;

    /**
     * 从文件开头到映射项的偏移量。该偏移量（必须为非零值）应该是到 data 区段的偏移量，而数据应采用下文中“map_list”指定的格式。
     */
    public int map_off;

    /**
     * 字符串常量数量
     */
    public int string_ids_size;

    /**
     * 从文件头到字符区偏移量
     */
    public int string_ids_off;

    /**
     * 类型标识符列表中的元素数量，最多为 65535
     */
    public int type_ids_size;

    /**
     * 从文件开头到类型标识符列表的偏移量
     */
    public int type_ids_off;

    /**
     * 原型标识符数量
     */
    public int proto_ids_size;

    /**
     * 从文件到proto区的偏移量
     */
    public int proto_ids_off;

    /**
     * 字段数量
     */
    public int field_ids_size;

    /**
     * 从文件头到字段区的偏移量
     */
    public int field_ids_off;

    /**
     * 方法数量
     */
    public int method_ids_size;

    /**
     * 从文件头到方法区的偏移量
     */
    public int method_ids_off;

    /**
     * 类定义列表中的元素数量
     */
    public int class_defs_size;

    /**
     * 从文件开头到类定义列表的偏移量
     */
    public int class_defs_off;

    /**
     * data 区段的大小（以字节为单位）。该数值必须是 sizeof(uint) 的偶数倍
     */
    public int data_size;

    /**
     * 从文件开头到 data 区段开头的偏移量。
     */
    public int data_off;

    public DexHeader(ByteReader reader) {
        this.magic = new String(reader.read(8), StandardCharsets.UTF_8);

        this.checkSum = ConversionUtils.byte2Int(reader.read(4));

        this.signature = ConversionUtils.bytesToHex(reader.read(20));

        this.file_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.header_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.endian_tag = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.link_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.link_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.map_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.string_ids_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.string_ids_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.type_ids_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.type_ids_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.proto_ids_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.proto_ids_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.field_ids_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.field_ids_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.method_ids_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.method_ids_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.class_defs_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.class_defs_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.data_size = ConversionUtils.byteToUnsignedInt(reader.read(4));

        this.data_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
    }

    @Override
    public String toString() {
        return "DexHeader{" + "magic='" + magic + '\'' + ", checkSum=" + checkSum + ", signature='" + signature + '\'' + ", file_size=" + file_size + ", header_size=" + header_size + ", endian_tag=" + endian_tag + ", link_size=" + link_size + ", link_off=" + link_off + ", map_off=" + map_off + ", string_ids_size=" + string_ids_size + ", string_ids_off=" + string_ids_off + ", type_ids_size=" + type_ids_size + ", type_ids_off=" + type_ids_off + ", proto_ids_size=" + proto_ids_size + ", proto_ids_off=" + proto_ids_off + ", field_ids_size=" + field_ids_size + ", field_ids_off=" + field_ids_off + ", method_ids_size=" + method_ids_size + ", method_ids_off=" + method_ids_off + ", class_defs_size=" + class_defs_size + ", class_defs_off=" + class_defs_off + ", data_size=" + data_size + ", data_off=" + data_off + '}';
    }
}
