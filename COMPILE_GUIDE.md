# ColorOS 优化器 - 编译指南

## 方式一：Android Studio（推荐）

1. 用 Android Studio 打开 `/workspace` 目录
2. 等待 Gradle sync 完成
3. 点击 Build > Build Bundle(s) / APK(s) > Build APK(s)
4. APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`

## 方式二：命令行编译

确保已安装：
- JDK 17+
- Android SDK (commandline-tools)
- Gradle 8.4+

```bash
cd /workspace
./gradlew assembleDebug
```

## 方式三：使用提供的 Gradle Wrapper

Windows:
```bat
gradlew.bat assembleDebug
```

Linux/Mac:
```bash
chmod +x gradlew
./gradlew assembleDebug
```

## 安装模块

1. 将 APK 传输到手机
2. 使用 LSPosed Manager 安装
3. 勾选 ColorOS 优化器模块
4. 重启设备
5. 在模块内选择运行模式

## 模式说明

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| 极限降温 | 强制降频，温度优先 | 发热严重/充电时 |
| 省电模式 | 限制后台，延长续航 | 外出/电量低 |
| 均衡模式 | 平衡性能与续航 | 日常使用（默认） |
| 性能模式 | 提升性能 | 游戏/高负载 |
| 狂暴模式 | 解除温控 | 极客/跑分 |
