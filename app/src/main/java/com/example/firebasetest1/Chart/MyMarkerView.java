package com.example.firebasetest1.Chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.firebasetest1.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * show the detailed information in line chart
 *
 * @author Junjie Lu
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {
    private final TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText((int) ce.getLow() + " o'clock, usage: " + Utils.formatNumber(ce.getHigh(), 0, true) + " ml");
        } else {

            tvContent.setText((int) e.getX() + " o'clock, usage: " + Utils.formatNumber(e.getY(), 0, true) + " ml");
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
