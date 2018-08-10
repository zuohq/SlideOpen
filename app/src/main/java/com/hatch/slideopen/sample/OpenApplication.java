package com.hatch.slideopen.sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * @Description:
 * @author: Created by martin on 2018/8/8.
 */
public class OpenApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();

        //初始化facebook 加载库
        Fresco.initialize(this, config);
    }
}
