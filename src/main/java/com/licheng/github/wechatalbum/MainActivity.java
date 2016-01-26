package com.licheng.github.wechatalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licheng.github.wechatalbum.localalbum.FilterImageView;
import com.licheng.github.wechatalbum.localalbum.LocalAlbum;
import com.licheng.github.wechatalbum.localalbum.LocalAlbumHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;//返回键
    private View mSend;//发送
    private EditText mContent;//动态内容编辑框
    private InputMethodManager imm;//软键盘管理
    private TextView textRemain;//字数提示
    private TextView picRemain;//图片数量提示
    private ImageView add;//添加图片按钮
    private LocalAlbumHelper helper;
    private LinearLayout picContainer;//图片容器

    private DisplayImageOptions options;

    private int size;
    private int padding;
    private final static int ALBUM = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_dynamic);
        initView();
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.post_back);
        mSend = findViewById(R.id.post_send);
        mContent = (EditText) findViewById(R.id.post_content);
        textRemain = (TextView) findViewById(R.id.post_text_remain);
        picRemain = (TextView) findViewById(R.id.post_pic_remain);
        add = (ImageView) findViewById(R.id.post_add_pic);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);

        initData();

        //设置ImageLoader参数
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.dangkr_no_picture_small)
                .showImageOnFail(R.drawable.dangkr_no_picture_small)
                .showImageOnLoading(R.drawable.dangkr_no_picture_small)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();

        //注册点击事件
        mBack.setOnClickListener(this);
        mSend.setOnClickListener(this);
        add.setOnClickListener(this);
        helper = LocalAlbumHelper.getInstance();

    }

    private void initData() {
        size = getResources().getDimensionPixelSize(R.dimen.size_100);
        padding = getResources().getDimensionPixelSize(R.dimen.padding_10);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_add_pic:
                //调用系统相册
//                Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //调用自定义相册
                Intent intent = new Intent(MainActivity.this,LocalAlbum.class);
                startActivityForResult(intent,ALBUM);
                break;
            default:
                if(v instanceof FilterImageView){
                    Toast.makeText(MainActivity.this,"点击预览",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("相册返回code：",resultCode+"相册请求code:"+requestCode);
        if(requestCode == ALBUM){
            //如果图片选中 填充横向线性布局视图
            if (helper.isResultOk()){
                helper.setResultOk(false);
                //获取选中的照片
                List<LocalAlbumHelper.LocalFile> files = helper.getCheckedItems();
                for (int i = 0; i < files.size(); i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
                    params.rightMargin = padding;
                    FilterImageView image = new FilterImageView(MainActivity.this);
                    image.setLayoutParams(params);
                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ImageLoader.getInstance().displayImage(files.get(i).getThumbnailUri(), new ImageViewAware(image), options,
                            null, null, files.get(i).getOrientation());
                    image.setOnClickListener(this);
                    picContainer.addView(image,picContainer.getChildCount()-1);
                    helper.setCurretSize(picContainer.getChildCount()-1);
                }
            }
        }
    }
}
