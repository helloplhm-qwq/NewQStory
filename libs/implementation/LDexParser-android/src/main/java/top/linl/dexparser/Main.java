package top.linl.dexparser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File apk = new File("D:\\JADX\\QQ_8.9.85.apk");
        DexFinder dexFinder =  DexFinder.builder(apk.getAbsolutePath()).build();
        System.out.println(dexFinder.testFindMethodString("doOnBackEvent"));
    }
}
