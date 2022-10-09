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

    private boolean isMagiskModuleInstalled = false, isRooted = false;
    private static boolean isXposedModuleEnable = false;

    private TextView rootStateText, appVersionNameText, magiskModuleVersionText, xposedModuleStateText;

    private String nonrootToastString;
    private String processingToastString;
    private String processSuccessedToastString;
    private String processFailedToastString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.rootStateText = (TextView) findViewById(R.id.textView_root_state);
        this.magiskModuleVersionText = (TextView) findViewById(R.id.textView_magisk_module_version);
        this.appVersionNameText = (TextView) findViewById(R.id.textView_app_version_name);
        this.xposedModuleStateText = (TextView) findViewById(R.id.textView_xposed_module_state);

        nonrootToastString = this.getString(R.string.mainactivity_toast_nonroot);
        processingToastString = this.getString(R.string.mainactivity_toast_processing);
        processSuccessedToastString = this.getString(R.string.mainactivity_toast_process_success);
        processFailedToastString = this.getString(R.string.mainactivity_toast_process_failed);

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

    public void resetPermissionsHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, processingToastString, Toast.LENGTH_SHORT).show();
        }
        if (!rootCommand("rm -f /data/misc_de/0/apexdata/com.android.permission/runtime-permissions.xml")) {
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        rootCommand("/system/bin/svc power reboot || /system/bin/reboot");
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }
    public void fixPermissionsHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        } else if (!isMagiskModuleInstalled) {
            Toast.makeText(this, this.getString(R.string.mainactivity_toast_not_magisk_module_installed), Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, processingToastString, Toast.LENGTH_SHORT).show();
        }
        if (!rootCommand("sh /system/etc/localization/tools/repairPermissions.sh")) {
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    public void cleanPackageCacheHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, processingToastString, Toast.LENGTH_SHORT).show();
        }
        if (!rootCommand("rm -rf /data/system/package_cache/*")) {
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        rootCommand("/system/bin/svc power reboot || /system/bin/reboot");
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    public void resetPackageRestrictionHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, processingToastString, Toast.LENGTH_SHORT).show();
        }
        if (!rootCommand("rm -rf /data/system/users/0/package-restrictions.xml")) {
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        rootCommand("/system/bin/svc power reboot || /system/bin/reboot");
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    public void deleteErrorLogHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("rm -f /sdcard/MiuiEULocalization/ErrorLog.tar rm -rf /data/system/dropbox/*wtf@*")){
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    public void packageErrorLogHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("[ -d /sdcard/MiuiEULocalization ] || mkdir /sdcard/MiuiEULocalization && tar -cf /sdcard/MiuiEULocalization/ErrorLog.tar /data/system/dropbox/*wtf@* /system/build.prop /system/etc/localization")){
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, this.getString(R.string.mainactivity_toast_package_error_log_success), Toast.LENGTH_LONG).show();
    }

    public void hideThemeFolderHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("touch /sdcard/MIUI/theme/.nomedia && rm -rf /data/user/0/com.miui.gallery/databases/*gallery* && am force-stop com.miui.gallery")) {
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    public void allowSysAppUpdateHandler(View view) {
        if (!this.isRooted) {
            Toast.makeText(this, nonrootToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rootCommand("setprop persist.sys.allow_sys_app_update true")){
            Toast.makeText(this, processFailedToastString, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, processSuccessedToastString, Toast.LENGTH_SHORT).show();
    }

    private void getDeviceStatus() {
        getRootState();

        String appVersion = "";
        try {
            appVersion = this.getApplicationContext().getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            this.appVersionNameText.setText(appVersion);
        } catch (Exception e) {
            e.printStackTrace();
            this.appVersionNameText.setText(this.getString(R.string.mainactivity_text_check_app_version_failed));
        }

        String magiskModuleVersion = SystemPropertiesProxy.get(this, "ro.miui.EULocalization");
        if (magiskModuleVersion == null || magiskModuleVersion.equals("")) {
            this.magiskModuleVersionText.setText(this.getString(R.string.mainactivity_text_magisk_module_not_installed));
            this.isMagiskModuleInstalled = false;
        } else {
            this.magiskModuleVersionText.setText(magiskModuleVersion);
            this.isMagiskModuleInstalled = true;
            if (!appVersion.equals(magiskModuleVersion)) {
                Toast.makeText(this, this.getString(R.string.mainactivity_toast_magisk_module_tools_not_match), Toast.LENGTH_LONG).show();
            }
        }

        if (isXposedModuleEnable) {
            this.xposedModuleStateText.setText(this.getString(R.string.mainactivity_text_xposed_module_activated));
            return;
        } else {
            this.xposedModuleStateText.setText(this.getString(R.string.mainactivity_text_xposed_module_inactivated));
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