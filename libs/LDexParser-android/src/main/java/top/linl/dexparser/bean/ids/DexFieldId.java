package top.linl.dexparser.bean.ids;

public class DexFieldId extends BaseId {
    /**
     * 此字段的定义符的 type_ids 列表中的索引。此项必须是“类”类型，而不能是“数组”或“基元”类型。
     */
    public short class_idx;
    /**
     * 此字段的类型的 type_ids 列表中的索引
     */
    public short type_idx;

    /**
     * 此字段的名称的 string_ids 列表中的索引
     */
    public int name_idx;

    public DexFieldId() {

    }

    public DexFieldId(short class_idx, short type_idx, int name_idx) {
        this.class_idx = class_idx;
        this.type_idx = type_idx;
        this.name_idx = name_idx;
    }
}
