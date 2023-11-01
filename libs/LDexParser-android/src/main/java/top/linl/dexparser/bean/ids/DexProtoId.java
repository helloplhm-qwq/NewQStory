package top.linl.dexparser.bean.ids;

public class DexProtoId extends BaseId {
    int shorty_idx;
    int return_type_idx;
    int parameters_off;

    public DexProtoId() {

    }

    public DexProtoId(int shorty_idx, int return_type_idx, int parameters_off) {
        this.shorty_idx = shorty_idx;
        this.return_type_idx = return_type_idx;
        this.parameters_off = parameters_off;
    }
}
