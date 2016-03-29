package com.ebay.roy.weatherapp.manager;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Roy on 3/29/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class , StorageUtils.class, DisplayImageOptions.class, ImageLoaderConfiguration.class, LruDiskCache.class})
public class ImageLoaderManagerTest {

    ImageLoaderManager imageLoaderManager;
    Context context;
    File cacheFile;

    @Before
    public void setUp() {
        constructMock();
        imageLoaderManager = new ImageLoaderManager();
    }

    private void constructMock() {
        context = PowerMockito.mock(Context.class);
        cacheFile = PowerMockito.mock(File.class);
        mockStatic(Environment.class);
        mockStatic(StorageUtils.class);
        mockStatic(DisplayImageOptions.class);
        mockStatic(ImageLoaderConfiguration.class);
        PowerMockito.when(StorageUtils.getCacheDirectory(context)).thenReturn(cacheFile);
        PowerMockito.when(cacheFile.mkdir()).thenReturn(anyBoolean());
    }

    @Test
    public void testImageLoader() {
        //imageLoaderManager.init(context);
        //@TODO : trying to mock storageUtils static method, with no success
        //Test by checking if specified method is called atleast once

    }

}
