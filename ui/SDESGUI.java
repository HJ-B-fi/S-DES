package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import core.SDESCore;
import utils.CommonUtils;

public class SDESGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextField keyTextField;
    private JTextArea outputTextArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JButton swapButton;
    private JButton copyButton;
    private JLabel inputLabel;
    private JLabel keyLabel;
    private JLabel outputLabel;
    private JButton binaryModeButton;
    private JButton asciiModeButton;
    private JButton bruteForceButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel binaryPanel;
    private JPanel asciiPanel;
    private boolean isBinaryMode = true;

    // 用于防止循环更新的标志
    private boolean isUpdatingKey = false;
    private boolean isUpdatingInput = false;
    private boolean isUpdatingOutput = false;

    public SDESGUI() {
        setTitle("S-DES加解密一体机");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 450);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        addEventListeners();
    }

    private void initializeComponents() {
        inputTextArea = new JTextArea(5, 40);
        keyTextField = new JTextField(20);
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setEditable(false);

        encryptButton = new JButton("加密");
        decryptButton = new JButton("解密");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换");
        copyButton = new JButton("复制结果");
        bruteForceButton = new JButton("算法分析");

        inputLabel = new JLabel("输入文本 (8位二进制):");
        keyLabel = new JLabel("密钥 (10位二进制):");
        outputLabel = new JLabel("输出结果 (8位二进制):");

        binaryModeButton = new JButton("二进制模式");
        asciiModeButton = new JButton("ASCII模式");

        // 设置文本区域自动换行
        inputTextArea.setLineWrap(true);
        outputTextArea.setLineWrap(true);

        // 创建卡片布局
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        binaryPanel = new JPanel();
        asciiPanel = new JPanel();
    }

    private void layoutComponents() {
        // 创建模式选择面板
        JPanel modePanel = new JPanel(new FlowLayout());
        modePanel.add(new JLabel("模式选择:"));
        modePanel.add(binaryModeButton);
        modePanel.add(asciiModeButton);

        // 设置主布局
        setLayout(new BorderLayout());
        add(modePanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        // 构建二进制模式面板
        buildBinaryPanel();
        // 构建ASCII模式面板
        buildAsciiPanel();

        // 添加卡片到主面板
        cardPanel.add(binaryPanel, "binary");
        cardPanel.add(asciiPanel, "ascii");

        // 默认显示二进制模式
        cardLayout.show(cardPanel, "binary");
    }

    private void buildBinaryPanel() {
        binaryPanel.setLayout(new BorderLayout());
        binaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 密钥面板
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(keyLabel);
        keyTextField.setPreferredSize(new Dimension(150, 25));
        keyPanel.add(keyTextField);

        // 添加暴力破解按钮到密钥面板
        bruteForceButton.setPreferredSize(new Dimension(100, 25));
        keyPanel.add(bruteForceButton);

        binaryPanel.add(keyPanel, BorderLayout.NORTH);

        // 中央面板
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 左侧面板 - 输入和输出区域
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("文本区域"));

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        leftPanel.add(inputPanel);
        leftPanel.add(outputPanel);

        // 右侧面板 - 按钮区域
        JPanel rightPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("操作按钮"));
        rightPanel.setPreferredSize(new Dimension(180, rightPanel.getPreferredSize().height));

        // 设置按钮大小一致
        Dimension buttonSize = new Dimension(120, 35);
        JButton[] buttons = {
                new JButton("加密"), new JButton("解密"), new JButton("清空"),
                new JButton("交换"), new JButton("复制结果")
        };

        for (JButton button : buttons) {
            button.setPreferredSize(buttonSize);
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(button);
            rightPanel.add(buttonPanel);
        }

        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);
        binaryPanel.add(centerPanel, BorderLayout.CENTER);

        // 为二进制模式按钮添加监听器
        addBinaryButtonListeners(buttons);
    }

    private void buildAsciiPanel() {
        asciiPanel.setLayout(new BorderLayout());
        asciiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 密钥面板
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("密钥 (10位二进制):"));
        JTextField asciiKeyTextField = new JTextField(20);
        asciiKeyTextField.setPreferredSize(new Dimension(150, 25));

        // 同步密钥文本
        setupKeySynchronization(asciiKeyTextField);

        keyPanel.add(asciiKeyTextField);

        // 在ASCII模式也添加暴力破解按钮
        JButton asciiBruteForceButton = new JButton("算法分析");
        asciiBruteForceButton.setPreferredSize(new Dimension(100, 25));
        keyPanel.add(asciiBruteForceButton);

        asciiPanel.add(keyPanel, BorderLayout.NORTH);

        // 中央面板 - 左右分割
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 左侧面板 - 输入
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("输入文本"));
        JTextArea asciiInputTextArea = new JTextArea(5, 40);
        asciiInputTextArea.setLineWrap(true);
        asciiInputTextArea.setText(inputTextArea.getText());

        // 输入文本同步
        setupInputSynchronization(asciiInputTextArea);

        leftPanel.add(new JScrollPane(asciiInputTextArea), BorderLayout.CENTER);

        // 右侧面板 - 输出
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("输出结果"));
        JTextArea asciiOutputTextArea = new JTextArea(5, 40);
        asciiOutputTextArea.setLineWrap(true);
        asciiOutputTextArea.setEditable(false);
        asciiOutputTextArea.setText(outputTextArea.getText());

        // 输出文本同步
        setupOutputSynchronization(asciiOutputTextArea);

        rightPanel.add(new JScrollPane(asciiOutputTextArea), BorderLayout.CENTER);
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        asciiPanel.add(centerPanel, BorderLayout.CENTER);

        // 底部面板 - 按钮
        JPanel bottomPanel = createAsciiButtonPanel();
        asciiPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 为ASCII模式的暴力破解按钮添加监听器
        asciiBruteForceButton.addActionListener(e -> openBruteForceTool());
    }

    private void setupKeySynchronization(JTextField asciiKeyTextField) {
        asciiKeyTextField.setText(keyTextField.getText());

        keyTextField.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingKey) {
                        isUpdatingKey = true;
                        asciiKeyTextField.setText(keyTextField.getText());
                        isUpdatingKey = false;
                    }
                }));

        asciiKeyTextField.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingKey) {
                        isUpdatingKey = true;
                        keyTextField.setText(asciiKeyTextField.getText());
                        isUpdatingKey = false;
                    }
                }));
    }

    private void setupInputSynchronization(JTextArea asciiInputTextArea) {
        inputTextArea.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingInput) {
                        isUpdatingInput = true;
                        asciiInputTextArea.setText(inputTextArea.getText());
                        isUpdatingInput = false;
                    }
                }));

        asciiInputTextArea.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingInput) {
                        isUpdatingInput = true;
                        inputTextArea.setText(asciiInputTextArea.getText());
                        isUpdatingInput = false;
                    }
                }));
    }

    private void setupOutputSynchronization(JTextArea asciiOutputTextArea) {
        outputTextArea.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingOutput) {
                        isUpdatingOutput = true;
                        asciiOutputTextArea.setText(outputTextArea.getText());
                        isUpdatingOutput = false;
                    }
                }));

        asciiOutputTextArea.getDocument().addDocumentListener(createDocumentListener(
                () -> {
                    if (!isUpdatingOutput) {
                        isUpdatingOutput = true;
                        outputTextArea.setText(asciiOutputTextArea.getText());
                        isUpdatingOutput = false;
                    }
                }));
    }

    private DocumentListener createDocumentListener(Runnable updateAction) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateAction.run();
            }

            public void removeUpdate(DocumentEvent e) {
                updateAction.run();
            }

            public void changedUpdate(DocumentEvent e) {
                updateAction.run();
            }
        };
    }

    private JPanel createAsciiButtonPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        Dimension asciiButtonSize = new Dimension(100, 30);
        JButton[] asciiButtons = {
                new JButton("加密"), new JButton("解密"), new JButton("清空"),
                new JButton("交换"), new JButton("复制结果")
        };

        for (JButton button : asciiButtons) {
            button.setPreferredSize(asciiButtonSize);
            bottomPanel.add(button);
        }

        // 为ASCII模式按钮添加监听器
        addAsciiButtonListeners(asciiButtons);

        return bottomPanel;
    }

    private void addBinaryButtonListeners(JButton[] buttons) {
        buttons[0].addActionListener(e -> {
            isBinaryMode = true;
            performEncryption();
        });
        buttons[1].addActionListener(e -> {
            isBinaryMode = true;
            performDecryption();
        });
        buttons[2].addActionListener(e -> clearTextAreas());
        buttons[3].addActionListener(e -> swapTextAreas());
        buttons[4].addActionListener(e -> copyOutputToClipboard());

        bruteForceButton.addActionListener(e -> openBruteForceTool());
    }

    private void addAsciiButtonListeners(JButton[] buttons) {
        buttons[0].addActionListener(e -> {
            isBinaryMode = false;
            performEncryption();
        });
        buttons[1].addActionListener(e -> {
            isBinaryMode = false;
            performDecryption();
        });
        buttons[2].addActionListener(e -> clearTextAreas());
        buttons[3].addActionListener(e -> swapTextAreas());
        buttons[4].addActionListener(e -> copyOutputToClipboard());
    }

    private void addEventListeners() {
        binaryModeButton.addActionListener(e -> switchToBinaryMode());
        asciiModeButton.addActionListener(e -> switchToAsciiMode());
    }

    private void switchToBinaryMode() {
        cardLayout.show(cardPanel, "binary");
        isBinaryMode = true;
        updateLabelsForMode(true);
    }

    private void switchToAsciiMode() {
        cardLayout.show(cardPanel, "ascii");
        isBinaryMode = false;
        updateLabelsForMode(false);
    }

    private void openBruteForceTool() {
        BruteForceSDES bruteForceWindow = new BruteForceSDES();
        bruteForceWindow.setVisible(true);
    }

    private void updateLabelsForMode(boolean isBinaryMode) {
        if (isBinaryMode) {
            inputLabel.setText("输入文本 (8位二进制):");
            outputLabel.setText("输出结果 (8位二进制):");
        } else {
            inputLabel.setText("输入文本 (ASCII):");
            outputLabel.setText("输出结果 (ASCII):");
        }
    }

    private void performEncryption() {
        try {
            String input = inputTextArea.getText();
            String key = keyTextField.getText().trim();

            if (!validateKey(key))
                return;

            SDESCore sdes = new SDESCore();
            String result = isBinaryMode ? sdes.encrypt(input, key) : sdes.encryptASCII(input, key);

            outputTextArea.setText(result);

        } catch (Exception ex) {
            showErrorMessage("加密过程中出现错误: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void performDecryption() {
        try {
            String input = inputTextArea.getText();
            String key = keyTextField.getText().trim();

            if (!validateKey(key))
                return;

            SDESCore sdes = new SDESCore();
            String result = isBinaryMode ? sdes.decrypt(input, key) : sdes.decryptASCII(input, key);

            outputTextArea.setText(result);

        } catch (Exception ex) {
            showErrorMessage("解密过程中出现错误: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validateKey(String key) {
        if (key.isEmpty() || !CommonUtils.isValidBinary(key, 10)) {
            showErrorMessage("请输入10位二进制密钥！");
            return false;
        }
        return true;
    }

    private void clearTextAreas() {
        inputTextArea.setText("");
        outputTextArea.setText("");
    }

    private void swapTextAreas() {
        String input = inputTextArea.getText();
        String output = outputTextArea.getText();
        inputTextArea.setText(output);
        outputTextArea.setText(input);
    }

    private void copyOutputToClipboard() {
        String output = outputTextArea.getText();
        if (!output.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new java.awt.datatransfer.StringSelection(output), null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}