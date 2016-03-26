package com.ebay.roy.weatherapp.manager;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by hungr on 26/03/2016.
 */
public class ImageLoaderManager
{

    private ImageLoader imageLoader;
    private Context context;



    public ImageLoader getImageLoader() {
        if (imageLoader == null || !imageLoader.isInited()) {
            init(context);
        }

        return imageLoader;
    }

    public void init(Context context) {

        this.context = context;

        if (imageLoader != null && imageLoader.isInited()) {
            imageLoader = ImageLoader.getInstance();
            return;
        }

        File cacheDir = StorageUtils.getCacheDirectory(context);
        cacheDir.mkdir();

        DisplayImageOptions options = new DisplayImageOptions.Builder()

                .showImageOnLoading(android.support.design.R.drawable.abc_spinner_mtrl_am_alpha)
                .showImageForEmptyUri(android.support.design.R.drawable.abc_spinner_mtrl_am_alpha)
                .showImageOnFail(android.support.design.R.drawable.abc_spinner_mtrl_am_alpha)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                        //.delayBeforeLoading(500)
                .displayer(new FadeInBitmapDisplayer(500))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .imageDownloader(new BaseImageDownloader(context, 1 * 1000, 20 * 1000)) //image download timeout, set 1 second
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(50 * 1024 * 1024)
                //.writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(options)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
    }

}
