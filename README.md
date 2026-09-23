# ColorOS 优化器

> LSPosed 模块 - ColorOS 16 系统优化

## 功能

- 🌨️ **极限降温** - 强制降频，温度优先
- 🔋 **省电模式** - 限制后台，延长续航
- ⚖️ **均衡模式** - 平衡性能与续航（默认）
- 🚀 **性能模式** - 提升性能，适当发热
- 🔥 **狂暴模式** - 解除温控，性能拉满

## 自动构建

每次 push 到 main 分支，GitHub Actions 会自动编译 APK。

📦 **下载 APK**: 点击上方 Actions → latest run → Artifacts

## 手动编译

```bash
./gradlew assembleDebug
```

## 安装

1. 下载 APK
2. 用 LSPosed Manager 安装
3. 勾选模块 → 重启
