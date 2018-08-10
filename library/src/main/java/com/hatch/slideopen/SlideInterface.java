package com.hatch.slideopen;

import android.view.View;

/**
 * @Description:
 * @author: Created by martin on 2018/8/2.
 */
public interface SlideInterface {

    /***
     * 包含ViewPager的容器
     * @param leftViewGroup
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
     * @param listener
     */
    void setOnOpenListener(SlideOpenLayout.OnOpenListener listener);

    /***
     * X轴滑动距离回调
     *
     * @param listener
     */
    void setOnScrollListener(SlideOpenLayout.OnScrollListener listener);
}
