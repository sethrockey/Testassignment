package com.sj.cityweather.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by HP on 7/11/2017.
 */

public class CustomAdapter extends CursorAdapter {

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public CustomAdapter(Context context, Cursor c, View.OnClickListener onClickListener,View.OnLongClickListener onLongClickListener) {
        super(context, c);
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate your view here.
        TextView view = new TextView(context);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String id = cursor.getString(0);
        String name = cursor.getString(1);
        // Get all the values
        // Use it however you need to
        TextView textView = (TextView) view;
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        textView.setText(name);
        textView.setTag(name);
        textView.setOnClickListener(onClickListener);
        textView.setOnLongClickListener(onLongClickListener);
    }
}
