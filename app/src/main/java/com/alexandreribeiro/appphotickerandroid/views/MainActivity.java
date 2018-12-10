package com.alexandreribeiro.appphotickerandroid.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alexandreribeiro.appphotickerandroid.R;
import com.alexandreribeiro.appphotickerandroid.utils.ImageUtil;
import com.alexandreribeiro.appphotickerandroid.utils.LongEventType;
import com.alexandreribeiro.appphotickerandroid.utils.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener{


    private static final int REQUEST_TAKE_PHOTO = 2;
    private final ViewHolderMain mViewHolderMain = new ViewHolderMain();
    private ImageView mImagemSelected;
    private boolean mAutoIncrement;
    private LongEventType mLongEventType;
    private Handler mRepeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //tratar erro da câmera
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);



        List<Integer> mListImage = ImageUtil.getImageList();

        mViewHolderMain.mRelativePhotoContent = findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = this.findViewById(R.id.linear_horizontal_scroll_content);

        for(Integer imageId : mListImage){
            ImageView image = new ImageView(this);

            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 50,50));
            image.setPadding(5,10,5,10);
            content.addView(image);
            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            //para previnir que não seja alocado na memoria a imagem
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageId, dimensions);

            final int with = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(mViewHolderMain.mRelativePhotoContent, imageId, with,height));
        }

        mViewHolderMain.mLinearControlePanel = this.findViewById(R.id.linear_control_painel);
        mViewHolderMain.mLinearSharedPanel = this.findViewById(R.id.linear_share_panel);

        this.mViewHolderMain.mImagePhoto = findViewById(R.id.image_photo);
        this.mViewHolderMain.mBtnRemove = findViewById(R.id.img_remove);
        this.mViewHolderMain.mBtnSave = findViewById(R.id.img_finish);
        this.mViewHolderMain.mBtnRotateLeft = findViewById(R.id.img_rotate_left);
        this.mViewHolderMain.mBtnRotateRight = findViewById(R.id.img_rotate_right);
        this.mViewHolderMain.mBtnZoomIn = findViewById(R.id.img_zoom_in);
        this.mViewHolderMain.mBtnZoomOut = findViewById(R.id.img_zoom_out);
        this.mViewHolderMain.mBtnCamera = findViewById(R.id.image_take_photo);
        this.setListeners();
    }

    private void setListeners() {
        this.mViewHolderMain.mBtnRemove.setOnClickListener(this);
        this.mViewHolderMain.mBtnSave.setOnClickListener(this);
        this.mViewHolderMain.mBtnRotateLeft.setOnClickListener(this);
        this.mViewHolderMain.mBtnRotateRight.setOnClickListener(this);
        this.mViewHolderMain.mBtnZoomIn.setOnClickListener(this);
        this.mViewHolderMain.mBtnZoomOut.setOnClickListener(this);
        this.mViewHolderMain.mBtnCamera.setOnClickListener(this);

        this.mViewHolderMain.mBtnRotateLeft.setOnLongClickListener(this);
        this.mViewHolderMain.mBtnRotateRight.setOnLongClickListener(this);
        this.mViewHolderMain.mBtnZoomIn.setOnLongClickListener(this);
        this.mViewHolderMain.mBtnZoomOut.setOnLongClickListener(this);

        this.mViewHolderMain.mBtnRotateLeft.setOnTouchListener(this);
        this.mViewHolderMain.mBtnRotateRight.setOnTouchListener(this);
        this.mViewHolderMain.mBtnZoomIn.setOnTouchListener(this);
        this.mViewHolderMain.mBtnZoomOut.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_take_photo:
                //verificar as permissões para acesso à câmera
                if(!PermissionUtil.hasCameraPermission(this)){
                    PermissionUtil.askCameraPermission(this);
                }
                dispatchTakePictureIntent();
                break;
            case R.id.img_zoom_in:
                ImageUtil.handleZoonIn(this.mImagemSelected);
                break;
            case R.id.img_zoom_out:
                ImageUtil.handleZoonOut(this.mImagemSelected);
                break;
            case R.id.img_rotate_left:
                ImageUtil.handleRotateLeft(this.mImagemSelected);
                break;
            case R.id.img_rotate_right:
                ImageUtil.handleRotateRight(this.mImagemSelected);
                break;
            case R.id.img_finish:
                toogleControlPanel(false);
                break;
            case R.id.img_remove:
                this.mViewHolderMain.mRelativePhotoContent.removeView(this.mImagemSelected);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //resultado das permissões
        if(requestCode == PermissionUtil.CAMERA_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //chamamos a câmera a partir daqui
                dispatchTakePictureIntent();
            } else{
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //para ser fechada a dialog
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    //retorno do startActivityForResult(sendo chamado a câmera)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //verificar se é da camera
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            this.setPhotoAsBackground();
        }
    }

    private void setPhotoAsBackground() {

        //dimensons da view
        int targetW = this.mViewHolderMain.mImagePhoto.getWidth();
        int targetH = this.mViewHolderMain.mImagePhoto.getHeight();

        //dimensons da foto
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //para não utilizar muito os recursos de memoria
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mViewHolderMain.mURIPhotoPath.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //redimencionar a photo
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        //para utilizar a imagem
        bmOptions.inJustDecodeBounds = false;

        //redimencionar a imagem
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(this.mViewHolderMain.mURIPhotoPath.getPath(), bmOptions);

        this.mViewHolderMain.mImagePhoto.setImageBitmap(bitmap);
    }

    private void dispatchTakePictureIntent() {
        //avisa que estamos com intenção de capturar uma imagem
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //avisar que existe essa intenção no celular
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            //iniciar a camera do celular
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFale(this);
                //guardar o path da imagem
                this.mViewHolderMain.mURIPhotoPath = Uri.fromFile(photoFile);
            }catch (IOException e){
                Toast.makeText(getApplicationContext(), "Não foi possível iniciar a camera", Toast.LENGTH_SHORT).show();
            }

            //aqui verifica se continua na chamada da camera
            if(photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                //chamada da câmera ocorre exatamente nesse ponto
                //startActivityForResult retorna o resultado com a foto no método - onActivityResult
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageId, int with, int height) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.removeAllViews();
                final  ImageView image  = new ImageView(MainActivity.this);
                image.setBackgroundResource(imageId);
                relativeLayout.addView(image);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

                mImagemSelected = image;

                toogleControlPanel(true);

                //para conseguir movimentar a imagem com o dedo

                image.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        float x, y;
                        switch (motionEvent.getAction()){
                            //caso clique na imagem
                            case MotionEvent.ACTION_DOWN:
                                mImagemSelected = image;
                                toogleControlPanel(true);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int coords[] = {0,0};
                                //preenche o posicionamento x e y , sabendo assim o posicionamento dele na tela
                                relativeLayout.getLocationOnScreen(coords);

                                //calcular onde o usuario quer movimentar na tela
                                // dive por 2 para o centro da imagem seja o centro dela
                                x = (motionEvent.getRawX() - (image.getWidth()/2));
                                //y é para saber o inicio do relative layout
                                y = motionEvent.getRawY() - ((coords[1] + 100) + (image.getHeight()/2));
                                image.setX(x);
                                image.setY(y);
                                break;
                            //quando usuario parar de tocar na imagem
                            case MotionEvent.ACTION_UP:
                                break;
                        }

                        return true;
                    }
                });
            }
        };
    }

    private void toogleControlPanel(boolean showControls){
        if(showControls){
            mViewHolderMain.mLinearSharedPanel.setVisibility(View.GONE);
            mViewHolderMain.mLinearControlePanel.setVisibility(View.VISIBLE);
        }else{
            mViewHolderMain.mLinearSharedPanel.setVisibility(View.VISIBLE);
            mViewHolderMain.mLinearControlePanel.setVisibility(View.GONE);

        }

    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if(id == R.id.img_zoom_in){
            this.mLongEventType = LongEventType.ZoomIn;
        }
        if(id == R.id.img_zoom_out){
            this.mLongEventType = LongEventType.ZoomOut;
        }
        if(id == R.id.img_rotate_left){
            this.mLongEventType = LongEventType.RotateLeft;
        }
        if(id == R.id.img_rotate_right){
            this.mLongEventType = LongEventType.RotateRight;
        }
        //ele ativa quando começar o click
        mAutoIncrement = true;
        //aqui chamar a thread
        new RptUpdater().run();

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        int id = v.getId();
        if(id == R.id.img_zoom_in || id == R.id.img_zoom_out || id == R.id.img_rotate_right || id == R.id.img_rotate_left){
            //parou de executar
            //desativa quando tirar o dedo
            if(motionEvent.getAction() == MotionEvent.ACTION_UP && mAutoIncrement == true){
                mAutoIncrement = false;
                this.mLongEventType = null;
            }
        }
        return false;
    }


    private static  class  ViewHolderMain{
        ImageView mBtnZoomIn;
        ImageView mBtnZoomOut;
        ImageView mBtnRotateLeft;
        ImageView mBtnRotateRight;
        ImageView mBtnSave;
        ImageView mBtnRemove;
        ImageView mBtnCamera;
        ImageView mImagePhoto;

        LinearLayout mLinearSharedPanel;
        LinearLayout mLinearControlePanel;

        RelativeLayout mRelativePhotoContent;

        Uri mURIPhotoPath;
    }

    private class RptUpdater implements Runnable {

        @Override
        public void run() {
            //por enquanto que for true vai continuar fazer a ação
            if(mAutoIncrement){
                //aqui estará sempre chamando a thred toda vez que for true(mAutoIncrement) cada 0,050 segundos
                mRepeatUpdateHandler.postDelayed(new RptUpdater(), 50);
            }

            //saber o evento que usuario está fazendo
            if(mLongEventType != null){
                switch (mLongEventType){
                    case ZoomIn:
                        ImageUtil.handleZoonIn(mImagemSelected);
                        break;
                    case ZoomOut:
                        ImageUtil.handleZoonOut(mImagemSelected);
                        break;
                    case RotateLeft:
                        ImageUtil.handleRotateLeft(mImagemSelected);
                        break;
                    case RotateRight:
                        ImageUtil.handleRotateRight(mImagemSelected);
                        break;

                }
            }
        }
    }
}
