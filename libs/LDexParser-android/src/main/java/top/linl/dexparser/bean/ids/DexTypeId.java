package top.linl.dexparser.bean.ids;

import top.linl.dexparser.DexParser;

public class DexTypeId extends BaseId {
    /**
     * 此类描述符字符串的 string_ids 列表中的索引。该字符串必须符合上文定义的 TypeDescriptor 的语法。
     * uint
     */
    public int descriptor_idx;

    @Deprecated
    public String string_data;

    public DexTypeId() {

    }

    public DexTypeId(int descriptor_idx) {
        this.descriptor_idx = descriptor_idx;
    }

    public String getString(DexParser parser) {
        DexStringId dexStringId = parser.dexStringIdsList[descriptor_idx];
        return dexStringId.getString(parser);
    }

    public String toString() {
        return string_data;
    }
}
