package com.alexandreribeiro.appphotickerandroid.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alexandreribeiro.appphotickerandroid.R;
import com.alexandreribeiro.appphotickerandroid.views.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SocialUtil {
    private static final String HASHTAG = "Photicker - xandão";

    public static void shareImageOnInsta(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
    }

    public static void shareImageOnFace(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
    }

    public static void shareImageOnTwitter(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
    }

    public static void shareImageOnWhatsapp(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
        //verificar se está instalado
        PackageManager pkManager = mainActivity.getPackageManager();

        try{
            pkManager.getPackageInfo("com.whatsapp", 0);
            String fileName = "temp_file" + System.currentTimeMillis()+ ".jpg";

            try {
                //relative seja uma img
                //para habilitar virar um bitmap
                mRelativePhotoContent.setDrawingCacheEnabled(true);
                mRelativePhotoContent.buildDrawingCache(true);

                File imageFile = new File(Environment.getExternalStorageDirectory(),fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                mRelativePhotoContent.getDrawingCache(true).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
                mRelativePhotoContent.setDrawingCacheEnabled(false);
                mRelativePhotoContent.destroyDrawingCache();

                try {
                    Intent sendIntend = new Intent();
                    sendIntend.setAction(Intent.ACTION_SEND);
                    sendIntend.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                    sendIntend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+ fileName));

                    sendIntend.setType("image/jpeg");
                    sendIntend.setPackage("com.whatsapp");

                    v.getContext().startActivity(Intent.createChooser(sendIntend, mainActivity.getString(R.string.share_image)));
                }catch (Exception e){
                    Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                }

            } catch (FileNotFoundException e){
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();

            }

        } catch (PackageManager.NameNotFoundException e){
            Toast.makeText(mainActivity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        }
    }
}
