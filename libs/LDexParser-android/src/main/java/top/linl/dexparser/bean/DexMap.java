package top.linl.dexparser.bean;

import java.util.LinkedHashMap;

/**
 * @author suzhelan
 * 2023.10.1
 */
public class DexMap {
    public final static int TYPE_HEADER_ITEM = 0x0000;
    public final static int TYPE_STRING_ID_ITEM = 0x0001;
    public final static int TYPE_TYPE_ID_ITEM = 0x0002;
    public final static int TYPE_PROTO_ID_ITEM = 0x0003;
    public final static int TYPE_FIELD_ID_ITEM = 0x0004;
    public final static int TYPE_METHOD_ID_ITEM = 0x0005;
    public final static int TYPE_CLASS_DEF_ITEM = 0x0006;
    public final static int TYPE_CALL_SITE_ID_ITEM = 0x0007;
    public final static int TYPE_METHOD_HANDLE_ITEM = 0x0008;
    public final static int TYPE_MAP_LIST = 0x1000;    //4 + (item.size * 12)
    public final static int TYPE_TYPE_LIST = 0x1001;//	4 + (item.size * 2)
    public final static int TYPE_ANNOTATION_SET_REF_LIST = 0x1002;//	4 + (item.size * 4)
    public final static int TYPE_ANNOTATION_SET_ITEM = 0x1003;//	4 + (item.size * 4)
    public final static int TYPE_CLASS_DATA_ITEM = 0x2000;//	隐式；必须解析
    public final static int TYPE_CODE_ITEM = 0x2001;//	隐式；必须解析
    public final static int TYPE_STRING_DATA_ITEM = 0x2002;//	隐式；必须解析
    public final static int TYPE_DEBUG_INFO_ITEM = 0x2003;//	隐式；必须解析
    public final static int TYPE_ANNOTATION_ITEM = 0x2004;//	隐式；必须解析
    public final static int TYPE_ENCODED_ARRAY_ITEM = 0x2005;//	隐式；必须解析
    public final static int TYPE_ANNOTATIONS_DIRECTORY_ITEM = 0x2006;//	隐式；必须解析
    public final static int TYPE_HIDDENAPI_CLASS_DATA_ITEM = 0xF000;//	隐式；必须解析
    private final LinkedHashMap<Integer, Item> itemList = new LinkedHashMap<>();

    /**
     * @param Type item的类型  参阅https://source.android.google.cn/docs/core/runtime/dex-format?hl=zh-cn#map-list
     */
    public Item findItem(int Type) {
        return itemList.get(Type);
    }

    public void addItem(int type, int size, int offset) {
        itemList.put(type, new Item(type, size, offset));
    }

    public static class Item {
        public int type;
        public int unused;
        public int size;
        public int offset;

        public Item(int type, int size, int offset) {
            this.type = type;
            this.size = size;
            this.offset = offset;
        }
    }
}
