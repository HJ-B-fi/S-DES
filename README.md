# S-DES
# 🔐 S-DES 算法实现（Java + Swing GUI）

> 《信息安全导论》课程作业 1  
> 题目：S-DES 加解密算法实现与暴力破解  
> 语言：Java（使用 Swing 实现 GUI）  
> 小组成员：张三、李四  
> 提交日期：2025-10-08

---
.
├── core/
│   ├── SDESCore.java         # 统一出入口：密钥生成 + 加/解密流程
│   ├── KeyGenerator.java     # 10-bit 主密钥 → 8-bit 子密钥 K1/K2
│   ├── Encryption.java       # 加密（两轮 Feistel + IP / IP^-1）
│   └── Decryption.java       # 解密（轮密钥顺序反向）
├── utils/
│   ├── CommonUtils.java      # 置换、异或、循环左移、二进制/字符转换、校验
│   └── SBoxManager.java      # S0 / S1 盒与替换逻辑
├── ui/
│   ├── SDESGUI.java          # 主界面（Binary/ASCII 双模式）
│   └── BruteForceSDES.java   # 暴力破解子窗体（多线程、进度条）
└── Main.java                 # 程序入口（启动 GUI）



## 📖 项目简介

本项目实现了教学用简化数据加密标准（Simplified DES, S-DES）算法，包括：

- 密钥扩展（10-bit → 两个子密钥 K1, K2）
- 加密与解密（8-bit 分组）
- GUI 图形化界面（基于 Java Swing）
- ASCII 字符串加密 / 解密
- 暴力破解（穷举 1024 个 10-bit 密钥，支持多线程）
- 封闭测试（分析密钥碰撞）

项目目标是掌握 Feistel 结构、置换盒（P-box）、S-box、以及对称加密算法的基本原理。

---

## 🧱 功能模块

| 模块 | 功能描述 |
|------|-----------|
| `SDES.java` | 核心算法模块，负责加解密、密钥扩展、S-Box 与置换 |
| `SDESGui.java` | 图形界面，支持用户输入密钥与明文、显示密文 |
| `BruteForcer.java` | 暴力破解实现，穷举 1024 种 10-bit 密钥 |
| `Main.java` | 程序入口，创建并启动 GUI 窗口 |

---

## 🖥️ 运行环境

- **操作系统**：Windows / macOS / Linux  
- **Java 版本**：JDK 11 或更高  
- **开发工具**：VS Code / Eclipse / IntelliJ IDEA  
- **依赖**：无第三方依赖，仅使用标准库 `javax.swing`

---

## 🚀 使用方法

### 🧩 1. 编译与运行（命令行方式）



---

## 作业要求与对应实现

**1) 实现 S-DES 的加密/解密流程（8 位分组）**  
- 流程：`IP → Round(K1) → 交换 → Round(K2) → IP⁻¹`；解密时轮密钥顺序相反（K2、K1）。  
- 代码：`Encryption.java`、`Decryption.java`；流程调度在 `SDESCore.java`。

**2) 密钥调度（10 位主密钥 → K1、K2）**  
- `P10 → (L/R 各左移 1) → P8 = K1`；再在此基础上 **各左移 2**、`P8 = K2`。  
- 代码：`KeyGenerator.java`；置换/左移在 `CommonUtils.java`。

**3) S 盒定义与查表**  
- `SBoxManager.java` 提供 S0/S1 与 `substitute`；行=首末位，列=中间两位，输出 2 位结果拼 4 位。

**4) ASCII 与二进制两种模式**  
- Binary：输入/输出均为 **8 位二进制**。  
- ASCII：逐字符转 8 位二进制处理，再还原字符。  
- 代码：`SDESCore#encrypt/decrypt`（二进制）；`SDESCore#encryptASCII/decryptASCII`（ASCII）；GUI 在 `SDESGUI.java` 里切换/校验/同步。

**5) 暴力破解（Brute Force）**  
- 穷举 2¹⁰=1024 个候选密钥，支持“**找到第一把**”/“**找到全部**”两种模式；默认多线程分片，带进度条与统计。  
- 代码：`BruteForceSDES.java`；入口按钮在 `SDESGUI.java`。

---

## 环境与构建

- **JDK**：8+（推荐 11 或更高）
- **外部依赖**：无
- **编译/运行**（跨平台）：

### Linux / macOS（bash/zsh）
```bash
# 1) 清理与输出目录
rm -rf out && mkdir -p out
# 2) 编译当前目录全部 Java 源文件
javac -encoding UTF-8 -d out *.java
# 3) 运行（GUI）
java -cp out Main
