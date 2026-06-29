# 声探体验版 — 应用市场上架指南 / App Store Publishing Guide

> 本文档详细说明如何将声探体验版 v1.4.0 发布到各大应用市场。
> This document details how to publish SoundScope v1.4.0 to major app stores.

---

## 📋 通用准备 / Common Preparation

### 1. 发布包 / Release Artifacts
- **APK 文件 / APK File**: `SoundScope-v1.4.0-Release-Signed.apk` (12 MB)
- **签名方案 / Signature**: APK Signature Scheme v2
- **包名 / Package Name**: `com.soundscope.app`
- **版本号 / Version**: 1.4.0 (versionCode: 5)

### 2. 应用信息 / App Information

| 字段 / Field | 值 / Value |
|---|---|
| 应用名称（中文）/ App Name (CN) | 声探体验版 |
| 应用名称（英文）/ App Name (EN) | SoundScope |
| 开发者 / Developer | 0xzzyyxx |
| 开源协议 / License | AGPL v3 |
| 官方仓库 / Official Repo | https://github.com/0xzzyyxx/soundscope |

### 3. 应用描述 / App Description

**简短描述（80字内）/ Short Description (max 80 chars)**:
```
基于AI的智能声音分析工具，实时测量环境噪音分贝，支持9种语言。
```

**完整描述 / Full Description**:
```
声探体验版是一款基于 Android 平台的智能声音分析应用，集成 AI 大模型能力，可实时测量环境噪音分贝、识别声音场景，并提供智能分析建议。

【核心功能】
• 实时分贝测量：支持 A/C/Z 计权，精准监测环境噪音
• AI 智能分析：集成大语言模型，智能识别声音场景并提供建议
• 历史记录：自动保存测量记录，支持查看和管理
• 多语言：支持简中、繁中、英、法、葡、西、阿、俄、印地 9 种语言
• RTL 支持：完整支持阿拉伯语等从右到左语言
• 主题切换：浅色/深色/跟随系统
• 隐私优先：所有数据本地存储，AI 调用可配置

【使用场景】
• 环境噪音监测（办公室、街道、家庭）
• 声音场景智能识别
• 听力健康保护参考

【技术特点】
• 基于 Jetpack Compose 构建，原生 Material 3 设计
• 完全开源（AGPL v3），代码托管在 GitHub
• 支持 Android 8.0 及以上设备

【关于体验版】
本版本为体验版，欢迎反馈使用体验和功能建议。
```

### 4. 应用截图要求 / Screenshot Requirements
- **尺寸 / Size**: 1080×1920 px（或更高 / or higher）
- **格式 / Format**: PNG/JPG
- **数量 / Count**: 3-8 张（各市场要求不同 / varies by store）
- **内容建议 / Content Suggestions**:
  1. 主界面（分贝测量中）/ Main screen (measuring)
  2. AI 分析结果页 / AI analysis result
  3. 历史记录页 / History page
  4. 设置页（语言切换）/ Settings (language switch)
  5. 关于页 / About page

### 5. 应用图标 / App Icon
- **源文件 / Source**: `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
- **尺寸 / Size**: 512×512 px（应用市场要求 / store requirement）

---

## 🇨🇳 国内应用市场 / China App Stores

### 华为应用市场 / Huawei AppGallery
- **网址 / URL**: https://developer.huawei.com/consumer/cn/agconnect/
- **注册要求 / Registration**: 个人开发者需身份证 + 人脸识别；企业需营业执照
- **审核周期 / Review**: 3-5 个工作日 / business days
- **特殊要求 / Special Requirements**:
  - 需提供《软件著作权》或开源证明 / Software copyright or open-source proof
  - 需填写隐私政策 URL / Privacy policy URL required
  - APK 需通过华为签名校验 / APK must pass Huawei signature verification

**上架步骤 / Steps**:
1. 注册华为开发者账号 / Register Huawei developer account
2. 创建应用，填写基本信息 / Create app, fill basic info
3. 上传 APK / Upload APK
4. 填写应用描述、截图、图标 / Fill description, screenshots, icon
5. 提交审核 / Submit for review

### 小米应用商店 / Xiaomi GetApps
- **网址 / URL**: https://dev.mi.com/
- **注册要求 / Registration**: 个人开发者需身份证；企业需营业执照
- **审核周期 / Review**: 2-3 个工作日 / business days
- **特殊要求 / Special Requirements**:
  - 需提供隐私政策 / Privacy policy required
  - 需提供应用权限说明 / App permission description required

### 腾讯应用宝 / Tencent MyApp
- **网址 / URL**: https://open.tencent.com/
- **注册要求 / Registration**: 个人开发者需身份证 + 银行卡；企业需营业执照
- **审核周期 / Review**: 1-3 个工作日 / business days
- **特殊要求 / Special Requirements**:
  - 需提供软件著作权或开源协议证明 / Software copyright or open-source license proof
  - 需通过腾讯安全检测 / Must pass Tencent security check

### OPPO 软件商店 / OPPO Software Store
- **网址 / URL**: https://open.oppomobile.com/
- **注册要求 / Registration**: 个人开发者需身份证；企业需营业执照
- **审核周期 / Review**: 1-3 个工作日 / business days

### vivo 应用商店 / vivo App Store
- **网址 / URL**: https://dev.vivo.com.cn/
- **注册要求 / Registration**: 个人开发者需身份证；企业需营业执照
- **审核周期 / Review**: 1-3 个工作日 / business days

---

## 🌍 国际应用市场 / International App Stores

### Google Play Store
- **网址 / URL**: https://play.google.com/console
- **注册费用 / Registration Fee**: $25（一次性 / one-time）
- **注册要求 / Registration**: Google 账号 + 信用卡/借记卡
- **审核周期 / Review**: 1-7 天 / days
- **特殊要求 / Special Requirements**:
  - 需提供隐私政策 URL / Privacy policy URL required
  - 需填写数据安全表单 / Data safety form required
  - 需提供内容分级（IARC 问卷）/ Content rating (IARC questionnaire)
  - 目标 API 级别需满足最新要求（目前需 API 34+）/ Target API level must meet latest requirement

**上架步骤 / Steps**:
1. 注册 Google Play 开发者账号 / Register Google Play developer account
2. 创建应用，填写基本信息 / Create app, fill basic info
3. 上传 AAB（推荐）或 APK / Upload AAB (recommended) or APK
    - 注意：Google Play 要求 AAB 格式 / Note: Google Play requires AAB format
    - 构建命令 / Build command: `./gradlew bundleRelease`
4. 填写商品详情（描述、截图、图标、特色图）/ Fill store listing
5. 完成数据安全表单 / Complete data safety form
6. 完成内容分级 / Complete content rating
7. 选择目标受众 / Select target audience
8. 提交审核 / Submit for review

### F-Droid
- **网址 / URL**: https://f-droid.org/
- **注册要求 / Registration**: 无需注册，提交应用信息到 F-Droid 仓库
- **审核周期 / Review**: 数周到数月 / weeks to months
- **特殊要求 / Special Requirements**:
  - 必须 100% 开源（包括所有依赖）/ Must be 100% open source (including all dependencies)
  - AGPL v3 协议符合要求 / AGPL v3 is compliant
  - 需提交到 [fdroiddata](https://gitlab.com/fdroid/fdroiddata) 仓库 / Submit to fdroiddata repo
  - 构建需可复现 / Build must be reproducible

**上架步骤 / Steps**:
1. 确认所有依赖均为开源 / Verify all dependencies are open source
2. 在 fdroiddata 仓库提交 Merge Request / Submit MR to fdroiddata repo
3. 等待社区审核 / Wait for community review
4. 通过后自动构建和发布 / Auto build and publish after approval

### APKPure / Aptoide
- **网址 / URL**: https://apkpure.com/ / https://www.aptoide.com/
- **注册要求 / Registration**: 免费注册 / Free registration
- **审核周期 / Review**: 1-2 天 / days
- **特点 / Features**: 适合无法访问 Google Play 的用户 / Suitable for users without Google Play access

---

## 📝 代码托管平台 / Code Hosting Platforms

### GitHub
- **仓库 URL / Repo URL**: https://github.com/0xzzyyxx/soundscope
- **操作 / Actions**:
  1. 创建公开仓库 / Create public repository
  2. 推送代码 / Push code:
     ```bash
     git init
     git add .
     git commit -m "Release v1.4.0: Multi-language expansion"
     git branch -M main
     git remote add origin https://github.com/0xzzyyxx/soundscope.git
     git push -u origin main
     ```
  3. 创建 Release / Create release:
     - Tag: `v1.4.0`
     - Title: `声探体验版 v1.4.0 - 多语言扩展`
     - 上传 `SoundScope-v1.4.0-Release-Signed.apk`
     - 粘贴 `RELEASE-NOTES-v1.4.0.md` 内容

### Gitee
- **仓库 URL / Repo URL**: https://gitee.com/0xzzyyxx/soundscope
- **操作 / Actions**:
  1. 创建公开仓库 / Create public repository
  2. 推送代码（可从 GitHub 镜像）/ Push code (can mirror from GitHub):
     ```bash
     git remote add gitee https://gitee.com/0xzzyyxx/soundscope.git
     git push gitee main
     ```
  3. 创建 Release / Create release
  4. 可开启 GitHub 自动镜像 / Can enable GitHub auto-mirror

---

## 🔒 隐私政策 / Privacy Policy

应用市场通常要求提供隐私政策 URL。建议创建一个简单的隐私政策页面：

App stores typically require a privacy policy URL. Recommend creating a simple privacy policy page:

**模板 / Template**:
```
声探体验版隐私政策

最后更新：2026年6月29日

1. 数据收集
本应用不收集任何个人身份信息。所有测量数据存储在设备本地。

2. 权限使用
• 麦克风：用于声音分贝测量和场景识别
• 存储：用于保存历史记录（仅本地）

3. AI 分析
当用户启用 AI 分析功能时，声音数据将发送至用户配置的 AI API（如 OpenAI、DeepSeek）。
本应用不自行收集或传输这些数据。

4. 开源
本应用基于 AGPL v3 开源，源码可在 GitHub/Gitee 查看。

5. 联系方式
如有疑问，请通过 GitHub Issues 联系。
```

可托管在 GitHub Pages 或 Gitee Pages。

---

## ✅ 发布检查清单 / Publishing Checklist

### 代码 / Code
- [x] 版本号更新为 1.4.0 (versionCode: 5)
- [x] 应用名称更新为"声探体验版"
- [x] 开源协议更新为 AGPL v3
- [x] Release APK 签名验证通过
- [x] 9 种语言字符串完整对齐（214 条/语言）

### 文档 / Documentation
- [x] README.md（中英双语）
- [x] CHANGELOG.md
- [x] RELEASE-NOTES-v1.4.0.md
- [x] LICENSE (AGPL v3)
- [x] .gitignore

### 发布材料 / Release Materials
- [ ] 应用图标 512×512 / App icon 512×512
- [ ] 应用截图 3-8 张 / App screenshots 3-8
- [ ] 隐私政策页面 / Privacy policy page
- [ ] 特色图（Google Play）/ Feature graphic (Google Play)

### 代码托管 / Code Hosting
- [ ] GitHub 仓库创建 / GitHub repo created
- [ ] Gitee 仓库创建 / Gitee repo created
- [ ] GitHub Release 发布 / GitHub Release published
- [ ] Gitee Release 发布 / Gitee Release published

### 应用市场 / App Stores
- [ ] 华为应用市场 / Huawei AppGallery
- [ ] 小米应用商店 / Xiaomi GetApps
- [ ] 腾讯应用宝 / Tencent MyApp
- [ ] Google Play Store
- [ ] F-Droid（申请中 / pending）
- [ ] APKPure
- [ ] Aptoide

---

## ⚠️ 注意事项 / Important Notes

1. **签名一致性 / Signature Consistency**: 所有版本必须使用同一签名密钥，否则无法升级 / All versions must use the same signing key, otherwise upgrades fail
2. **AGPL v3 合规 / AGPL v3 Compliance**: 如有衍生作品通过网络提供服务，需开源源码 / Derivative works providing services over network must open-source
3. **权限说明 / Permission Description**: 各市场要求明确说明权限用途 / Stores require clear explanation of permission usage
4. **目标 API 级别 / Target API Level**: Google Play 要求 targetSdk 为最新（目前 API 34 符合）/ Google Play requires latest targetSdk (API 34 is compliant)
5. **64 位支持 / 64-bit Support**: 所有市场要求支持 64 位 / All stores require 64-bit support (已满足 / satisfied)
