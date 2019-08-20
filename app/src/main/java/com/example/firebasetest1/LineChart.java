package com.example.firebasetest1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.firebasetest1.Chart.MyMarkerView;
import com.example.firebasetest1.Room.DailyInfo;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LineChart extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener,OnChartValueSelectedListener {
    private com.github.mikephil.charting.charts.LineChart chart;
    private SeekBar seekBarX;
    private TextView tvX;
    DailyInfoDatabase db = null;
    List<DailyInfo> dailyInfos;
    String uuid;
    private static final String TAG = "lineChart";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_line_chart);
        setTitle("DailyWaterUsage");
        tvX = findViewById(R.id.tvXMax);
        seekBarX = findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.lineChart);
        chart.setOnChartValueSelectedListener(this);
        //search database
        SharedPreferences sharedPrefs = getApplication().getSharedPreferences(
                "PREF_UNIQUE_ID", Context.MODE_PRIVATE);
        uuid = sharedPrefs.getString("PREF_UNIQUE_ID", null);
        db = Room.databaseBuilder(getApplicationContext(),
                DailyInfoDatabase.class, "dailyInfo_database")
                .fallbackToDestructiveMigration()
                .build();


        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);


        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        //chart.setScaleMinima(3f, 3f);
        seekBarX.setProgress(Integer.parseInt(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime())));

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = chart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setGranularity(1f); // one hour
        //xAxis.setAxisMaximum(23f);
        //xAxis.setAxisMaximum(0f);
        /*xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });*/

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        //leftAxis.setAxisMinimum(0f);
        //leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
    }

    private void setData() {

        ArrayList<Entry> values = new ArrayList<>();
        Map<Integer,Integer> mapHour = new HashMap<>();
        for(int i = 0; i <24;i++)
        {
            mapHour.put(i,0);
        }
        // increment by 1 hour
        for (DailyInfo d : dailyInfos) {
            int sum = mapHour.containsKey(d.getHour()) ? mapHour.get(d.getHour()):0;
            mapHour.put(d.getHour() ,sum + d.getUsage());
        }
        Iterator iterator = mapHour.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            Log.d(TAG, pair.getKey()+" "+ pair.getValue());
            values.add(new Entry(Float.parseFloat(pair.getKey()+""),Float.parseFloat(pair.getValue()+"")));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Water usage by hour");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.BLUE);
        data.setValueTextSize(9f);

        // set data
        chart.setData(data);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        new addGraphChart().execute(seekBarX.getProgress());
        // redraw

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private class addGraphChart extends AsyncTask<Integer,Void, List<DailyInfo>> {



        @Override
        protected List<DailyInfo> doInBackground(Integer... integers) {
            Date today = Calendar.getInstance().getTime();
            //int date = Integer.parseInt(new SimpleDateFormat("HH").format(today));
            int month = Integer.parseInt(new SimpleDateFormat("MM").format(today));
            int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(today));
            dailyInfos = db.InfoDao().monthyTotalUsage(uuid,integers[0],month,year);
            return dailyInfos;
        }

        @Override
        protected void onPostExecute(List<DailyInfo> dailyInfos) {
            // add data
            setData();
            chart.invalidate();

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
