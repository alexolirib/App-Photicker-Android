package com.alexandreribeiro.appphotickerandroid.views;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alexandreribeiro.appphotickerandroid.R;
import com.alexandreribeiro.appphotickerandroid.utils.ImageUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final ViewHolderMain mViewHolderMain = new ViewHolderMain();
    private ImageView mImagemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);



        List<Integer> mListImage = ImageUtil.getImageList();
        Log.e("qtd", String.valueOf(mListImage.size()));

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

        this.mViewHolderMain.mBtnRemove = findViewById(R.id.img_remove);
        this.mViewHolderMain.mBtnSave = findViewById(R.id.img_finish);
        this.mViewHolderMain.mBtnRotateLeft = findViewById(R.id.img_rotate_left);
        this.mViewHolderMain.mBtnRotateRight = findViewById(R.id.img_rotate_right);
        this.mViewHolderMain.mBtnZoomIn = findViewById(R.id.img_zoom_in);
        this.mViewHolderMain.mBtnZoomOut = findViewById(R.id.img_zoom_out);
        this.setListeners();
    }

    private void setListeners() {
        this.mViewHolderMain.mBtnRemove.setOnClickListener(this);
        this.mViewHolderMain.mBtnSave.setOnClickListener(this);
        this.mViewHolderMain.mBtnRotateLeft.setOnClickListener(this);
        this.mViewHolderMain.mBtnRotateRight.setOnClickListener(this);
        this.mViewHolderMain.mBtnZoomIn.setOnClickListener(this);
        this.mViewHolderMain.mBtnZoomOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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



    private static  class  ViewHolderMain{
        ImageView mBtnZoomIn;
        ImageView mBtnZoomOut;
        ImageView mBtnRotateLeft;
        ImageView mBtnRotateRight;
        ImageView mBtnSave;
        ImageView mBtnRemove;

        LinearLayout mLinearSharedPanel;
        LinearLayout mLinearControlePanel;

        RelativeLayout mRelativePhotoContent;

    }
}
