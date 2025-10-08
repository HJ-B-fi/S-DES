package core;

import utils.CommonUtils;

/**
 * 密钥生成器
 * 负责根据10位主密钥生成两个8位子密钥
 */
public class KeyGenerator {
    // P10置换表
    private static final int[] P10_TABLE = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
    // P8置换表
    private static final int[] P8_TABLE = { 6, 3, 7, 4, 8, 5, 10, 9 };
    // 左循环移位表
    private static final int[] SHIFT_BITS = { 1, 2 };

    /**
     * 生成两个8位子密钥
     * 
     * @param key 10位主密钥
     * @return 包含两个8位子密钥的数组 [K1, K2]
     */
    public String[] generateKeys(String key) {
        // P10置换
        String p10Result = CommonUtils.permute(key, P10_TABLE, 10);

        // 分割成左右两部分
        String leftPart = p10Result.substring(0, 5);
        String rightPart = p10Result.substring(5);

        // 生成K1
        String leftShifted1 = CommonUtils.leftShift(leftPart, SHIFT_BITS[0]);
        String rightShifted1 = CommonUtils.leftShift(rightPart, SHIFT_BITS[0]);
        String key1 = CommonUtils.permute(leftShifted1 + rightShifted1, P8_TABLE, 8);

        // 生成K2
        String leftShifted2 = CommonUtils.leftShift(leftShifted1, SHIFT_BITS[1]);
        String rightShifted2 = CommonUtils.leftShift(rightShifted1, SHIFT_BITS[1]);
        String key2 = CommonUtils.permute(leftShifted2 + rightShifted2, P8_TABLE, 8);

        return new String[] { key1, key2 };
    }
}