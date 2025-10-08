# S-DES
# 🔐 S-DES 算法实现（Java + Swing GUI）

> 《信息安全导论》课程作业 1  
> 题目：S-DES 加解密算法实现与暴力破解  
> 语言：Java（使用 Swing 实现 GUI）  
> 小组成员：张三、李四  
> 提交日期：2025-10-08


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

## 🧪 关卡测试

### 第1关：基本测试 ✅
开发了完整的S-DES算法实现，并提供了图形用户界面，支持用户交互式操作，支持8位二进制数据和10位二进制密钥的加解密。

输入：8位明文 + 10位密钥 → 输出：8位密文

核心算法模块包括：密钥生成器、加密模块、解密模块

界面支持二进制模式和ASCII模式切换
***
#### 加密解密
输入8位二进制明/密文，10位二进制密钥，点击加/解密按钮后得到8位二进制密/明文

<img width="552" height="331" alt="联想截图_20251008193506" src="https://github.com/user-attachments/assets/44c935fa-98bd-452f-a65a-35114dbd829d" />

### 第2关：交叉测试 ✅
严格遵循标准S-DES算法流程，使用统一的置换盒(P-Box)和替换盒(S-Box)

算法组件标准化：

- P10置换表：{3,5,2,7,4,10,1,9,8,6}
- P8置换表：{6,3,7,4,8,5,10,9}
- 初始IP置换：{2,6,3,1,4,8,5,7}
- 逆初始IP置换：{4,1,3,5,7,2,8,6}

S0盒和S1盒采用标准定义

测试结果：不同小组使用相同密钥对同一明文加密，均得到相同密文；解密也能正确还原明文

#### 约定密钥为：0000000000

> B组使用明文：10101010
> 
> B组得到密文：00010001

## 欣悦的图

> A组已知密文：00010001
> 
> A组解密得到：10101010

<img width="552" height="331" alt="联想截图_20251008193317" src="https://github.com/user-attachments/assets/4d27a209-9833-4c8c-b902-a7b0f74e1102" />


### 第3关：扩展功能 ✅
扩展支持ASCII字符串的加解密功能

输入处理：将ASCII字符转换为8位二进制分组

输出处理：将加密后的二进制数据转换回ASCII字符


<img width="552" height="331" alt="联想截图_20251008202610" src="https://github.com/user-attachments/assets/d0eb5213-9759-4036-bd2b-ef8febd9c572" />


### 第4关：暴力破解 ✅

密钥空间：2¹⁰ = 1024个可能密钥

采用多线程技术，默认使用4个线程并行破解

每个线程处理256个密钥（1024/4）
#### 破解演示


<img width="589" height="367" alt="联想截图_20251008202732" src="https://github.com/user-attachments/assets/789d3fc2-a731-451f-9273-a63ed1af8ab5" />


#### 破解性能：

平均破解时间：< 50毫秒

测试环境：普通个人计算机

支持多组明密文对验证，提高破解准确性
### 第5关：封闭测试 ✅
#### 密钥唯一性分析
对于给定的明密文对，可能存在多个有效密钥

测试发现：使用不同数量的明密文对会影响找到的密钥数量

- 单对明密文对可能对应多个密钥

<img width="589" height="367" alt="联想截图_20251008202947" src="https://github.com/user-attachments/assets/9e0fa02b-1a8d-4199-8f8a-bab463687e43" />

- 多对明密文对可唯一确定密钥

<img width="589" height="368" alt="联想截图_20251008203402" src="https://github.com/user-attachments/assets/08663fc5-871b-4372-8b93-98e65cc84acb" />

#### 密钥碰撞分析


分析结果：存在密钥碰撞现象

<img width="589" height="339" alt="联想截图_20251008203818" src="https://github.com/user-attachments/assets/4ad7a38e-0afc-4577-8a1b-5c18419e6272" />

部分不同密钥加密同一明文会产生相同密文

最大碰撞数：观察到最多有6个不同密钥对应同一密文

<img width="384" height="27" alt="联想截图_20251008204004" src="https://github.com/user-attachments/assets/fdc2e5b3-6785-4248-b8f0-2b4706d39f96" />

#### 碰撞统计：

测试明文数量：可变（用户自定义）

总碰撞次数：依赖测试数据

平均每个明文对应的密钥数：4个（1024/256）

<img width="173" height="81" alt="联想截图_20251008203922" src="https://github.com/user-attachments/assets/7fef05a2-c826-46fc-a405-94d8c2c72838" />


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
