package com.alexandreribeiro.appphotickerandroid.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.alexandreribeiro.appphotickerandroid.R;
import com.alexandreribeiro.appphotickerandroid.views.MainActivity;

public class PermissionUtil {

    public static final int CAMERA_PERMISSION = 0;

    public static boolean hasCameraPermission(Context context) {
        if(needToAskPermission()){
            //vai verificar se o usuário já fez a permissão da câmera
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private static boolean needToAskPermission() {
        //verificar versão do android (é necessário fazer essas permissões após o ANDROID LOLLIPOP
        return (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP);
    }


    //solicitar permissão ao usuário
    public static void askCameraPermission(final MainActivity mainActivity) {
        //aqui ocorre se o usuário no primeiro momento tenha negado a permissão
        if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)){
            new AlertDialog.Builder(mainActivity)
                    .setMessage(mainActivity.getString(R.string.permission_camera_explanation))
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE, 
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.CAMERA_PERMISSION);
                        }
                    }).show();
        } else{
            ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.CAMERA_PERMISSION);
        }
    }
}
