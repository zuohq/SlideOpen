# SlideOpen
## Demo
![image](https://github.com/zuohq/SlideOpen/blob/master/art/slide-open.gif)
## Installation
SlideOpen is installed by adding the following dependency to your build.gradle file:

    dependencies {
        implementation 'com.liulishuo.filedownloader:library:1.7.5'
    }
## Usage
    
    1. <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        ......
        <item name="slide_open_style">@style/CustomSlideOpen</item>
       </style>

    2. <style name="CustomSlideOpen">
        <item name="android:paddingRight">2dp</item>
        <item name="android:paddingLeft">5dp</item>
        <item name="android:src">@drawable/ic_right_arrow</item>
        <item name="so_drag_label">@string/drag_label</item>
        <item name="so_release_label">@string/release_label</item>
        <item name="so_child_margin_left">5dp</item>
        <item name="android:textSize">14sp</item>
        <item name="android:textColor">@color/gray</item>
        <item name="so_ratio">1.5</item>
        <item name="so_friction">3.5</item>
       </style>
    
    3.  final SlideInterface mSlideOpen = findViewById(R.id.slide_open_layout);
        mSlideOpen.instantiateItem(view);
        mSlideOpen.setUnableToDrag(true);
        mSlideOpen.setOnOpenListener(new SlideOpenLayout.OnOpenListener() {
                @Override
                public void onOpen() {
                    //open
                }
            });
    
            mSlideOpen.setOnScrollListener(new SlideOpenLayout.OnScrollListener() {
                @Override
                public void onPageScrolled(int scrollX) {
                    //......
                }
            });
## License
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.