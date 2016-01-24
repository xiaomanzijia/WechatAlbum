package com.licheng.github.wechatalbum.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.licheng.github.wechatalbum.R;
import com.licheng.github.wechatalbum.localalbum.LocalAlbumHelper;

import java.util.List;


/**
 * Created by licheng on 23/1/16.
 */
public class LocalAlbumDetailAdapter extends BaseAdapter {

    private List<LocalAlbumHelper.LocalFile> fileList;
    private Context mContext;
    private CompoundButton.OnCheckedChangeListener listener;

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public CompoundButton.OnCheckedChangeListener getListener() {
        return listener;
    }

    public LocalAlbumDetailAdapter(List<LocalAlbumHelper.LocalFile> fileList, Context mContext) {
        this.fileList = fileList;
        this.mContext = mContext;

    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item,null);
            viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.imageView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.checkBox.setOnCheckedChangeListener(getListener());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SimpleDraweeView imageView = viewHolder.imageView;
        LocalAlbumHelper.LocalFile localFile = fileList.get(position);
//            FrescoLoader.getInstance().localDisplay(localFile.getThumbnailUri(), imageView, options);

        //图片显示 Fresco
        //设置图片的地址以及对图片的控制
        String uri = localFile.getThumbnailUri();
        Log.i("gridview图片URI",uri);
        Uri imageUri = Uri.parse(uri);
        imageView.setImageURI(imageUri);

        viewHolder.checkBox.setTag(localFile);
//        viewHolder.checkBox.setChecked(checkedItems.contains(localFile));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"点击多选", Toast.LENGTH_SHORT).show();
//                showViewPager(i);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView imageView;
        CheckBox checkBox;
    }
}
