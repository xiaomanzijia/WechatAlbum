package com.licheng.github.wechatalbum.localalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.licheng.github.wechatalbum.R;
import com.licheng.github.wechatalbum.adapter.FolderApater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by licheng on 22/1/16.
 */
public class LocalAlbum extends Activity implements View.OnClickListener {
    ListView listView;
    ImageView progress;
    View camera;
    private static final int IMAGECAPTURE = 1;
    private LocalAlbumHelper helper;
    private FolderApater adapter;
    //应用是否销毁标志
    protected boolean isDestroy;
    private List<String> folderNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_album);
        isDestroy = false;
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化本地相片文件夹
                LocalAlbumHelper.getInstance().initImage();
                //初始化完毕之后 显示文件夹列表
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isDestroy){
                            initFolderAdapter();
                            listView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();

        //获取文件夹名字
        folderNames = adapter.getFolderNames();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LocalAlbum.this,LocalAlbumDetail.class);
                intent.putExtra("folder_names",folderNames.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
            }
        });


    }

    private void initFolderAdapter() {
        listView.setAdapter(adapter);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.local_album_list);
        camera = findViewById(R.id.loacal_album_camera);
        camera.setOnClickListener(this);
//        camera.setVisibility(View.GONE);
        progress = (ImageView) findViewById(R.id.progress_bar);
        helper = LocalAlbumHelper.getInstance();
        adapter = new FolderApater(helper.getFolderMap(),LocalAlbum.this);
        folderNames = new ArrayList<>();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loacal_album_camera:
                //调用相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMAGECAPTURE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
}
