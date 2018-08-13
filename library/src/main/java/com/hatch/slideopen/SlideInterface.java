package com.hatch.slideopen;

import android.view.View;

/**
 * @author: Created by martin on 2018/8/2.
 */
public interface SlideInterface {

    /***
     * 包含ViewPager的容器
     * @param leftViewGroup ViewPager容器
     */
    void instantiateItem(View leftViewGroup);

    /***
     * 是否不能拖拽
     * @param isUnableToDrag true 不拦截
     */
    void setUnableToDrag(boolean isUnableToDrag);


    /***
     * 打开回调
     *
     * @param listener 触发打开事件
     */
    void setOnOpenListener(SlideOpenLayout.OnOpenListener listener);

    /***
     * X轴滑动距离回调
     *
     * @param listener X轴滑动距离回调
     */
    void setOnScrollListener(SlideOpenLayout.OnScrollListener listener);
}
