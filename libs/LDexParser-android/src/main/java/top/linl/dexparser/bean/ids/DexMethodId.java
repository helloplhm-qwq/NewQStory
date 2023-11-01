package top.linl.dexparser.bean.ids;

import java.util.ArrayList;
import java.util.List;

import top.linl.dexparser.DexParser;
import top.linl.dexparser.util.ConversionUtils;
import top.linl.dexparser.util.Utils;

public class DexMethodId extends BaseId {
    /**
     * 此方法的定义符的 type_ids 列表中的索引。此项必须是“类”或“数组”类型，而不能是“基元”类型。
     */
    public short class_ids;

    /**
     * 此方法的原型的 proto_ids 列表中的索引
     */
    public short proto_idx;

    /**
     * 此方法的名称的 string_ids 列表中的索引。
     */
    public int name_idx;
    /**
     * 方法使用的字符串列表 指向string_ids
     */
    private List<Integer> usedStringList;

    public DexMethodId() {

    }

    public DexMethodId(short class_ids, short proto_idx, int name_idx) {
        this.class_ids = class_ids;
        this.proto_idx = proto_idx;
        this.name_idx = name_idx;
    }

    public DexTypeId getReturnType(DexParser dexParser) {
        DexProtoId dexProtoId = dexParser.dexDexProtoIdsList[proto_idx];
        return dexParser.dexTypeIdsList[dexProtoId.return_type_idx];
    }

    public DexTypeId[] getMethodParams(DexParser dexParser) {
        DexProtoId dexProtoId = dexParser.dexDexProtoIdsList[proto_idx];
        if (dexProtoId.parameters_off == 0) return new DexTypeId[0];
        //4字节uint对齐
        int size = ConversionUtils.byteToUnsignedInt(Utils.copyArrays(dexParser.dexData, dexProtoId.parameters_off, 4));
        DexTypeId[] paramsList = new DexTypeId[size];
        for (int i = 0; i < size; i++) {
            int typeIdx = ConversionUtils.bytesToUnsignedShort(Utils.copyArrays(dexParser.dexData, dexProtoId.parameters_off + i * 2 + 4, 2));
            paramsList[i] = dexParser.dexTypeIdsList[typeIdx];
        }
        return paramsList;
    }

    /**
     * @return 获得方法使用的字符串列表
     */
    public List<Integer> getUsedStringList() {
        return usedStringList;
    }

    public void initUsedStringList() {
        this.usedStringList = new ArrayList<>();
    }

}
