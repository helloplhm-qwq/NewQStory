package top.linl.dexparser;

import top.linl.dexparser.bean.DexHeader;
import top.linl.dexparser.bean.DexMap;
import top.linl.dexparser.bean.ids.DexClassDef;
import top.linl.dexparser.bean.ids.DexFieldId;
import top.linl.dexparser.bean.ids.DexMethodId;
import top.linl.dexparser.bean.ids.DexProtoId;
import top.linl.dexparser.bean.ids.DexStringId;
import top.linl.dexparser.bean.ids.DexTypeId;
import top.linl.dexparser.util.ByteReader;
import top.linl.dexparser.util.ConversionUtils;

public class DexParser {
    /**
     * 文件头
     */
    public DexHeader dexHeader;
    /**
     * 该dex的所有映射列表
     */
    public DexMap dexMap;
    /**
     * 字符串常量
     */
    public DexStringId[] dexStringIdsList;
    /**
     * 类型
     */
    public DexTypeId[] dexTypeIdsList;
    /**
     * 方法签名
     */
    public DexProtoId[] dexDexProtoIdsList;
    /**
     * 字段
     */
    public DexFieldId[] dexFieldIdsList;
    /**
     * 方法
     */
    public DexMethodId[] dexMethodIdsList;
    public DexClassDef[] dexClassDefList;
    public byte[] dexData;
    private String dexName;
    private ByteReader reader;

    public DexParser(byte[] dexData, String dexName) {
        this.dexData = dexData;
        this.setDexName(dexName);
        reader = new ByteReader(dexData);
        //解析文件头
        dexHeader = new DexHeader(reader);
        dexStringIdsList = new DexStringId[dexHeader.string_ids_size];
        dexTypeIdsList = new DexTypeId[dexHeader.type_ids_size];
        dexDexProtoIdsList = new DexProtoId[dexHeader.proto_ids_size];
        dexFieldIdsList = new DexFieldId[dexHeader.field_ids_size];
        dexMethodIdsList = new DexMethodId[dexHeader.method_ids_size];
        dexClassDefList = new DexClassDef[dexHeader.class_defs_size];

    }

    public DexParser() {

    }

    /**
     * 将16进制的int分析成数组
     */
    public static int[] SplitHexInt(int decnum) {
        int[] result = new int[2];
        if (decnum < 0x0100) {
            result[1] = decnum;
            return result;
        }
        result[0] = decnum / 256;
        result[1] = decnum % 256;
        return result;
    }

    /**
     * 排除常用类
     */
    public static boolean isCommonlyUsedClass(String name) {
        return name.startsWith("Ljava") || name.startsWith("Landroid") || name.startsWith("Lkotlin") || name.startsWith("Lcom/android") || name.startsWith("Lcom/google") || name.startsWith("Lcom/microsoft") || name.startsWith("Ldalvik");
    }

    public void setDexData(byte[] dexData) {
        this.dexData = dexData;
    }

    /**
     * 解析完使用完必须释放 否则内存占用会很高 当dex数量达到20个以上时这是不能接受的
     */
    public void closeDexData() {
        this.dexData = null;
        System.gc();
    }

    public void startParse() {
        //然后解析基本常量
        //映射表
        parseMap();
        //字符串常量
        parseStringConstantPools();
        //type
        parseType();
        //方法签名
        parseProto();
        //字段
        parseField();
        //方法
        parseMethod();
        //class_def
        parseClassDef();

        //释放不再使用的资源以节省内存
        dexHeader = null;
        dexMap = null;
        reader = null;

//        closeDexData();

    }

    private void parseMap() {
        dexMap = new DexMap();
        ByteReader byteReader = new ByteReader(dexData);
        byteReader.setStartPosition(dexHeader.map_off);
        int size = ConversionUtils.byteToUnsignedInt(byteReader.read(4));
        for (int i = 0; i < size; i++) {
            int type = ConversionUtils.bytesToUnsignedShort(byteReader.read(2));
            int unused = ConversionUtils.bytesToUnsignedShort(byteReader.read(2));
            int item_size = ConversionUtils.byteToUnsignedInt(byteReader.read(4));
            int off = ConversionUtils.byteToUnsignedInt(byteReader.read(4));
            dexMap.addItem(type, item_size, off);
        }
    }

    /**
     * Parse the string constant pool first
     */
    private void parseStringConstantPools() {
        DexMap.Item item = dexMap.findItem(DexMap.TYPE_STRING_ID_ITEM);
        reader.setStartPosition(item.offset);//设置读取的偏移量到字符串 不设置也行 因为读取完dexHeader也是默认流读到了string_ids_off
        for (int i = 0; i < item.size; i++) {
            //该字符串偏移量
            int string_data_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
            /*
             * 此字符串的大小；以 UTF-16 代码单元（在许多系统中为“字符串长度”）为单位。
             * 也就是说，这是该字符串的解码长度（编码长度隐含在 0 字节的位置）string_data_off
             * */
            int utf16_size = ConversionUtils.readULeb128(dexData, string_data_off);//第一位是字符串长度 是解码后的长度 不是字节数组长度
            //算出字符串真实字节长度
            int string_byte_length = 0;
            while (dexData[++string_byte_length + string_data_off + 1] != '\0')
                ;//MUTF-8中不可能会出现字节/字符为0 如果为0说明该字符已经结束

//            byte[] string_data = Utils.copyArrays(dexData, string_data_off + 1, string_byte_length);
//            String string = new String(string_data);

            DexStringId dexStringId = new DexStringId(string_data_off, string_byte_length);
            dexStringIdsList[i] = dexStringId;
//            System.out.println("String: " + i + " " + dexStringId.getString(this));
        }
    }

    private void parseType() {
        for (int i = 0; i < dexHeader.type_ids_size; i++) {
            int descriptor_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            DexTypeId dexTypeId = new DexTypeId(descriptor_idx);
            dexTypeIdsList[i] = dexTypeId;
        }
    }

    private void parseProto() {
        for (int i = 0; i < dexHeader.proto_ids_size; i++) {
            int shorty_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int return_type_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int parameters_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
            /*//参数列表偏移量不为0则指向参数列表
            if (parameters_off != 0) {
                int size = ConversionUtils.byteToUnsignedInt(Utils.copyArrays(dexData, parameters_off, 4));
                for (int j = 0; j < size; j++) {
                    //指向typeids
                    int typeIdx = ConversionUtils.bytesToUnsignedShort(Utils.copyArrays(dexData, parameters_off + i * 2 + 4, 2));
                }
            }*/
            DexProtoId dexProtoId = new DexProtoId(shorty_idx, return_type_idx, parameters_off);
            dexDexProtoIdsList[i] = dexProtoId;
        }
    }

    private void parseField() {
        DexMap.Item field_item = dexMap.findItem(DexMap.TYPE_FIELD_ID_ITEM);
        reader.setStartPosition(field_item.offset);
        for (int i = 0; i < field_item.size; i++) {
            short class_idx = (short) ConversionUtils.bytesToUnsignedShort(reader.read(2));
            short type_idx = (short) ConversionUtils.bytesToUnsignedShort(reader.read(2));
            int name_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            DexFieldId dexFieldId = new DexFieldId(class_idx, type_idx, name_idx);
            dexFieldIdsList[i] = dexFieldId;
        }
    }

    private void parseMethod() {
        DexMap.Item method_item = dexMap.findItem(DexMap.TYPE_METHOD_ID_ITEM);
        reader.setStartPosition(method_item.offset);
        for (int i = 0; i < method_item.size; i++) {
            short class_idx = (short) ConversionUtils.bytesToUnsignedShort(reader.read(2));
            short proto_idx = (short) ConversionUtils.bytesToUnsignedShort(reader.read(2));
            int name_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            DexMethodId dexMethodId = new DexMethodId(class_idx, proto_idx, name_idx);
            dexMethodIdsList[i] = dexMethodId;
        }
    }

    private void parseClassDef() {
        DexMap.Item class_def_item = dexMap.findItem(DexMap.TYPE_CLASS_DEF_ITEM);
        reader.setStartPosition(class_def_item.offset);
        //解析每个类
        for (int i = 0; i < class_def_item.size; i++) {
            //解析类
            int class_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int access_flags = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int superclass_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int interfaces_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int source_file_idx = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int annotations_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int class_data_off = ConversionUtils.byteToUnsignedInt(reader.read(4));
            int static_values_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

            parseClassData(class_data_off);
        }
    }

    private void parseClassData(int class_data_off) {
        if (class_data_off == 0) {
//            System.out.println("The class has no data");
            return;
        }
        ByteReader classDataReader = new ByteReader(dexData);
        classDataReader.setStartPosition(class_data_off);

        int static_fields_size = classDataReader.readUnsignedLeb128();
        int instance_fields_size = classDataReader.readUnsignedLeb128();
        int direct_methods_size = classDataReader.readUnsignedLeb128();
        int virtual_methods_size = classDataReader.readUnsignedLeb128();

        int index = -1;
        for (int i = 0; i < static_fields_size; i++) {
            int field_idx_diff = index == -1 ? (index = classDataReader.readUnsignedLeb128()) : (index += classDataReader.readUnsignedLeb128());
            int access_flags = classDataReader.readUnsignedLeb128();
        }
        index = -1;
        for (int i = 0; i < instance_fields_size; i++) {
            int field_idx_diff = index == -1 ? (index = classDataReader.readUnsignedLeb128()) : (index += classDataReader.readUnsignedLeb128());
            int access_flags = classDataReader.readUnsignedLeb128();
        }
        index = -1;
        for (int i = 0; i < direct_methods_size; i++) {
            int method_idx_diff = (index == -1 ? (index = classDataReader.readUnsignedLeb128()) : (index += classDataReader.readUnsignedLeb128()));
            int access_flags = classDataReader.readUnsignedLeb128();
            int code_off = classDataReader.readUnsignedLeb128();
            DexMethodId dexMethodId = dexMethodIdsList[method_idx_diff];
            paresMethodInstructionSet(dexMethodId, code_off);
        }
        index = -1;
        for (int i = 0; i < virtual_methods_size; i++) {
            int method_idx_diff = index == -1 ? (index = classDataReader.readUnsignedLeb128()) : (index += classDataReader.readUnsignedLeb128());
            int access_flags = classDataReader.readUnsignedLeb128();
            int code_off = classDataReader.readUnsignedLeb128();
            DexMethodId dexMethodId = dexMethodIdsList[method_idx_diff];
            paresMethodInstructionSet(dexMethodId, code_off);
        }
    }

    private void paresMethodInstructionSet(DexMethodId methodId, int code_off) {
        if (code_off == 0) {
//            System.out.println("method is abstract or native");
            return;
        }
        ByteReader codeReader = new ByteReader(dexData);
        codeReader.setStartPosition(code_off);
        //	此代码使用的寄存器数量
        int registers_size = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
        // 此代码所用方法的传入参数的字数
        int ins_size = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
        // 此代码进行方法调用所需的传出参数空间的字数
        int outs_size = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
        //此实例的 try_item 数量。如果此值为非零值，则这些项会显示为 insns 数组（正好位于此实例中 tries 的后面）。
        int tries_size = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
        //从文件开头到此代码的调试信息（行号 + 局部变量信息）序列的偏移量；如果没有任何信息，该值为 0。该偏移量（如果为非零值）应该是到 data 区段中某个位置的偏移量。数据格式由下文的“debug_info_item”指定。
        int debug_info_off = ConversionUtils.byteToUnsignedInt(codeReader.read(4));
        //指令列表的大小（以 16 位代码单元为单位）
        int insns_size = ConversionUtils.byteToUnsignedInt(codeReader.read(4));
        for (int i = 0; i < insns_size; i++) {
            int insns = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
            //返回的第一位是v?寄存器 第二位可能是指令
            int invoke = SplitHexInt(insns)[1];
            if (invoke == 0x1A) {//const-string vAA, string@BBBB
//                int string_idx = ConversionUtils.bytesToUnsignedShort(Utils.copyArrays(dexData, codeReader.getPosition(), 2));
                int string_idx = ConversionUtils.bytesToUnsignedShort(codeReader.read(2));
                i++;
                if (string_idx > dexStringIdsList.length) continue;

                if (methodId.getUsedStringList() == null) methodId.initUsedStringList();
                methodId.getUsedStringList().add(string_idx);
            } else if (invoke == 0x1B) {//const-string/jumbo vAA, string@BBBBBBBB
                int string_idx = ConversionUtils.byteToUnsignedInt(codeReader.read(4));
                i += 2;//4位已经被读走 需要略过两位指令
                if (string_idx >= dexStringIdsList.length) continue;
                if (string_idx < 0) continue;
                if (methodId.getUsedStringList() == null) methodId.initUsedStringList();
                methodId.getUsedStringList().add(string_idx);
            }
        }
    }

    public void parseCallSite() {
        DexMap.Item call_site_item = dexMap.findItem(DexMap.TYPE_CALL_SITE_ID_ITEM);
        reader.setStartPosition(call_site_item.offset);
        for (int i = 0; i < call_site_item.size; i++) {
            int call_site_off = ConversionUtils.byteToUnsignedInt(reader.read(4));

        }
    }

    public void parseMethodHandle() {
        DexMap.Item item = dexMap.findItem(DexMap.TYPE_METHOD_HANDLE_ITEM);
        reader.setStartPosition(item.offset);
        for (int i = 0; i < item.size; i++) {
            int method_handle_type = ConversionUtils.bytesToUnsignedShort(reader.read(2));
            int unused = ConversionUtils.bytesToUnsignedShort(reader.read(2));
            int field_or_method_id = ConversionUtils.bytesToUnsignedShort(reader.read(2));
            int unused_2 = ConversionUtils.bytesToUnsignedShort(reader.read(2));

        }
    }


    public String getDexName() {
        return dexName;
    }

    public void setDexName(String dexName) {
        this.dexName = dexName;
    }
}
