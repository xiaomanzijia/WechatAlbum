package com.licheng.github.wechatalbum.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.licheng.github.wechatalbum.AppContext;
import com.licheng.github.wechatalbum.R;
import com.licheng.github.wechatalbum.localalbum.FilterImageView;
import com.licheng.github.wechatalbum.localalbum.LocalAlbumHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


/**
 * Created by licheng on 23/1/16.
 */
public class LocalAlbumDetailAdapter extends BaseAdapter {

    private List<LocalAlbumHelper.LocalFile> fileList;
    private Context mContext;
    private CompoundButton.OnCheckedChangeListener listener;
    DisplayImageOptions options;
    private LocalAlbumHelper helper;

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public CompoundButton.OnCheckedChangeListener getListener() {
        return listener;
    }

    public LocalAlbumDetailAdapter(List<LocalAlbumHelper.LocalFile> fileList, Context mContext) {
        this.fileList = fileList;
        this.mContext = mContext;
        options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.dangkr_no_picture_small)
                .showImageOnFail(R.drawable.dangkr_no_picture_small)
                .showImageOnLoading(R.drawable.dangkr_no_picture_small)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .setImageSize(new ImageSize(((AppContext) mContext.getApplicationContext()).getQuarterWidth(), 0))
                .displayer(new SimpleBitmapDisplayer()).build();
        helper = LocalAlbumHelper.getInstance();
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
            viewHolder.imageView = (FilterImageView) convertView.findViewById(R.id.imageView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.checkBox.setOnCheckedChangeListener(getListener());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FilterImageView imageView = viewHolder.imageView;
        LocalAlbumHelper.LocalFile localFile = fileList.get(position);
//            FrescoLoader.getInstance().localDisplay(localFile.getThumbnailUri(), imageView, options);

        //图片显示 Fresco
        //设置图片的地址以及对图片的控制
//        String uri = localFile.getThumbnailUri();
//        Log.i("gridview图片URI",uri);
//        Uri imageUri = Uri.parse("content://media/external/images/media/36154");
//        imageView.setImageURI(imageUri);

        ImageLoader.getInstance().displayImage(localFile.getThumbnailUri(), new ImageViewAware(viewHolder.imageView), options,
                loadingListener, null, localFile.getOrientation());

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
        FilterImageView imageView;
        CheckBox checkBox;
    }

    SimpleImageLoadingListener loadingListener=new   SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap bm) {
            if (TextUtils.isEmpty(imageUri)) {
                return;
            }
            //由于很多图片是白色背景，在此处加一个#eeeeee的滤镜，防止checkbox看不清
            try {
                ((ImageView) view).getDrawable().setColorFilter(Color.argb(0xff, 0xee, 0xee, 0xee), PorterDuff.Mode.MULTIPLY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
