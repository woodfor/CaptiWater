package com.example.firebasetest1.Chart;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

/**
 * Initial the pie chart by specifying some settings
 */
public class MyPieChart {

    public static void setPieChart(PieChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.95f);
        Typeface tfLight;
        tfLight = Typeface.DEFAULT_BOLD;
        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText("Ratio of area");

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        // chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTypeface(Typeface.DEFAULT);
        chart.setEntryLabelTextSize(12f);
    }
}
