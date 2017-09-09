package com.lidong.settings;

import android.graphics.Point;

/**
 * Created by LiDong on 2017/3/9.
 */
public class PointView extends Point {
    int index;//用于转换密码的下标

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void set(int x, int y) {
        super.set(x, y);
    }
    public PointView(int x, int y){
        super(x,y);
    }
}
