package com.irisid.user.it100_sample.Common.ui;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.irisid.user.it100_sample.Common.util.Logger;

public class IrisVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;

    public IrisVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IrisVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IrisVideoView(Context context) {
        super(context);
    }


    @Override
    public void setVideoURI(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.getContext(), uri);
        }catch (Exception e){
            Logger.d("setVideoURI Exception is "+ e.getMessage());
        }
        mVideoWidth = 1024;
        mVideoHeight = 600;
        super.setVideoURI(uri);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth;
            } else if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight;
            } else {
            }
        }
        setMeasuredDimension(width, height);
    }

}
