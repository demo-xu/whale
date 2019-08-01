package com.demon.test;

import java.util.Arrays;

import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedBridge;
import com.lody.whale.xposed.XposedHelpers;

import android.util.Log;

public class HookTest {
    private static final String[] WHITE_LIST_CLASS =
            {"java.lang.ClassNotFoundException", "java.lang.Throwable", "android.system.GaiException"};

    private static boolean isExceptionInWhiteList(String name) {
        int index = Arrays.binarySearch(WHITE_LIST_CLASS, name);
        return index >= 0;
    }

    public static void hookException() {
        constructException();
    }

    private static void constructException() {
        XposedHelpers.findAndHookConstructor(Exception.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object thisObject = param.thisObject;
                String name = thisObject.getClass().getCanonicalName();
                if (!isExceptionInWhiteList(name)) {
                    XposedBridge.log("hooking...afterHookedMethod name:" + name);
                    printException("hook", (Throwable) thisObject);
                }

            }
        });

        XposedHelpers.findAndHookConstructor(Exception.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object thisObject = param.thisObject;
                String name = thisObject.getClass().getCanonicalName();
                if (!isExceptionInWhiteList(name)) {
                    XposedBridge.log("hooking...afterHookedMethod name:" + name);
                    printException("hook", (Throwable) thisObject);
                    if (name.endsWith("CalledFromWrongThreadException")) {
                        Log.e("hook", "warning");
                    }
                }

            }
        });

        XposedHelpers.findAndHookConstructor(Exception.class, String.class, Throwable.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object thisObject = param.thisObject;
                String name = thisObject.getClass().getCanonicalName();
                if (!isExceptionInWhiteList(name)) {
                    XposedBridge.log("hooking...afterHookedMethod name:" + name);
                    printException("hook", (Throwable) thisObject);
                }

            }
        });
    }

    private static void printException(Throwable ex) {
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder sb = new StringBuilder();
        if (stackElements != null) {
            sb.append("printCallStack").append("\n").append("--------start--------").append("\n");
            for (int i = 0; i < stackElements.length; i++) {
                sb.append("        at ").append(stackElements[i].getClassName()).append(".")
                        .append(stackElements[i].getMethodName()).append("(").append(stackElements[i].getFileName())
                        .append(":").append(stackElements[i].getLineNumber()).append(")").append("\n");
            }
            sb.append("--------end---------");
        }
        Log.e("printCallStack", sb.toString());
    }

}
