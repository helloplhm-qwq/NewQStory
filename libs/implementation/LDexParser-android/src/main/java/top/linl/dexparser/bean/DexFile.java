package top.linl.dexparser.bean;

import java.io.IOException;

import top.linl.dexparser.DexParser;
import top.linl.dexparser.util.FileUtils;

public class DexFile {
    public static void writeLocallyAndFreeMemory(String path, DexParser dexParser) throws IOException {
        FileUtils.writeObjectToFile(path, dexParser);

    }
}
