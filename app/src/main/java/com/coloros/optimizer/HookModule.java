package com.coloros.optimizer;

import android.os.Build;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookModule implements IXposedHookLoadPackage {
    
    private static final String TAG = "ColorOSOptimizer";
    public static int currentMode = 2; // 默认均衡模式

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.coloros.systemserver")) {
            return;
        }

        Log.d(TAG, "Hooking ColorOS 16 system server...");

        // Hook 温度管理
        hookThermalManagement(lpparam);
        // Hook CPU 调度
        hookCpuScheduler(lpparam);
        // Hook 内存管理
        hookMemoryManagement(lpparam);
        // Hook 电池管理
        hookBatteryManagement(lpparam);

        XposedBridge.log(TAG + " ColorOS 16 optimization activated!");
    }

    private void hookThermalManagement(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook thermal service
            Class<?> thermalClass = XposedHelpers.findClass(
                "com.coloros.temperature.TemperatureService", 
                lpparam.classLoader
            );

            XposedHelpers.findAndHookMethod(thermalClass, "getCpuTemperature", 
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        float temp = (float) param.getResult();
                        switch (currentMode) {
                            case 0: // 极限降温
                                return temp * 0.85f;
                            case 1: // 省电模式
                                return temp * 0.92f;
                            case 2: // 均衡模式
                                return temp;
                            case 3: // 性能模式
                                return temp * 1.05f;
                            case 4: // 狂暴模式
                                return temp * 1.15f;
                        }
                        return temp;
                    }
                }
            );

            // Hook 温控阈值
            XposedHelpers.findAndHookMethod(thermalClass, "shouldThrottle", 
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        switch (currentMode) {
                            case 0:
                                return false;
                            case 1:
                                return (boolean) param.getResult() || (currentMode == 0);
                            case 4:
                                return false;
                            default:
                                return param.getResult();
                        }
                    }
                }
            );
        } catch (Throwable t) {
            Log.d(TAG, "Thermal hook partial: " + t.getMessage());
        }
    }

    private void hookCpuScheduler(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> cpuClass = XposedHelpers.findClass(
                "com.coloros.cpu.CpuManager",
                lpparam.classLoader
            );

            // Hook CPU 频率上限
            XposedHelpers.findAndHookMethod(cpuClass, "getMaxCpuFreq",
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        long freq = (long) param.getResult();
                        switch (currentMode) {
                            case 0: return freq / 2;      // 极限降温 - 降到一半
                            case 1: return (long)(freq * 0.7);  // 省电 - 70%
                            case 2: return freq;          // 均衡
                            case 3: return (long)(freq * 1.1); // 性能 +10%
                            case 4: return (long)(freq * 1.2); // 狂暴 +20%
                        }
                        return freq;
                    }
                }
            );

            // Hook CPU 调度策略
            XposedHelpers.findAndHookMethod(cpuClass, "getSchedulerMode",
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        switch (currentMode) {
                            case 0: return "powersave";
                            case 1: return "powersave";
                            case 2: return "balanced";
                            case 3: return "performance";
                            case 4: return "performance";
                        }
                        return param.getResult();
                    }
                }
            );
        } catch (Throwable t) {
            Log.d(TAG, "CPU scheduler hook partial: " + t.getMessage());
        }
    }

    private void hookMemoryManagement(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> memClass = XposedHelpers.findClass(
                "com.coloros.memory.MemoryManager",
                lpparam.classLoader
            );

            // Hook OOM 阈值
            XposedHelpers.findAndHookMethod(memClass, "getOomScore",
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        int score = (int) param.getResult();
                        switch (currentMode) {
                            case 0: return score + 200;
                            case 1: return score + 100;
                            case 2: return score;
                            case 3: return Math.max(0, score - 50);
                            case 4: return Math.max(0, score - 100);
                        }
                        return score;
                    }
                }
            );
        } catch (Throwable t) {
            Log.d(TAG, "Memory hook partial: " + t.getMessage());
        }
    }

    private void hookBatteryManagement(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> batteryClass = XposedHelpers.findClass(
                "com.coloros.power.PowerManager",
                lpparam.classLoader
            );

            // Hook 后台省电策略
            XposedHelpers.findAndHookMethod(batteryClass, "getBackgroundLimit",
                new XC_MethodHook() {
                    @Override
                    protected Object afterHookedMethod(MethodHookParam param) {
                        switch (currentMode) {
                            case 0: return 0;        // 完全限制
                            case 1: return 30;       // 30秒
                            case 2: return 60;       // 60秒
                            case 3: return 300;      // 5分钟
                            case 4: return -1;       // 不限制
                        }
                        return param.getResult();
                    }
                }
            );
        } catch (Throwable t) {
            Log.d(TAG, "Battery hook partial: " + t.getMessage());
        }
    }
}
