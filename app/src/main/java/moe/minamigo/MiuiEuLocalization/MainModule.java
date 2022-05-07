package moe.minamigo.MiuiEuLocalization;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainModule implements IXposedHookLoadPackage {
    List<String> XSPACE_INTRODUCE_APPS;
    boolean isA10;
    boolean isA11;

    public MainModule() {
        ArrayList arrayList = new ArrayList();
        this.XSPACE_INTRODUCE_APPS = arrayList;
        arrayList.add("com.tencent.mm");
        this.XSPACE_INTRODUCE_APPS.add("com.tencent.mobileqq");
        this.XSPACE_INTRODUCE_APPS.add("com.sina.weibo");
        this.XSPACE_INTRODUCE_APPS.add("com.whatsapp");
        this.XSPACE_INTRODUCE_APPS.add("com.facebook.katana");
        this.XSPACE_INTRODUCE_APPS.add("com.instagram.android");
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String pkg = lpparam.packageName;

        switch (pkg){
            case "moe.minamigo.MiuiEuLocalization":
                try{
                    handleSelf(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.miui.securitycore":
                try{
                    handleSecuritycore(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.google.android.inputmethod.latin":
                try{
                    handleGboard(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.miui.home":
                try{
                    handleMiuiHome(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "android":
            case "com.miui.powerkeeper":
            case "com.xiaomi.powerchecker":
            case "com.miui.core":
                try{
                    handleInternational(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
        }
    }

    private void handleMiuiHome(XC_LoadPackage.LoadPackageParam lpparam) {
        handleInternational(lpparam);

        Class<?> clazz_NotificationManagerServiceInjector = XposedHelpers.findClass("com.miui.home.launcher.MIUIWidgetUtil", lpparam.classLoader);
        final Method[] declareMethods_NotificationManagerServiceInjector = clazz_NotificationManagerServiceInjector.getDeclaredMethods();
        Method isMiuiWidgetSupportMethod = null;
        for (Method method : declareMethods_NotificationManagerServiceInjector) {
            if (method.getName().equals("isMIUIWidgetSupport")) {
                isMiuiWidgetSupportMethod = method;
                break;
            }
        }
        if (isMiuiWidgetSupportMethod != null) {
            XposedBridge.hookMethod(isMiuiWidgetSupportMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    handleChina(lpparam);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    handleInternational(lpparam);
                }
            });
        } else{
            XposedBridge.log("isMiuiWidgetSupportMethod is not found!");
        }
    }

    private void handleSecuritycore(XC_LoadPackage.LoadPackageParam lpparam) {
        handleInternational(lpparam);
        XposedHelpers.setStaticObjectField(XposedHelpers.findClass("com.miui.xspace.constant.XSpaceApps", lpparam.classLoader), "XSPACE_INTRODUCE_APPS", this.XSPACE_INTRODUCE_APPS);
    }

    public void checkVersion() {
        switch (Build.VERSION.SDK_INT) {
            case 28:
            case 29:
                this.isA10 = true;
                this.isA11 = false;
                return;
            case 30:
                this.isA10 = false;
                this.isA11 = true;
                return;
            default:
                this.isA10 = false;
                this.isA11 = false;
                return;
        }
    }
    private void handleGboard(XC_LoadPackage.LoadPackageParam lpparam) {
        checkVersion();
        final Class<?> clazz = XposedHelpers.findClass("android.inputmethodservice.InputMethodServiceInjector", lpparam.classLoader);
        XposedHelpers.setStaticIntField(clazz, "sIsImeSupport", 1);
        if (this.isA10) {
            XposedHelpers.findAndHookMethod(clazz, "isXiaoAiEnable", new Object[]{XC_MethodReplacement.returnConstant(false)});
        } else {
            XposedHelpers.findAndHookMethod("com.android.internal.policy.PhoneWindow", lpparam.classLoader, "setNavigationBarColor", new Object[]{Integer.TYPE, new XC_MethodHook() {
                /* class moe.minamigo.MiuiEULocalization.MainModule.AnonymousClass1 */

                /* access modifiers changed from: protected */
                public void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    int color = -1 - ((Integer) param.args[0]).intValue();
                    XposedHelpers.callStaticMethod(clazz, "customizeBottomViewColor", new Object[]{true, param.args[0], Integer.valueOf(-16777216 | color), Integer.valueOf(1711276032 | color)});
                }
            }});
        }
        if (this.isA10) {
            XposedHelpers.findAndHookMethod("android.inputmethodservice.InputMethodServiceInjector$MiuiSwitchInputMethodListener", lpparam.classLoader, "deleteNotSupportIme", new Object[]{XC_MethodReplacement.returnConstant((Object) null)});
            return;
        }
        XposedHelpers.findAndHookMethod("android.inputmethodservice.InputMethodModuleManager", lpparam.classLoader, "loadDex", new Object[]{ClassLoader.class, String.class, new XC_MethodHook() {
            /* class moe.minamigo.MiuiEULocalization.MainModule.AnonymousClass2 */

            /* access modifiers changed from: protected */
            public void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                final Class<?> clazz = XposedHelpers.findClass("com.miui.inputmethod.InputMethodBottomManager", (ClassLoader) param.args[0]);
                XposedHelpers.setStaticIntField(clazz, "sIsImeSupport", 1);
                XposedHelpers.findAndHookMethod(clazz, "isXiaoAiEnable", new Object[]{XC_MethodReplacement.returnConstant(false)});
                XposedHelpers.findAndHookMethod(clazz, "getSupportIme", new Object[]{new XC_MethodReplacement() {
                    /* class moe.minamigo.MiuiEULocalization.MainModule.AnonymousClass2.AnonymousClass1 */

                    /* access modifiers changed from: protected */
                    public Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        return ((InputMethodManager) XposedHelpers.getObjectField(XposedHelpers.getStaticObjectField(clazz, "sBottomViewHelper"), "mImm")).getEnabledInputMethodList();
                    }
                }});
            }
        }});
    }

    private void handleSelf(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> clazz = XposedHelpers.findClass("moe.minamigo.MiuiEuLocalization.MainActivity", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(clazz, "isXposedModuleEnable", true);
    }

    private void handleInternational(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", true);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_GLOBAL_BUILD", true);
    }
    private void handleChina(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", false);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_GLOBAL_BUILD", false);
    }
}
