package org.geometerplus.android.fbreader;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.geometerplus.zlibrary.ui.android.R;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            startActivity(FBReader.defaultIntent(SplashActivity.this));
        }
    });

    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onResume() {
        super.onResume();
        // 申请存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                permissionLauncher.launch(permissions);
            } else {
                //打开管理所有文件的权限
                showRequestPermissionDialog();
            }
        } else {
            permissionLauncher.launch(permissions);
        }
    }

    private void showRequestPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("请开启全部文件访问权限，否则无法导入本地书籍！")
                .setPositiveButton("去打开", (dialog, which) -> {
                    // 跳转到系统文件访问页面，手动赋予
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }).setNegativeButton("取消", null)
                .show();
    }
}