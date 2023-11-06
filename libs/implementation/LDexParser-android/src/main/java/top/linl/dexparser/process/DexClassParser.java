package top.linl.dexparser.process;

import top.linl.dexparser.DexParser;
import top.linl.dexparser.util.ByteReader;

public class DexClassParser {
    private final DexParser dexParser;
    private final ByteReader reader;

    public DexClassParser(DexParser dexParser, ByteReader reader) {
        this.dexParser = dexParser;
        this.reader = reader;
    }
}
