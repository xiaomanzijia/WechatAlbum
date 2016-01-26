package com.licheng.github.wechatalbum.localalbum;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.licheng.github.wechatalbum.R;
import com.licheng.github.wechatalbum.adapter.LocalAlbumDetailAdapter;

import java.util.List;

/**
 * Created by licheng on 23/1/16.
 */
public class LocalAlbumDetail extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    GridView gridView;
    TextView title;//标题
    View titleBar;//标题栏
    View pagerContainer;//图片显示部分
    TextView finish,headerFinish;
    String folder;
    TextView mCountView;
    List<LocalAlbumHelper.LocalFile> currentFolder = null;

    ImageView mBackView;
    View headerBar;
    CheckBox checkBox;
    LocalAlbumHelper helper = LocalAlbumHelper.getInstance();
    List<LocalAlbumHelper.LocalFile> checkedItems ;
    List<LocalAlbumHelper.LocalFile> checkedItemsAll ;
    private LocalAlbumDetailAdapter adapter;

    private String foldername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_album_detail);

//        if(!LocalAlbumHelper.getInstance().isInited()){
//            finish();
//            return;
//        }

        initView();

        foldername = getIntent().getExtras().getString("folder_names");

        Log.d("文件夹名字",foldername);

        new Thread(new Runnable() {
            @Override
            public void run() {
                helper.initImage();
                //获取相册照片
                final List<LocalAlbumHelper.LocalFile> folderPics = helper.getFolder(foldername);
                Log.d("文件夹相册图片数量",folderPics.size()+"");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(folderPics != null){
                            currentFolder = folderPics;
                            adapter = new LocalAlbumDetailAdapter(currentFolder,LocalAlbumDetail.this);
                            //设置多选点击监听
                            adapter.setListener(LocalAlbumDetail.this);
                            gridView.setAdapter(adapter);

                            //设置当前选中数量
                            if(checkedItems.size()+helper.getCurretSize()>0){
                                finish.setText("完成（"+(checkedItems.size()+helper.getCurretSize())+"/9)");
                                finish.setEnabled(true);
                            }else {
                                finish.setText("完成");
                                finish.setEnabled(false);
                            }


                        }
                    }
                });
            }
        }).start();
        checkedItems = helper.getCheckedItems();
        checkedItemsAll = helper.getCheckedItemsAll();
        LocalAlbumHelper.getInstance().setResultOk(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("生命周期","onPostResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("生命周期","onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("生命周期","onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("生命周期","onDestroy");
    }

    private void initView() {
        title = (TextView) findViewById(R.id.album_title);
        finish = (TextView) findViewById(R.id.album_finish);
        headerFinish=(TextView)findViewById(R.id.header_finish);
        gridView = (GridView) findViewById(R.id.gridview);
        titleBar = findViewById(R.id.album_title_bar);
        pagerContainer = findViewById(R.id.pagerview);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        headerBar =findViewById(R.id.album_item_header_bar);
        checkBox= (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(this);
        mBackView.setOnClickListener(this);
        finish.setOnClickListener(this);
        headerFinish.setOnClickListener(this);
        findViewById(R.id.album_back).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.album_finish:
                finish();
                helper.setResultOk(true);
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isChecked){
            if(checkedItems.contains(buttonView.getTag())||checkedItemsAll.contains(buttonView.getTag())){
                checkedItems.remove(buttonView.getTag());
                checkedItemsAll.remove(buttonView.getTag());
            }
        }else {
            if(!checkedItems.contains(buttonView.getTag())||!checkedItemsAll.contains(buttonView.getTag())){
                checkedItems.add((LocalAlbumHelper.LocalFile) buttonView.getTag());
                checkedItemsAll.add((LocalAlbumHelper.LocalFile) buttonView.getTag());
                Log.d("选中的照片数量",(checkedItems.size()+" "+helper.getCurretSize())+" ");
                if(checkedItems.size()+helper.getCurretSize()>=9){
                    Toast.makeText(LocalAlbumDetail.this,"最多只能选9张照片",Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    return;
                }
            }else{
                return;
            }
        }
        if(checkedItems.size()+helper.getCurretSize()>0){
            finish.setText("完成（"+(checkedItems.size()+helper.getCurretSize())+"/9)");
            finish.setEnabled(true);
        }else {
            finish.setText("完成");
            finish.setEnabled(false);
        }
    }
}
