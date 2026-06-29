# 声探体验版 v1.4.0 发布说明 / Release Notes

## 📦 发布包 / Release Artifacts

| 文件 / File | 大小 / Size | 说明 / Description |
|---|---|---|
| `SoundScope-v1.4.0-Release-Signed.apk` | 12 MB | Release 签名 APK（v2 签名，可上架）/ Release signed APK (v2 signature, store-ready) |
| `SoundScope-v1.4.0-i18n-Debug.apk` | 18 MB | Debug APK（仅测试用）/ Debug APK (testing only) |
| `Source code (zip/tar.gz)` | - | 完整源码 / Full source code |

---

## 🆕 v1.4.0 更新内容 / What's New

### 🌍 多语言扩展 / Multi-language Expansion
本次版本将支持语言从 2 种扩展到 **9 种**：

| 语言 / Language | 代码 / Code | 状态 / Status |
|---|---|---|
| 简体中文 / Simplified Chinese | `zh-CN` | ✅ 已有 / Existing |
| 繁体中文 / Traditional Chinese | `zh-TW` | 🆕 新增 / New |
| 英语 / English | `en` | ✅ 已有 / Existing |
| 法语 / French | `fr` | 🆕 新增 / New |
| 葡萄牙语 / Portuguese | `pt` | 🆕 新增 / New |
| 西班牙语 / Spanish | `es` | 🆕 新增 / New |
| 阿拉伯语 / Arabic | `ar` | 🆕 新增 / New |
| 俄语 / Russian | `ru` | 🆕 新增 / New |
| 印地语 / Hindi | `hi` | 🆕 新增 / New |

### 🔄 其他变更 / Other Changes
- 🏷️ 应用名称更改为"声探体验版"
- 📄 开源协议从 MIT 更改为 AGPL v3
- 🌐 阿拉伯语 RTL 布局支持
- 🔑 Release 签名 APK

---

## 📋 系统要求 / System Requirements

- **Android 版本 / Android Version**: 8.0 (API 26) 或更高 / or later
- **架构 / Architecture**: arm64-v8a, armeabi-v7a, x86_64
- **存储空间 / Storage**: ~15 MB 安装空间 / Installation space
- **权限 / Permissions**: 麦克风（录音分析）/ Microphone (audio analysis)

---

## 🔐 签名信息 / Signature Info

- **签名方案 / Signature Scheme**: APK Signature Scheme v2
- **证书主体 / Certificate Subject**: CN=SoundScope, OU=Dev, O=0xzzyyxx, L=Shenzhen, ST=Guangdong, C=CN
- **有效期 / Validity**: 100 年（至 2126 年）/ 100 years (until 2126)

---

## 📥 安装方式 / Installation

### 直接安装 APK / Direct APK Install
1. 下载 `SoundScope-v1.4.0-Release-Signed.apk`
2. 在手机设置中允许"安装未知来源应用" / Enable "Install from unknown sources" in phone settings
3. 点击 APK 文件安装 / Tap the APK file to install

### 从源码构建 / Build from Source
```bash
git clone https://github.com/0xzzyyxx/soundscope.git
cd soundscope
git checkout v1.4.0
./gradlew assembleRelease
# 输出 / Output: app/build/outputs/apk/release/app-release.apk
```

---

## 🌐 发布渠道 / Distribution Channels

### 国内 / China
- [ ] GitHub Release
- [ ] Gitee Release
- [ ] 华为应用市场 / Huawei AppGallery
- [ ] 小米应用商店 / Xiaomi GetApps
- [ ] 腾讯应用宝 / Tencent MyApp
- [ ] OPPO 软件商店 / OPPO Software Store
- [ ] vivo 应用商店 / vivo App Store

### 国际 / International
- [ ] GitHub Release
- [ ] Google Play Store
- [ ] F-Droid (待申请 / Pending)
- [ ] APKPure
- [ ] Aptoide

---

## ⚠️ 已知问题 / Known Issues

1. **AI 提示词 / AI Prompts**: 当前仅中英文两套提示词，其他语言环境默认使用英文提示词 / Currently only Chinese and English prompts; other languages default to English
2. **已弃用图标 / Deprecated Icons**: `Icons.Filled.Send` 和 `ExitToApp` 已弃用，建议迁移至 `AutoMirrored` 版本以更好支持 RTL / Deprecated, recommend migrating to `AutoMirrored` versions for better RTL support
3. **截图 / Screenshots**: 首次发布暂无截图，将在后续版本补充 / No screenshots for initial release, will be added in future versions

---

## 📞 反馈 / Feedback

- **GitHub Issues**: [提交问题 / Report Issue](https://github.com/0xzzyyxx/soundscope/issues)
- **Gitee Issues**: [提交问题 / Report Issue](https://gitee.com/0xzzyyxx/soundscope/issues)
- **邮箱 / Email**: 通过 GitHub/Gitee 联系 / Contact via GitHub/Gitee

---

## 📄 协议 / License

Copyright © 2026 0xzzyyxx. 本项目基于 [GNU AGPL v3](LICENSE) 开源。

This project is licensed under [GNU AGPL v3](LICENSE).
