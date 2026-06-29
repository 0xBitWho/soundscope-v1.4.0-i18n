# 声探体验版 / SoundScope

<p align="center">
  <strong>基于 AI 的智能声音分析工具</strong><br>
  <sub>AI-powered intelligent sound analysis tool</sub>
</p>

<p align="center">
  <img alt="Version" src="https://img.shields.io/badge/version-1.4.0-blue">
  <img alt="License" src="https://img.shields.io/badge/license-AGPL%20v3-green">
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android%208.0%2B-orange">
  <img alt="Language" src="https://img.shields.io/badge/languages-9-yellow">
</p>

---

## 📖 项目简介 / Introduction

**中文**：声探体验版是一款基于 Android 平台的智能声音分析应用，集成 AI 大模型能力，可实时测量环境噪音分贝、识别声音场景，并提供智能分析建议。支持 9 种语言，适合全球用户使用。

**English**: SoundScope is an Android-based intelligent sound analysis app that integrates AI large model capabilities. It can measure environmental noise decibels in real-time, recognize sound scenes, and provide intelligent analysis suggestions. Supports 9 languages for global users.

---

## ✨ 核心功能 / Features

| 功能 / Feature | 说明 / Description |
|---|---|
| 🎵 实时分贝测量 / Real-time dB Meter | 实时监测环境噪音水平，支持 A/C/Z 计权 / Real-time environmental noise monitoring with A/C/Z weighting |
| 🤖 AI 智能分析 / AI Analysis | 集成大语言模型，智能识别声音场景并提供建议 / Integrated LLM for intelligent scene recognition and suggestions |
| 📊 历史记录 / History | 自动保存测量记录，支持查看和导出 / Auto-save measurement records with view and export |
| 🌍 多语言 / Multi-language | 支持简中、繁中、英、法、葡、西、阿、俄、印地 9 种语言 / 9 languages: zh-CN, zh-TW, en, fr, pt, es, ar, ru, hi |
| 🌐 RTL 支持 / RTL Support | 完整支持阿拉伯语等从右到左语言 / Full support for RTL languages like Arabic |
| 🎨 主题切换 / Theme | 支持浅色/深色/跟随系统主题 / Light/Dark/System theme |
| 🔒 隐私优先 / Privacy First | 所有数据本地存储，AI 调用可配置 / All data stored locally, AI calls configurable |

---

## 📱 截图 / Screenshots

> 截图将在首次发布后补充 / Screenshots will be added after first release

---

## 🚀 下载安装 / Download & Install

### 方式一：直接下载 APK / Direct APK Download
从 [Releases](../../releases) 页面下载最新 `SoundScope-v1.4.0-Release-Signed.apk`，允许"安装未知来源应用"后安装。

Download the latest `SoundScope-v1.4.0-Release-Signed.apk` from the [Releases](../../releases) page. Enable "Install from unknown sources" and install.

### 方式二：从源码构建 / Build from Source
```bash
git clone https://github.com/0xzzyyxx/soundscope.git
cd soundscope
./gradlew assembleRelease
```

**环境要求 / Requirements:**
- Android Studio Hedgehog (2023.1.1) 或更高 / or later
- JDK 17
- Android SDK 34
- Gradle 8.5

---

## 🌍 支持语言 / Supported Languages

| 语言 / Language | 代码 / Code | 原生名称 / Native Name |
|---|---|---|
| 简体中文 / Simplified Chinese | `zh-CN` | 简体中文 |
| 繁体中文 / Traditional Chinese | `zh-TW` | 繁體中文 |
| 英语 / English | `en` | English |
| 法语 / French | `fr` | Français |
| 葡萄牙语 / Portuguese | `pt` | Português |
| 西班牙语 / Spanish | `es` | Español |
| 阿拉伯语 / Arabic | `ar` | العربية |
| 俄语 / Russian | `ru` | Русский |
| 印地语 / Hindi | `hi` | हिन्दी |

---

## ⚙️ AI 配置 / AI Configuration

应用支持自定义 AI 大模型 API。在设置页面配置：

The app supports custom AI large model API. Configure in Settings:

1. **API 地址 / API URL**: 兼容 OpenAI 格式的接口地址 / OpenAI-compatible API endpoint
2. **API Key**: 你的密钥 / Your API key
3. **模型名称 / Model Name**: 如 `gpt-4o-mini`、`deepseek-chat` 等 / e.g., `gpt-4o-mini`, `deepseek-chat`

> 💡 推荐使用 [DeepSeek](https://platform.deepseek.com/) 或 [OpenAI](https://platform.openai.com/) 的 API

---

## 🛠️ 技术栈 / Tech Stack

- **语言 / Language**: Kotlin
- **UI 框架 / UI Framework**: Jetpack Compose + Material 3
- **架构 / Architecture**: MVVM + Repository Pattern
- **数据库 / Database**: Room
- **偏好存储 / Preferences**: DataStore
- **网络 / Network**: OkHttp
- **国际化 / i18n**: Android Resource Qualifiers + AppCompatDelegate
- **最低 SDK / Min SDK**: Android 8.0 (API 26)
- **目标 SDK / Target SDK**: Android 14 (API 34)

---

## 📋 版本历史 / Changelog

### v1.4.0 (2026-06-29)
- ✨ 新增 7 种语言：法语、葡萄牙语、西班牙语、阿拉伯语、俄语、印地语、繁体中文
- ✨ 新增阿拉伯语 RTL（从右到左）布局支持
- ✨ 设置页新增 10 个语言切换选项（含跟随系统）
- 🔄 开源协议从 MIT 更改为 AGPL v3
- 🏷️ 应用名称更改为"声探体验版"

### v1.3.0
- ✨ 国际化改造，支持简体中文和英文
- ✨ AI 提示词根据语言环境自动切换
- ✨ 设置页新增语言切换功能

### v1.2.0
- ✨ 集成 AI 大模型分析功能
- ✨ 新增声音场景识别

### v1.1.0
- ✨ 基础分贝测量功能
- ✨ 历史记录管理

### v1.0.0
- 🎉 初始版本

---

## 📄 开源协议 / License

本项目基于 [GNU Affero General Public License v3.0](LICENSE) 开源。

This project is licensed under the [GNU Affero General Public License v3.0](LICENSE).

> ⚠️ **重要 / Important**: AGPL v3 要求任何通过网络提供服务的衍生作品也必须开源源代码。/ AGPL v3 requires that derivative works providing services over a network must also open-source their source code.

---

## 👨‍💻 开发者 / Developer

**0xzzyyxx**

- GitHub: [@0xzzyyxx](https://github.com/0xzzyyxx)
- Gitee: [@0xzzyyxx](https://gitee.com/0xzzyyxx)

---

## 🤝 贡献 / Contributing

欢迎提交 Issue 和 Pull Request！

Issues and Pull Requests are welcome!

1. Fork 本仓库 / Fork this repository
2. 创建特性分支 / Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 提交更改 / Commit changes (`git commit -m 'Add amazing feature'`)
4. 推送分支 / Push to branch (`git push origin feature/amazing-feature`)
5. 开启 Pull Request / Open a Pull Request

---

## ⭐ 致谢 / Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) — 现代 Android UI 工具包
- [Material 3](https://m3.material.io/) — 设计系统
- [Android Open Source Project](https://source.android.com/) — Android 平台

---

## 📞 联系方式 / Contact

- 提交 Issue: [GitHub Issues](../../issues)
- 邮箱 / Email: 通过 GitHub 联系 / Contact via GitHub

---

<p align="center">
  Made with ❤️ by 0xzzyyxx<br>
  Copyright © 2026 0xzzyyxx. All rights reserved under AGPL v3.
</p>
