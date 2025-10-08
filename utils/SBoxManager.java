package utils;

/**
 * S盒管理器
 * 负责S-DES算法中的S盒替换操作
 */
public class SBoxManager {
    // S0盒
    private static final int[][] S0_BOX = {
            { 1, 0, 3, 2 },
            { 3, 2, 1, 0 },
            { 0, 2, 1, 3 },
            { 3, 1, 3, 2 }
    };

    // S1盒
    private static final int[][] S1_BOX = {
            { 0, 1, 2, 3 },
            { 2, 0, 1, 3 },
            { 3, 0, 1, 0 },
            { 2, 1, 0, 3 }
    };

    /**
     * 执行S盒替换
     * 
     * @param input 8位输入
     * @return 4位输出
     */
    public static String substitute(String input) {
        // 分割输入为两部分
        String leftPart = input.substring(0, 4);
        String rightPart = input.substring(4);

        // 处理左半部分（S0盒）
        String s0Output = processSBox(leftPart, S0_BOX);

        // 处理右半部分（S1盒）
        String s1Output = processSBox(rightPart, S1_BOX);

        return s0Output + s1Output;
    }

    /**
     * 处理单个S盒
     * 
     * @param input 4位输入
     * @param sBox  S盒
     * @return 2位输出
     */
    private static String processSBox(String input, int[][] sBox) {
        // 计算行和列索引
        int row = Integer.parseInt(input.charAt(0) + "" + input.charAt(3), 2);
        int col = Integer.parseInt(input.charAt(1) + "" + input.charAt(2), 2);

        // 获取S盒值
        int value = sBox[row][col];

        // 转换为2位二进制
        String binary = Integer.toBinaryString(value);
        while (binary.length() < 2) {
            binary = "0" + binary;
        }
        return binary;
    }
}