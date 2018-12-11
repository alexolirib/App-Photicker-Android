package com.alexandreribeiro.appphotickerandroid.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alexandreribeiro.appphotickerandroid.R;
import com.alexandreribeiro.appphotickerandroid.views.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SocialUtil {
    private static final String HASHTAG = "Photicker - xandão";

    public static void shareImageOnInsta(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
    }

    public static void shareImageOnFace(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
    }

    public static void shareImageOnTwitter(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
        PackageManager pkManager = mainActivity.getPackageManager();

        try{
            //verificar se o twitter está instalado
            pkManager.getPackageInfo("com.twitter.android",0);
            try {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);

                //converter a imagem para um bitmap
                Bitmap image = ImageUtil.drawBitmap(mRelativePhotoContent);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpg");
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());

                //passar informações para o twitter
                tweetIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpg"));

                //qual o app que o celular possui para enviar (twitter)
                PackageManager pm = mainActivity.getPackageManager();
                List<ResolveInfo> resolve = pm.queryIntentActivities(tweetIntent,PackageManager.MATCH_DEFAULT_ONLY);
                boolean resolved = false;
                for(ResolveInfo ri : resolve){
                    //aqui é para entrar no twitter
                    if(ri.activityInfo.name.contains("twitter")){
                        tweetIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }

                v.getContext().startActivity(resolved? tweetIntent: Intent.createChooser(tweetIntent, mainActivity.getString(R.string.share_image)));


            } catch (FileNotFoundException e){
                Toast.makeText(mainActivity, R.string.unexpected_error,Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                Toast.makeText(mainActivity, R.string.unexpected_error,Toast.LENGTH_SHORT).show();
            }

        }catch (PackageManager.NameNotFoundException e){
            Toast.makeText(mainActivity, R.string.twitter_not_installed, Toast.LENGTH_SHORT).show();
        }

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
