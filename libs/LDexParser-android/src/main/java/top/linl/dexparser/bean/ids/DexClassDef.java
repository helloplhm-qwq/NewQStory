package top.linl.dexparser.bean.ids;

public class DexClassDef extends BaseId {
    public int class_idx;
    public int access_flags;
    public int superclass_idx;
    public int interfaces_off;
    public int source_file_idx;
    public int annotations_off;
    public int class_data_off;
    public int static_values_off;

    public DexClassDef(int class_idx, int access_flags, int superclass_idx, int interfaces_off, int source_file_idx, int annotations_off, int class_data_off, int static_values_off) {
        this.class_idx = class_idx;
        this.access_flags = access_flags;
        this.superclass_idx = superclass_idx;
        this.interfaces_off = interfaces_off;
        this.source_file_idx = source_file_idx;
        this.annotations_off = annotations_off;
        this.class_data_off = class_data_off;
        this.static_values_off = static_values_off;
    }
}
