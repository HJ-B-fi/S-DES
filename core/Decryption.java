package core;

import utils.CommonUtils;
import utils.SBoxManager;

/**
 * 解密模块
 * 负责执行S-DES解密算法
 */
public class Decryption {
    // 初始置换IP表
    private static final int[] INITIAL_PERMUTATION_TABLE = { 2, 6, 3, 1, 4, 8, 5, 7 };
    // 逆初始置换IP^-1表
    private static final int[] INVERSE_INITIAL_PERMUTATION_TABLE = { 4, 1, 3, 5, 7, 2, 8, 6 };
    // 扩展置换EP表
    private static final int[] EXPANSION_PERMUTATION_TABLE = { 4, 1, 2, 3, 2, 3, 4, 1 };
    // P4置换表
    private static final int[] P4_PERMUTATION_TABLE = { 2, 4, 3, 1 };

    /**
     * 使用给定的子密钥解密密文块
     * 
     * @param ciphertext 8位二进制密文
     * @param key1       第一个8位子密钥（解密时顺序与加密相反）
     * @param key2       第二个8位子密钥（解密时顺序与加密相反）
     * @return 8位二进制明文
     */
    public String decrypt(String ciphertext, String key1, String key2) {
        return processBlock(ciphertext, key1, key2);
    }

    private String processBlock(String block, String key1, String key2) {
        // 初始置换IP
        String initialPermutation = CommonUtils.permute(block, INITIAL_PERMUTATION_TABLE, 8);

        // 第一轮
        String round1Result = performRound(initialPermutation, key1);

        // 交换左右4位
        String swapped = round1Result.substring(4) + round1Result.substring(0, 4);

        // 第二轮
        String round2Result = performRound(swapped, key2);

        // 逆初始置换
        return CommonUtils.permute(round2Result, INVERSE_INITIAL_PERMUTATION_TABLE, 8);
    }

    private String performRound(String input, String roundKey) {
        // 分割成左右4位
        String leftPart = input.substring(0, 4);
        String rightPart = input.substring(4);

        // 扩展置换EP
        String expanded = CommonUtils.permute(rightPart, EXPANSION_PERMUTATION_TABLE, 8);

        // 与轮密钥异或
        String xorResult = CommonUtils.xor(expanded, roundKey);

        // S盒替换
        String sboxOutput = SBoxManager.substitute(xorResult);

        // P4置换
        String p4Result = CommonUtils.permute(sboxOutput, P4_PERMUTATION_TABLE, 4);

        // 与左半部分异或
        String newLeft = CommonUtils.xor(leftPart, p4Result);

        return newLeft + rightPart;
    }
}