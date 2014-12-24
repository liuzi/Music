package com.example.jiangliu.music.Class;

import android.app.Notification;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by jiangliu on 14-12-23.
 */
public class MyTextView extends TextView {
    @Override
    public boolean isFocused() {
        return true;
    }
    public MyTextView(Context context){
        super(context);
    }
    public MyTextView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public MyTextView(Context context,AttributeSet attributeSet,int w){
        super(context,attributeSet,w);
    }
}
