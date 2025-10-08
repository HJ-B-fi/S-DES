# S-DES
# 🔐 S-DES 算法实现（Java + Swing GUI）

> 《信息安全导论》课程作业 1  
> 题目：S-DES 加解密算法实现与暴力破解  
> 语言：Java（使用 Swing 实现 GUI）  
> 小组成员：张三、李四  
> 提交日期：2025-10-08

---

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

```bash
# 编译
javac -d out src/sdes/*.java

# 运行
java -cp out sdes.Main
