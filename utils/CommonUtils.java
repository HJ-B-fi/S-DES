package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用工具类
 * 包含S-DES算法中使用的各种通用功能
 */
public class CommonUtils {

    /**
     * 执行置换操作
     * 
     * @param input            输入二进制字符串
     * @param permutationTable 置换表
     * @param outputSize       输出大小
     * @return 置换后的二进制字符串
     */
    public static String permute(String input, int[] permutationTable, int outputSize) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < outputSize; i++) {
            result.append(input.charAt(permutationTable[i] - 1));
        }
        return result.toString();
    }

    /**
     * 执行左循环移位
     * 
     * @param input     输入二进制字符串
     * @param shiftBits 移位位数
     * @return 移位后的二进制字符串
     */
    public static String leftShift(String input, int shiftBits) {
        int length = input.length();
        shiftBits = shiftBits % length;
        return input.substring(shiftBits) + input.substring(0, shiftBits);
    }

    /**
     * 执行异或操作
     * 
     * @param a 第一个二进制字符串
     * @param b 第二个二进制字符串
     * @return 异或结果二进制字符串
     */
    public static String xor(String a, String b) {
        if (a.length() != b.length()) {
            throw new IllegalArgumentException("输入字符串长度必须相同");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    /**
     * 将字符转换为二进制字符串
     * 
     * @param character 要转换的字符
     * @param bits      位数
     * @return 二进制字符串
     */
    public static String charToBinaryString(char character, int bits) {
        String binary = Integer.toBinaryString(character);
        // 填充前导零
        while (binary.length() < bits) {
            binary = "0" + binary;
        }
        // 如果超过指定位数，截取低位
        if (binary.length() > bits) {
            binary = binary.substring(binary.length() - bits);
        }
        return binary;
    }

    /**
     * 将二进制字符串转换为字符
     * 
     * @param binaryString 二进制字符串
     * @return 对应的字符
     */
    public static char binaryStringToChar(String binaryString) {
        return (char) Integer.parseInt(binaryString, 2);
    }

    /**
     * 验证二进制字符串是否有效
     * 
     * @param binary         二进制字符串
     * @param expectedLength 期望长度
     * @return 是否有效
     */
    public static boolean isValidBinary(String binary, int expectedLength) {
        if (binary == null || binary.length() != expectedLength) {
            return false;
        }
        return binary.matches("[01]{" + expectedLength + "}");
    }

    /**
     * 获取非空行列表
     * 
     * @param text 输入文本
     * @return 非空行列表
     */
    public static List<String> getNonEmptyLines(String text) {
        List<String> lines = new ArrayList<>();
        String[] splitLines = text.split("\n");
        for (String line : splitLines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }
}