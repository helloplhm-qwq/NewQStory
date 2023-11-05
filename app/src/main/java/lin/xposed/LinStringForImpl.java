package lin.xposed;

import com.github.megatronking.stringfog.IStringFog;
import com.github.megatronking.stringfog.annotation.StringFogIgnore;

@StringFogIgnore
public class LinStringForImpl implements IStringFog {

    public static final String TAG = "想学习开口便是 何须行此径 你不说我怎么知道你想要呢 ~ ";
    private static final char[][] keyAndString = {{'0', '喵'}, {'1', '呜'}, {' ', '~'}};

    public static String toBinary(String str) {
        //把字符串转成字符数组
        char[] strChar = str.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char c : strChar) {
            //toBinaryString(int i)返回变量的二进制表示的字符串
            //toHexString(int i) 八进制
            //toOctalString(int i) 十六进制
            result.append(Integer.toBinaryString(c)).append(" ");
        }
        return result.toString();
    }

    public static String toString(String binary) {
        String[] tempStr = binary.split(" ");
        char[] tempChar = new char[tempStr.length];
        for (int i = 0; i < tempStr.length; i++) {
            tempChar[i] = BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }

    //将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    //将二进制转换成字符
    public static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }

    @Override
    public byte[] encrypt(String data, byte[] key) {
        String b = toBinary(data);
        for (char[] chars : keyAndString) {
            b = b.replace(chars[0], chars[1]);
        }
        return b.getBytes();
    }

    @Override
    public String decrypt(byte[] data, byte[] key) {
        String binary = new String(data);
        for (char[] chars : keyAndString) {
            binary = binary.replace(chars[1], chars[0]);
        }
        return toString(binary);
    }

    @Override
    public boolean shouldFog(String data) {
        if (data.length() > 256) return false;
        String b = toBinary(data);
        for (char[] chars : keyAndString) {
            b = b.replace(chars[0], chars[1]);
        }
        return b.getBytes().length < (65535 / 2);
    }


}
