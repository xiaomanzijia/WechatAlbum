package com.licheng.github.wechatalbum.localalbum;

import android.database.Cursor;
import android.provider.MediaStore;


import com.licheng.github.wechatalbum.AppContext;
import com.licheng.github.wechatalbum.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 22/1/16.
 */
public class LocalAlbumHelper {
    private static LocalAlbumHelper instance;
    private AppContext mAppContext;
    private final List<LocalFile> fileList = new ArrayList<>();
    private final Map<String,List<LocalFile>> folders = new HashMap<>();
    private boolean isRunning = false;
    //大图遍历字段
    private static final String[] STORAGEIMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };

    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
        MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
    };

    public LocalAlbumHelper(AppContext mAppContext) {
        this.mAppContext = mAppContext;
    }

    public static LocalAlbumHelper getInstance(){
        return instance;
    }

    public static void init(AppContext AppContext){
        instance = new LocalAlbumHelper(AppContext);
        //开启线程读取初始化相册文件列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.initImage();
            }
        }).start();
    }

    public synchronized void initImage(){
        if (isRunning)
            return;
        isRunning=true;
        if(isInited()){
            return;
        }
        //获取大图的游标
        Cursor cursor = mAppContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//获取大图URI
                STORAGEIMAGES,//查询内容
                null,null,
                MediaStore.Images.Media.DATE_TAKEN+" DESC" //查询条件：按照拍照时间升序
        );
        if(cursor == null)
            return;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String path = cursor.getString(1);
            File file = new File(path);
            //判断大图是否存在
            if(file.exists()){
                //获取缩略图uri
                String thumbUri = getThumbnail(id,path);
                //获取大图uri
                String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build().toString();
                if (StringUtils.isEmpty(uri)){
                    continue;
                }
                //如果缩略图URI不存在,则用大图URI
                if(StringUtils.isEmpty(thumbUri)){
                    thumbUri = uri;
                }
                //获取目录名
                String folder = file.getParentFile().getName();

                LocalFile localfile = new LocalFile();
                localfile.setOriginalUri(uri);
                localfile.setThumbnailUri(thumbUri);
                //设置图片方向
                int degree = cursor.getInt(2);
                if(degree != 0){
                    degree = degree + 180;
                }
                localfile.setOrientation(360 - degree);

                fileList.add(localfile);
                if(folders.containsKey(folder)){
                    folders.get(folder).add(localfile);
                }else {
                    List<LocalFile> files = new ArrayList<>();
                    files.add(localfile);
                    folders.put(folder,files);
                }
            }
        }
        folders.put("所有图片",fileList);
        cursor.close();
        isRunning=false;
    }

    public boolean isInited() {
        return fileList.size() > 0;
    }

    private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = mAppContext.getContentResolver().query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,//获取大图缩略图URI
                THUMBNAIL_STORE_IMAGE, //查询内容
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",//查询条件：根据id查找
                new String[]{id + ""},
                null
                );
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int thumbId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(thumbId))
                    .build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    //获取文件列表以及所有照片信息
    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
    }

    //根据文件名获取该文件下所有文件
    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public static class LocalFile{
        private String originalUri;//原图URI
        private String thumbnailUri;//缩略图URI
        private int orientation;//图片旋转角度

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public String getOriginalUri() {
            return originalUri;
        }

        public void setOriginalUri(String originalUri) {
            this.originalUri = originalUri;
        }

        public String getThumbnailUri() {
            return thumbnailUri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }
    }

}
