# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.4.0] - 2026-06-29

### Added
- 🌍 新增 7 种语言支持：法语 (fr)、葡萄牙语 (pt)、西班牙语 (es)、阿拉伯语 (ar)、俄语 (ru)、印地语 (hi)、繁体中文 (zh-TW)
- 🌐 阿拉伯语 RTL（从右到左）布局完整支持，通过 `CompositionLocalProvider` + `LocalLayoutDirection` 实现
- ⚙️ 设置页新增 10 个语言切换选项（简中、繁中、英、法、葡、西、阿、俄、印地、跟随系统）
- 📝 每种语言 214 条字符串，与默认中文版本 1:1 对齐
- 🔑 Release 签名配置，生成可上架的签名 APK

### Changed
- 🏷️ 应用名称从"声探"更改为"声探体验版"（简中）/ "聲探體驗版"（繁中）
- 📄 开源协议从 MIT License 更改为 GNU AGPL v3
- 📦 versionCode 从 4 升级到 5，versionName 从 1.3.0 升级到 1.4.0
- 📖 关于页应用名称显示更新为"声探体验版 SoundScope"

### Fixed
- N/A（本次为功能扩展版本，无 Bug 修复）

---

## [1.3.0] - 2026-06-15

### Added
- 🌍 国际化改造，支持简体中文 (zh-CN) 和英文 (en) 两种语言
- 🤖 AI 提示词根据语言环境自动切换（中文环境用中文提示词，英文环境用英文提示词）
- ⚙️ 设置页新增语言切换功能（跟随系统 / 简体中文 / English）
- 🔧 `LocaleManager` 工具类，封装语言检测和应用逻辑
- 📦 DataStore 持久化 `languageMode` 偏好设置

### Changed
- 📦 versionCode 从 3 升级到 4，versionName 从 1.2.0 升级到 1.3.0
- 🏗️ `DecibelLevel`、`SceneMode`、`AiProvider` 枚举重构为使用 `@StringRes` 资源 ID
- 🎨 主题父类从 `android:Theme.Material.NoActionBar` 更改为 `Theme.AppCompat.NoActionBar`
- 🏗️ `MainActivity` 从 `ComponentActivity` 改为 `AppCompatActivity`

### Fixed
- 修复非 ASCII 项目路径（"源码"）导致 AGP 构建失败的问题（添加 `android.overridePathCheck=true`）
- 修复 `Alignment.CenterHorizontally` 在 `Row` 中的类型不匹配错误
- 修复 `AppCompatDelegate` 不可用的问题（添加 appcompat 依赖）

---

## [1.2.0] - 2026-05-XX

### Added
- 🤖 集成 AI 大模型分析功能，支持 OpenAI 兼容 API
- 🎵 新增声音场景识别（办公室、街道、家庭、自然等）
- ⚙️ 设置页新增 AI 配置（API URL、Key、Model）
- 📊 AI 分析结果展示和保存

---

## [1.1.0] - 2026-04-XX

### Added
- 🎵 实时分贝测量功能（A/C/Z 计权）
- 📊 历史记录管理（Room 数据库）
- 🎨 浅色/深色/跟随系统主题
- 📱 基础 UI 框架搭建

---

## [1.0.0] - 2026-03-XX

### Added
- 🎉 项目初始版本
- 📱 基础项目结构和构建配置
