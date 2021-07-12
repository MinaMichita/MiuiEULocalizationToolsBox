package moe.minamigo.MiuiEuLocalization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView TextView_RootState, TextView_appVersionName, TextView_magiskModuleVersion, TextView_xposedModuleState;

    private double double_appVersionName, double_magiskModuleVersion;
    private boolean isMagiskModuleInstalled = false,isRoot = false;
    private static boolean isXposedModuleEnable = false;
    private int warning_fixLocalNofiticationCount = 0;

    private static String magiskModulePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.TextView_RootState = (TextView) findViewById(R.id.textView_rootState);
        this.TextView_magiskModuleVersion = (TextView) findViewById(R.id.textView_magiskModuleVersion);
        this.TextView_appVersionName = (TextView) findViewById(R.id.textView_appVersionName);
        this.TextView_xposedModuleState = (TextView) findViewById(R.id.textView_xposedModuleState);
        getDeviceStatus();
    }

    public void recheckDeviceStatusHandler(View view) {
        getDeviceStatus();
    }

    public void fixMusicHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMagiskModuleInstalled || magiskModulePath ==null || magiskModulePath.equals("") ){
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
            rootCommand("pm install " + magiskModulePath + "/system/priv-app/Music/Music.apk");
            Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void fixMipayHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMagiskModuleInstalled || magiskModulePath ==null || magiskModulePath.equals("") ){
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
            rootCommand("pm install " + magiskModulePath + "/system/app/Mipay/Mipay.apk");
            Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void fixVoiceAssistHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMagiskModuleInstalled || magiskModulePath ==null || magiskModulePath.equals("") ){
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
            rootCommand("pm install " + magiskModulePath + "/system/app/VoiceAssist/VoiceAssist.apk");
            Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void fixVirtualSimHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMagiskModuleInstalled || magiskModulePath ==null || magiskModulePath.equals("") ){
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
            rootCommand("pm install " + magiskModulePath + "/system/priv-app/VirtualSim/VirtualSim.apk");
            Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void fixPermissionsHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isMagiskModuleInstalled) {
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
        }
        try {
            rootCommand("sh " + magiskModulePath + "/system/etc/localization/tools/repairPermissions.sh");
            Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void fixLocalNofiticationHanlder(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isXposedModuleEnable || !this.isMagiskModuleInstalled || SystemPropertiesProxy.get(this, "ro.product.mod_device").contains("_global") || this.warning_fixLocalNofiticationCount != 0) {
            try {
                rootCommand("rm -rf /data/system/netstats && rm -f /data/system/appops.xml && reboot");
            } catch (Exception e) {
                Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "未激活工具箱Xposed模块，修复可能无效！如果仍需修复，请再次点击修复！", Toast.LENGTH_SHORT).show();
            this.warning_fixLocalNofiticationCount++;
        }
    }

    public void killProcessesHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            rootCommand("am force-stop com.android.networkstack.tethering.inprocess && am force-stop com.miui.home && am force-stop com.miui.notification && am force-stop com.miui.securitycenter && am force-stop com.android.thememanager && am force-stop com.android.networkstack.inprocess");
            Toast.makeText(this, "杀进程完成！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }
    public void reniceHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            rootCommand("renice -n  -20 -p `ps -ef |grep com.android.systemui|awk '{print $2}'|awk 'NR==1'` && renice -n  -20 -p `ps -ef |grep com.miui.home|awk '{print $2}'|awk 'NR==1'` && renice -n  -20 -p `ps -ef |grep system_server|awk '{print $2}'|awk 'NR==1'`");
            Toast.makeText(this, "调整进程优先级完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteErrorLogHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            rootCommand("rm -f /sdcard/MiuiEULocalization/ErrorLog.tar rm -rf /data/system/dropbox/*wtf@*");
            Toast.makeText(this, "清空错误日志完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }
    public void packageErrorLogHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            rootCommand("[ -d /sdcard/MiuiEULocalization ] || mkdir /sdcard/MiuiEULocalization && tar -cf /sdcard/MiuiEULocalization/ErrorLog.tar /data/system/dropbox/*wtf@* /system/build.prop " + magiskModulePath + "/system/etc/localization");
            Toast.makeText(this, "打包完成，路径：/sdcard/MiuiEULocalization/ErrorLog.tar\n日志包含设备基本信息，请勿随意分享！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }
    public void hideThemeFolderHandler(View view) {
        if (!this.isRoot) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            rootCommand("touch /sdcard/MIUI/theme/.nomedia && rm -rf /data/user/0/com.miui.gallery/databases/*gallery* && am force-stop com.miui.gallery");
            Toast.makeText(this, "执行完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMagiskModuleVersion() {
        try {
            String string_magiskModuleVersion = SystemPropertiesProxy.get(this, "ro.miui.EULocalization");
            if (string_magiskModuleVersion.equals("")) {
                this.TextView_magiskModuleVersion.setText("未安装或版本过旧");
                this.isMagiskModuleInstalled = false;
                return;
            }
            this.double_magiskModuleVersion = Double.parseDouble(string_magiskModuleVersion.split("v")[1]);
            this.TextView_magiskModuleVersion.setText(string_magiskModuleVersion);
            this.isMagiskModuleInstalled = true;
        } catch (Exception e) {
            this.isMagiskModuleInstalled = false;
            Log.e(TAG, String.valueOf(e));
            this.TextView_magiskModuleVersion.setText("检查错误，请重试！");
        }
    }
    private void getAppVersionName(Context ctx) {
        try {
            String localVersion = ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            this.TextView_appVersionName.setText(localVersion);
            this.double_appVersionName = Double.parseDouble(localVersion);
        } catch (Exception e) {
            e.printStackTrace();
            this.TextView_appVersionName.setText("检查错误，请重试！");
        }
    }
    private void getXposedEnableState() {
        if (isXposedModuleEnable) {
            this.TextView_xposedModuleState.setText("已激活！");
            return;
        }
        this.TextView_xposedModuleState.setText("未激活！");
        Toast.makeText(this, "未激活工具箱Xposed模块，激活后重启工具箱以刷新状态！", Toast.LENGTH_SHORT).show();
    }
    private void getMagiskPath(){
        magiskModulePath = SystemPropertiesProxy.get(this, "moe.minamigo.MiuiEULocalization.path");
    }
    private void getDeviceStatus() {
        getRootState();
        getMagiskModuleVersion();
        getAppVersionName(this);
        getXposedEnableState();
        getMagiskPath();
    }

    private synchronized void getRootState() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                TextView_RootState.setText("已获得！");
                isRoot=true;
            } else {
                TextView_RootState.setText("未获取Root权限或未授权！");
                isRoot=false;
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            TextView_RootState.setText("检查错误，请重试！");
            isRoot=false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean rootCommand(String command){
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            try {
                os.close();
                process.destroy();
            } catch (Exception e) {
            }
            Log.d("MainActivity", "Command Successed!");
            return true;
        } catch (Exception e2) {
            Log.e("MainActivity", "Command Failed: " + command + "\n" + e2.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}