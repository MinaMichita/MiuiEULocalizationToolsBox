package moe.minamigo.MiuiEuLocalization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView rootStateText, appVersionNameText, magiskModuleVersionText, xposedModuleStateText;

    private boolean isMagiskModuleInstalled = false, isRooted = false;
    private static boolean isXposedModuleEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.rootStateText = (TextView) findViewById(R.id.textView_root_state);
        this.magiskModuleVersionText = (TextView) findViewById(R.id.textView_magisk_module_version);
        this.appVersionNameText = (TextView) findViewById(R.id.textView_app_version_name);
        this.xposedModuleStateText = (TextView) findViewById(R.id.textView_xposed_module_state);
        getDeviceStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (os != null) {
                os.writeBytes("exit\n");
                os.flush();

                os.close();
            }
            if (process != null) {
                process.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recheckDeviceStatusHandler(View view) {
        getDeviceStatus();
    }

    public void fixPermissionsHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isMagiskModuleInstalled) {
            Toast.makeText(this, "未安装Magisk模块或版本过低！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "开始修复，请等待一会！", Toast.LENGTH_SHORT).show();
        }
        if (!rootCommand("sh /system/etc/localization/tools/repairPermissions.sh")) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "修复完成！", Toast.LENGTH_SHORT).show();
    }

    public void deleteErrorLogHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("rm -f /sdcard/MiuiEULocalization/ErrorLog.tar rm -rf /data/system/dropbox/*wtf@*")){
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "清空错误日志完成", Toast.LENGTH_SHORT).show();
    }

    public void packageErrorLogHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("[ -d /sdcard/MiuiEULocalization ] || mkdir /sdcard/MiuiEULocalization && tar -cf /sdcard/MiuiEULocalization/ErrorLog.tar /data/system/dropbox/*wtf@* /system/build.prop /system/etc/localization")){
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "打包完成，路径：/sdcard/MiuiEULocalization/ErrorLog.tar\n日志包含设备基本信息，请勿随意分享！", Toast.LENGTH_LONG).show();
    }

    public void hideThemeFolderHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("touch /sdcard/MIUI/theme/.nomedia && rm -rf /data/user/0/com.miui.gallery/databases/*gallery* && am force-stop com.miui.gallery")) {
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "执行完成", Toast.LENGTH_SHORT).show();
    }

    public void allowSysAppUpdateHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, "设备未root或未授权应用root权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("setprop persist.sys.allow_sys_app_update true")){
            Toast.makeText(this, "执行失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "执行完成", Toast.LENGTH_SHORT).show();
    }

    private void getDeviceStatus() {
        getRootState();

        String appVersion = "";
        try {
            appVersion = this.getApplicationContext().getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            this.appVersionNameText.setText(appVersion);
        } catch (Exception e) {
            e.printStackTrace();
            this.appVersionNameText.setText("检查错误，请重试！");
        }

        String magiskModuleVersion = SystemPropertiesProxy.get(this, "ro.miui.EULocalization");
        if (magiskModuleVersion == null || magiskModuleVersion.equals("")) {
            this.magiskModuleVersionText.setText("未安装或版本过旧");
            this.isMagiskModuleInstalled = false;
        } else {
            this.magiskModuleVersionText.setText(magiskModuleVersion);
            this.isMagiskModuleInstalled = true;
            if (!appVersion.equals(magiskModuleVersion)) {
                Toast.makeText(this, "警告！工具箱版本和模块版本不匹配！", Toast.LENGTH_SHORT).show();
            }
        }

        if (isXposedModuleEnable) {
            this.xposedModuleStateText.setText("已激活！");
            return;
        } else {
            this.xposedModuleStateText.setText("未激活！请检查");
        }
    }

    private Process process = null;
    private DataOutputStream os = null;

    private synchronized void getRootState() {
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                rootStateText.setText("已获得！");
                isRooted = true;
                process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());
            } else {
                rootStateText.setText("未获取Root权限或未授权！");
                isRooted = false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            rootStateText.setText("检查错误，请重试！");
            isRooted = false;
        }
    }

    private boolean rootCommand(String command) {
        if (process == null || os == null) {
            return false;
        }
        try {
            os.writeBytes(command + "\n");
            os.flush();
            Log.d("MainActivity", "Command Successed!");
            return true;
        } catch (Exception e2) {
            Log.e("MainActivity", "Command Failed: " + command + "\n" + e2.getMessage());
            return false;
        }
    }

}