package com.licheng.github.wechatalbum.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.licheng.github.wechatalbum.R;
import com.licheng.github.wechatalbum.localalbum.FilterImageView;
import com.licheng.github.wechatalbum.localalbum.LocalAlbumHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by licheng on 22/1/16.
 */
public class FolderApater extends BaseAdapter {

    private Map<String,List<LocalAlbumHelper.LocalFile>> folderlist;
    private Context mContext;
    private List<String> folderNames;
    private LocalAlbumHelper helper;

    public FolderApater(Map<String, List<LocalAlbumHelper.LocalFile>> folderlist, Context mContext) {
        this.folderlist = folderlist;
        this.mContext = mContext;
        folderNames = new ArrayList<>();
        //获取文件夹名字
        Iterator iterator = folderlist.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String filename = (String) entry.getKey();
            folderNames.add(filename);
            helper = LocalAlbumHelper.getInstance();
        }

        Log.d("文件夹数量",folderNames.size()+"");

        //将文件夹名字按照图片数量从大到小排序
        Collections.sort(folderNames, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                Integer num1 = helper.getFolder(lhs).size();
                Integer num2 = helper.getFolder(rhs).size();
                return num2.compareTo(num1);
            }
        });

    }

    @Override
    public int getCount() {
        return folderNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_albumfoler, null);
            viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = folderNames.get(position);
        List<LocalAlbumHelper.LocalFile> files = folderlist.get(name);
        viewHolder.textView.setText(name + "(" + files.size() + ")");
        if (files.size() > 0) {
            String uri = files.get(0).getThumbnailUri();
            Log.d("第一张图片URI",uri);
//            //图片显示 Fresco
            Uri imageUri = Uri.parse(uri);
            viewHolder.imageView.setImageURI(imageUri);
        }
        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView imageView;
        TextView textView;
    }

    public List<String> getFolderNames() {
        return folderNames;
    }
}
