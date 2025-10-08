package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.SDESCore;
import utils.CommonUtils;

/**
 * S-DES暴力破解工具界面
 * 提供多线程暴力破解和密钥碰撞分析功能
 */
public class BruteForceSDES extends JFrame {
    // UI组件
    private JTextArea plaintextPairsArea;
    private JTextArea ciphertextPairsArea;
    private JTextArea resultArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton findAllKeysButton;
    private JButton collisionTestButton; // 新增：碰撞测试按钮
    private JButton addPairButton;
    private JButton clearPairsButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // 多线程控制变量
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicInteger keysTested = new AtomicInteger(0);
    private AtomicInteger completedThreads = new AtomicInteger(0);
    private List<String> allFoundKeys = Collections.synchronizedList(new ArrayList<>());
    private long startTime;

    // 线程数量常量
    private static final int THREAD_COUNT = 4;
    private static final int TOTAL_KEYS = 1024;
    private static final int KEYS_PER_THREAD = TOTAL_KEYS / THREAD_COUNT;

    public BruteForceSDES() {
        setTitle("S-DES算法分析工具");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500); // 稍微增加高度以容纳新按钮
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        addEventListeners();
    }

    /**
     * 初始化UI组件
     */
    private void initializeComponents() {
        plaintextPairsArea = new JTextArea(5, 20);
        plaintextPairsArea.setLineWrap(true);
        ciphertextPairsArea = new JTextArea(5, 20);
        ciphertextPairsArea.setLineWrap(true);
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);

        startButton = new JButton("开始破解");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        findAllKeysButton = new JButton("所有密钥");
        collisionTestButton = new JButton("碰撞测试");
        addPairButton = new JButton("添加示例");
        clearPairsButton = new JButton("清空所有");

        statusLabel = new JLabel("就绪");
        progressBar = new JProgressBar(0, TOTAL_KEYS);

        // 设置进度条高度与按钮一致
        progressBar.setPreferredSize(new Dimension(300, 28));
        progressBar.setStringPainted(true);
    }

    /**
     * 布局UI组件
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 明密文对输入面板
        JPanel pairsPanel = createPairsPanel();
        // 控制面板
        JPanel controlPanel = createControlPanel();
        // 结果面板
        JPanel resultPanel = createResultPanel();

        mainPanel.add(pairsPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建明密文对输入面板
     */
    private JPanel createPairsPanel() {
        JPanel pairsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        pairsPanel.setBorder(BorderFactory.createTitledBorder("明密文对列表 (每行一对，8位二进制)"));

        // 明文面板
        JPanel plaintextPanel = new JPanel(new BorderLayout());
        plaintextPanel.setBorder(BorderFactory.createTitledBorder("明文列表"));
        plaintextPanel.add(new JScrollPane(plaintextPairsArea), BorderLayout.CENTER);

        // 密文面板
        JPanel ciphertextPanel = new JPanel(new BorderLayout());
        ciphertextPanel.setBorder(BorderFactory.createTitledBorder("密文列表"));
        ciphertextPanel.add(new JScrollPane(ciphertextPairsArea), BorderLayout.CENTER);

        pairsPanel.add(plaintextPanel);
        pairsPanel.add(ciphertextPanel);

        return pairsPanel;
    }

    /**
     * 创建控制面板
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());

        // 按钮面板
        JPanel buttonPanel = createButtonPanel();
        // 状态面板
        JPanel statusPanel = createStatusPanel();

        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(statusPanel, BorderLayout.SOUTH);

        return controlPanel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));

        // 左侧按钮面板
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.add(startButton);
        leftButtonPanel.add(stopButton);
        leftButtonPanel.add(findAllKeysButton);
        leftButtonPanel.add(collisionTestButton); // 添加碰撞测试按钮
        leftButtonPanel.add(addPairButton);
        leftButtonPanel.add(clearPairsButton);

        // 右侧进度条面板
        JPanel rightProgressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightProgressPanel.add(progressBar);

        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonPanel.add(rightProgressPanel, BorderLayout.CENTER);

        return buttonPanel;
    }

    /**
     * 创建状态面板
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("状态:"));
        statusPanel.add(statusLabel);
        return statusPanel;
    }

    /**
     * 创建结果面板
     */
    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("破解结果"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return resultPanel;
    }

    /**
     * 添加事件监听器
     */
    private void addEventListeners() {
        startButton.addActionListener(e -> startBruteForce());
        stopButton.addActionListener(e -> stopBruteForce());
        findAllKeysButton.addActionListener(e -> findAllPossibleKeys());
        collisionTestButton.addActionListener(e -> performCollisionTest()); // 新增碰撞测试监听器
        addPairButton.addActionListener(e -> addSamplePairs());
        clearPairsButton.addActionListener(e -> clearAllPairs());
    }

    /**
     * 执行碰撞测试
     */
    private void performCollisionTest() {
        List<String> plaintexts = getPlaintexts();

        if (plaintexts.isEmpty()) {
            showErrorMessage("请输入至少一个明文进行碰撞测试！");
            return;
        }

        // 检查明文格式
        for (String plaintext : plaintexts) {
            if (!CommonUtils.isValidBinary(plaintext, 8)) {
                showErrorMessage("明文必须是8位二进制字符串！\n错误明文: " + plaintext);
                return;
            }
        }

        resetState();

        resultArea.setText("开始碰撞测试...\n");
        resultArea.append("测试明文: " + plaintexts + "\n");
        resultArea.append("正在分析所有1024个密钥的加密结果...\n\n");

        // 启动碰撞测试线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadNum = i;
            new Thread(() -> collisionTestWorker(threadNum, plaintexts)).start();
        }
    }

    /**
     * 碰撞测试工作线程
     */
    private void collisionTestWorker(int threadNum, List<String> plaintexts) {
        SDESCore sdesCore = new SDESCore();
        Map<String, List<String>> ciphertextToKeysMap = new HashMap<>(); // 密文到密钥列表的映射

        int startKey = threadNum * KEYS_PER_THREAD;
        int endKey = (threadNum + 1) * KEYS_PER_THREAD;

        for (int keyValue = startKey; keyValue < endKey && isRunning.get(); keyValue++) {
            String binaryKey = formatBinaryKey(keyValue);

            // 对每个明文进行加密
            for (String plaintext : plaintexts) {
                try {
                    String ciphertext = sdesCore.encrypt(plaintext, binaryKey);

                    // 同步更新映射表
                    synchronized (ciphertextToKeysMap) {
                        ciphertextToKeysMap
                                .computeIfAbsent(ciphertext, k -> new ArrayList<>())
                                .add(binaryKey);
                    }
                } catch (Exception e) {
                    // 忽略加密异常，继续测试下一个密钥
                }
            }

            updateProgress();
        }

        checkCollisionTestCompletion(ciphertextToKeysMap, plaintexts);
    }

    /**
     * 检查碰撞测试完成状态
     */
    private void checkCollisionTestCompletion(Map<String, List<String>> ciphertextToKeysMap, List<String> plaintexts) {
        int completedCount = completedThreads.incrementAndGet();
        if (completedCount >= THREAD_COUNT && isRunning.get()) {
            SwingUtilities.invokeLater(() -> {
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;

                resultArea.append("=== 碰撞测试完成 ===\n");
                resultArea.append("总耗时: " + timeElapsed + " 毫秒\n\n");

                displayCollisionResults(ciphertextToKeysMap, plaintexts);
                finishOperation();
            });
            isRunning.set(false);
        }
    }

    /**
     * 显示碰撞测试结果
     */
    private void displayCollisionResults(Map<String, List<String>> ciphertextToKeysMap, List<String> plaintexts) {
        int totalCollisions = 0;
        int maxCollisions = 0;
        String maxCollisionCiphertext = "";

        resultArea.append("=== 碰撞分析结果 ===\n");

        for (String plaintext : plaintexts) {
            resultArea.append("\n明文: " + plaintext + " 的碰撞分析:\n");
            resultArea.append("----------------------------------------\n");

            // 筛选出该明文的加密结果
            Map<String, List<String>> plaintextCollisions = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : ciphertextToKeysMap.entrySet()) {
                String ciphertext = entry.getKey();
                List<String> keys = entry.getValue();

                // 验证这些密钥确实能产生该密文
                boolean valid = true;
                SDESCore sdesCore = new SDESCore();
                for (String key : keys) {
                    if (!sdesCore.encrypt(plaintext, key).equals(ciphertext)) {
                        valid = false;
                        break;
                    }
                }

                if (valid && keys.size() > 1) {
                    plaintextCollisions.put(ciphertext, keys);
                }
            }

            if (plaintextCollisions.isEmpty()) {
                resultArea.append("未发现密钥碰撞现象\n");
            } else {
                for (Map.Entry<String, List<String>> entry : plaintextCollisions.entrySet()) {
                    String ciphertext = entry.getKey();
                    List<String> keys = entry.getValue();

                    resultArea.append("密文: " + ciphertext + " ← 密钥数量: " + keys.size() + "\n");
                    resultArea.append("对应密钥: " + keys + "\n");

                    totalCollisions += keys.size() - 1; // 每个额外密钥算一次碰撞
                    if (keys.size() > maxCollisions) {
                        maxCollisions = keys.size();
                        maxCollisionCiphertext = ciphertext;
                    }
                }
            }
        }

        // 总体统计
        resultArea.append("\n=== 总体统计 ===\n");
        resultArea.append("测试明文数量: " + plaintexts.size() + "\n");
        resultArea.append("总碰撞次数: " + totalCollisions + "\n");
        resultArea.append("最大碰撞数: " + maxCollisions + " 个密钥对应同一密文\n");
        if (maxCollisions > 0) {
            resultArea.append("最大碰撞密文: " + maxCollisionCiphertext + "\n");
        }
        resultArea.append("平均每个明文对应的密钥数: " + String.format("%.2f", 1024.0 / 256) + "\n");

    }

    /**
     * 开始暴力破解
     */
    private void startBruteForce() {
        if (!validateInput())
            return;
        resetState();

        resultArea.setText("开始暴力破解...\n");
        appendInputInfo();
        resultArea.append("正在测试所有" + TOTAL_KEYS + "个可能的密钥...\n\n");

        startBruteForceThreads(false);
    }

    /**
     * 寻找所有可能密钥
     */
    private void findAllPossibleKeys() {
        if (!validateInput())
            return;
        resetState();

        resultArea.setText("开始寻找所有可能密钥...\n");
        appendInputInfo();
        resultArea.append("正在测试所有" + TOTAL_KEYS + "个可能的密钥...\n\n");

        startBruteForceThreads(true);
    }

    /**
     * 重置状态
     */
    private void resetState() {
        isRunning.set(true);
        startButton.setEnabled(false);
        findAllKeysButton.setEnabled(false);
        collisionTestButton.setEnabled(false); // 禁用碰撞测试按钮
        stopButton.setEnabled(true);
        addPairButton.setEnabled(false);
        clearPairsButton.setEnabled(false);
        keysTested.set(0);
        completedThreads.set(0);
        allFoundKeys.clear();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        startTime = System.currentTimeMillis();
    }

    /**
     * 完成操作
     */
    private void finishOperation() {
        startButton.setEnabled(true);
        findAllKeysButton.setEnabled(true);
        collisionTestButton.setEnabled(true); // 重新启用碰撞测试按钮
        stopButton.setEnabled(false);
        addPairButton.setEnabled(true);
        clearPairsButton.setEnabled(true);
        statusLabel.setText("操作完成");
        progressBar.setValue(TOTAL_KEYS);
    }

    /**
     * 添加示例明密文对
     */
    private void addSamplePairs() {
        plaintextPairsArea.setText("10010011\n10101010\n00001111\n11110000");
        ciphertextPairsArea.setText("01011011\n01101011\n00110101\n01011001");

        resultArea.setText("已添加示例明密文对！\n");
        resultArea.append("这些是使用密钥 '1010101010' 生成的测试数据\n");
        resultArea.append("你可以修改或添加更多的明密文对\n");
        resultArea.append("提示：使用较少的明密文对会找到更多可能的密钥\n\n");

        // 添加碰撞测试说明
        resultArea.append("碰撞测试说明：\n");
        resultArea.append("- 在明文区域输入要测试的明文(8位二进制)\n");
        resultArea.append("- 点击'碰撞测试'分析不同密钥产生相同密文的情况\n");
        resultArea.append("- 可用于验证S-DES算法的密钥碰撞特性\n");
    }

    /**
     * 启动暴力破解线程
     * 
     * @param findAllKeys 是否查找所有可能的密钥
     */
    private void startBruteForceThreads(boolean findAllKeys) {
        for (int threadNum = 0; threadNum < THREAD_COUNT; threadNum++) {
            final int currentThreadNum = threadNum;
            new Thread(() -> {
                if (findAllKeys) {
                    findAllKeysWorker(currentThreadNum);
                } else {
                    bruteForceWorker(currentThreadNum);
                }
            }).start();
        }
    }

    /**
     * 暴力破解工作线程
     * 
     * @param threadNum 线程编号
     */
    private void bruteForceWorker(int threadNum) {
        SDESCore sdesCore = new SDESCore();
        List<String> plaintexts = getPlaintexts();
        List<String> ciphertexts = getCiphertexts();

        int startKey = threadNum * KEYS_PER_THREAD;
        int endKey = (threadNum + 1) * KEYS_PER_THREAD;

        for (int keyValue = startKey; keyValue < endKey && isRunning.get(); keyValue++) {
            String binaryKey = formatBinaryKey(keyValue);

            if (testKey(sdesCore, plaintexts, ciphertexts, binaryKey)) {
                handleFoundKey(binaryKey, threadNum, plaintexts, ciphertexts, sdesCore);
                return;
            }

            updateProgress();
        }

        checkCompletion();
    }

    /**
     * 查找所有密钥工作线程
     * 
     * @param threadNum 线程编号
     */
    private void findAllKeysWorker(int threadNum) {
        SDESCore sdesCore = new SDESCore();
        List<String> plaintexts = getPlaintexts();
        List<String> ciphertexts = getCiphertexts();

        int startKey = threadNum * KEYS_PER_THREAD;
        int endKey = (threadNum + 1) * KEYS_PER_THREAD;

        for (int keyValue = startKey; keyValue < endKey && isRunning.get(); keyValue++) {
            String binaryKey = formatBinaryKey(keyValue);

            if (testKey(sdesCore, plaintexts, ciphertexts, binaryKey)) {
                allFoundKeys.add(binaryKey);
            }

            updateProgress();
        }

        checkAllKeysCompletion(plaintexts, ciphertexts, sdesCore);
    }

    /**
     * 测试密钥是否正确
     * 
     * @param sdesCore    SDES核心实例
     * @param plaintexts  明文列表
     * @param ciphertexts 密文列表
     * @param key         待测试密钥
     * @return 密钥是否正确
     */
    private boolean testKey(SDESCore sdesCore, List<String> plaintexts, List<String> ciphertexts, String key) {
        try {
            for (int i = 0; i < plaintexts.size(); i++) {
                String encrypted = sdesCore.encrypt(plaintexts.get(i), key);
                if (!encrypted.equals(ciphertexts.get(i))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 更新进度条
     */
    private void updateProgress() {
        int testedCount = keysTested.incrementAndGet();
        if (testedCount % 100 == 0 || testedCount == TOTAL_KEYS) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(testedCount);
                statusLabel.setText("已测试: " + testedCount + " / " + TOTAL_KEYS + " 个密钥");
            });
        }
    }

    /**
     * 处理找到的密钥
     */
    private void handleFoundKey(String key, int threadNum, List<String> plaintexts,
            List<String> ciphertexts, SDESCore sdesCore) {
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        SwingUtilities.invokeLater(() -> {
            resultArea.append("=== 找到密钥！ ===\n");
            resultArea.append("密钥: " + key + "\n");
            resultArea.append("测试密钥数量: " + keysTested.get() + "\n");
            resultArea.append("耗时: " + timeElapsed + " 毫秒\n");
            resultArea.append("线程 " + threadNum + " 找到密钥\n\n");

            verifyKey(key, plaintexts, ciphertexts, sdesCore);
            progressBar.setValue(TOTAL_KEYS);
            statusLabel.setText("已测试: " + TOTAL_KEYS + " / " + TOTAL_KEYS + " 个密钥");
        });

        isRunning.set(false);
        finishOperation();
    }

    /**
     * 检查暴力破解完成状态
     */
    private void checkCompletion() {
        int completedCount = completedThreads.incrementAndGet();
        if (completedCount >= THREAD_COUNT && isRunning.get()) {
            SwingUtilities.invokeLater(() -> {
                resultArea.append("=== 破解完成 ===\n");
                resultArea.append("未找到匹配的密钥！\n");
                resultArea.append("测试了所有" + TOTAL_KEYS + "个可能的密钥\n");

                progressBar.setValue(TOTAL_KEYS);
                statusLabel.setText("已测试: " + TOTAL_KEYS + " / " + TOTAL_KEYS + " 个密钥");
                finishOperation();
            });
            isRunning.set(false);
        }
    }

    /**
     * 检查所有密钥查找完成状态
     */
    private void checkAllKeysCompletion(List<String> plaintexts, List<String> ciphertexts, SDESCore sdesCore) {
        int completedCount = completedThreads.incrementAndGet();
        if (completedCount >= THREAD_COUNT && isRunning.get()) {
            SwingUtilities.invokeLater(() -> {
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;

                resultArea.append("=== 所有可能密钥寻找完成 ===\n");
                resultArea.append("总耗时: " + timeElapsed + " 毫秒\n");
                resultArea.append("找到的密钥数量: " + allFoundKeys.size() + "\n\n");

                if (allFoundKeys.isEmpty()) {
                    resultArea.append("未找到任何匹配的密钥！\n");
                } else {
                    displayFoundKeys(plaintexts, ciphertexts, sdesCore);
                }
                finishOperation();
            });
            isRunning.set(false);
        }
    }

    /**
     * 显示找到的密钥
     */
    private void displayFoundKeys(List<String> plaintexts, List<String> ciphertexts, SDESCore sdesCore) {
        resultArea.append("=== 找到的密钥列表 ===\n");
        for (int i = 0; i < allFoundKeys.size(); i++) {
            resultArea.append("密钥 " + (i + 1) + ": " + allFoundKeys.get(i) + "\n");
        }

        resultArea.append("\n=== 密钥验证 ===\n");
        for (String key : allFoundKeys) {
            resultArea.append("密钥 " + key + " 验证: ");
            boolean isAllVerified = verifyAllPairs(key, plaintexts, ciphertexts, sdesCore);
            resultArea.append(isAllVerified ? "✓ 全部正确" : "✗ 存在错误");
            resultArea.append("\n");
        }

    }

    /**
     * 验证所有明密文对
     */
    private boolean verifyAllPairs(String key, List<String> plaintexts, List<String> ciphertexts, SDESCore sdesCore) {
        for (int i = 0; i < plaintexts.size(); i++) {
            String decrypted = sdesCore.decrypt(ciphertexts.get(i), key);
            if (!decrypted.equals(plaintexts.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证密钥
     */
    private void verifyKey(String key, List<String> plaintexts, List<String> ciphertexts, SDESCore sdesCore) {
        resultArea.append("=== 验证结果 ===\n");
        boolean isAllVerified = true;

        for (int i = 0; i < plaintexts.size(); i++) {
            String decrypted = sdesCore.decrypt(ciphertexts.get(i), key);
            boolean isVerified = decrypted.equals(plaintexts.get(i));
            resultArea.append("对 " + (i + 1) + ": 解密=" + decrypted + ", 期望=" + plaintexts.get(i) +
                    " -> " + (isVerified ? "✓" : "✗") + "\n");
            if (!isVerified) {
                isAllVerified = false;
            }
        }
        resultArea.append("总体验证: " + (isAllVerified ? "成功" : "失败") + "\n\n");
    }

    /**
     * 验证输入数据
     */
    private boolean validateInput() {
        List<String> plaintexts = getPlaintexts();
        List<String> ciphertexts = getCiphertexts();

        if (plaintexts.isEmpty() || ciphertexts.isEmpty()) {
            showErrorMessage("请输入明密文对！");
            return false;
        }

        if (plaintexts.size() != ciphertexts.size()) {
            showErrorMessage("明文和密文数量不匹配！");
            return false;
        }

        for (int i = 0; i < plaintexts.size(); i++) {
            String plaintext = plaintexts.get(i);
            String ciphertext = ciphertexts.get(i);
            if (!CommonUtils.isValidBinary(plaintext, 8) || !CommonUtils.isValidBinary(ciphertext, 8)) {
                showErrorMessage("第 " + (i + 1) + " 对明密文必须是8位二进制字符串！\n明文: " + plaintext + "\n密文: " + ciphertext);
                return false;
            }
        }
        return true;
    }

    /**
     * 添加输入信息到结果区域
     */
    private void appendInputInfo() {
        List<String> plaintexts = getPlaintexts();
        List<String> ciphertexts = getCiphertexts();

        resultArea.append("明密文对数量: " + plaintexts.size() + "\n");
        for (int i = 0; i < plaintexts.size(); i++) {
            resultArea.append("对 " + (i + 1) + ": 明文=" + plaintexts.get(i) + ", 密文=" + ciphertexts.get(i) + "\n");
        }
    }

    /**
     * 停止暴力破解
     */
    private void stopBruteForce() {
        isRunning.set(false);
        finishOperation();
        statusLabel.setText("已停止");
    }

    /**
     * 清空所有明密文对
     */
    private void clearAllPairs() {
        plaintextPairsArea.setText("");
        ciphertextPairsArea.setText("");
        resultArea.setText("已清空所有明密文对\n");
        progressBar.setValue(0);
    }

    /**
     * 获取明文列表
     */
    private List<String> getPlaintexts() {
        return CommonUtils.getNonEmptyLines(plaintextPairsArea.getText());
    }

    /**
     * 获取密文列表
     */
    private List<String> getCiphertexts() {
        return CommonUtils.getNonEmptyLines(ciphertextPairsArea.getText());
    }

    /**
     * 格式化二进制密钥
     */
    private String formatBinaryKey(int keyValue) {
        return String.format("%10s", Integer.toBinaryString(keyValue)).replace(' ', '0');
    }

    /**
     * 显示错误消息
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 主方法 - 用于独立测试
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BruteForceSDES().setVisible(true));
    }
}