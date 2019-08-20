package com.example.firebasetest1.Chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyBarChart {
    public static void setGraphChart(Context context, BarChart chart, List<DailyInfo> reports){

        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);

        chart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(Typeface.DEFAULT);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        /*xAxis.setValueFormatter(new IAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });*/
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        ArrayList<BarEntry> values4 = new ArrayList<>();
        int minday = 0;
        int maxday = 31;


        for (DailyInfo r: reports)
        {
            int date = Integer.parseInt(new SimpleDateFormat("dd").format(r.getTime()));

            if (date>minday)
            {
                minday = date;
            }
            if(date<maxday)
            {
                maxday = date;
            }
            int tmp;
           //values1.add(new BarEntry(date, r.getTotalcalconsume()));
            //values2.add(new BarEntry(date, basicBurned));
            //values3.add(new BarEntry(date, (tmp = (r.getTotalcalburned() - basicBurned))< 0 ? 0:tmp));
        }

        BarDataSet set1, set2, set3;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            set1 = new BarDataSet(values1, "Consumed");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Rest");
            set2.setColor(Color.rgb(164, 228, 251));
            set3 = new BarDataSet(values3, "Run");
            set3.setColor(Color.rgb(242, 247, 158));

            BarData data = new BarData(set1, set2, set3);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(Typeface.DEFAULT);

            chart.setData(data);
        }

        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(minday);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(minday + chart.getBarData().getGroupWidth(groupSpace, barSpace) * (maxday-minday+1));
        chart.groupBars(minday, groupSpace, barSpace);
        chart.invalidate();

    }
}
