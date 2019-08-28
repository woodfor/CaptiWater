package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.firebasetest1.Chart.MyMarkerView;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.House;
import com.example.firebasetest1.Room.Model.DailyResult;
import com.example.firebasetest1.Room.Model.MonthlyResult;
import com.example.firebasetest1.Room.Model.YearlyResult;
import com.example.firebasetest1.Room.Records;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class WaterUsageFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    View vWaterUsage;
    House house;
    DailyInfoDatabase db = null;
    TextView tv_totalLiter;
    TextView tv_cost;
    TextView tv_estCost;
    TextView tv_houseName;
    Context mContext;
    private SeekBar seekBarXDay;
    private SeekBar seekBarXMonth;
    private SeekBar seekBarXYear;
    private TextView tvDay;
    private TextView tvMonth;
    private TextView tvYear;
    private ProgressBar pg_status;
    TextView timeText;
    LinearLayout yearLayout;
    LinearLayout monthLayout;
    LinearLayout dateLayout;
    List<Records> records;
    String period;
    int date;
    int year;
    int month;
    private static com.github.mikephil.charting.charts.LineChart chart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        ImageView leftBtn = (ImageView) vWaterUsage.findViewById(R.id.left_btn);
        ImageView rightBtn = (ImageView) vWaterUsage.findViewById(R.id.right_btn);
        pg_status = vWaterUsage.findViewById(R.id.pg_status);
        tv_houseName = vWaterUsage.findViewById(R.id.tv_houseName);
        tv_totalLiter = vWaterUsage.findViewById(R.id.liter_text);
        tv_cost = vWaterUsage.findViewById(R.id.cost_text);
        tv_estCost = vWaterUsage.findViewById(R.id.estimated_cost_text);
        chart = vWaterUsage.findViewById(R.id.lc_water_usage);
        tvDay = vWaterUsage.findViewById(R.id.tvXMaxDay);
        tvMonth = vWaterUsage.findViewById(R.id.tvXMaxMonth);
        tvYear = vWaterUsage.findViewById(R.id.tvXMaxYear);
        seekBarXMonth = vWaterUsage.findViewById(R.id.seekBarMonth);
        seekBarXYear = vWaterUsage.findViewById(R.id.seekBarYear);
        seekBarXDay = vWaterUsage.findViewById(R.id.seekBarDay);
        timeText = (TextView) vWaterUsage.findViewById(R.id.time_text);
        yearLayout = vWaterUsage.findViewById(R.id.year);
        monthLayout = vWaterUsage.findViewById(R.id.month);
        dateLayout = vWaterUsage.findViewById(R.id.date);
        period = timeText.getText().toString();
        yearLayout.setVisibility(View.VISIBLE);
        monthLayout.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.VISIBLE);

        seekBarXMonth.setMax(12);
        seekBarXMonth.setMin(1);

        seekBarXYear.setMin(2017);
        seekBarXYear.setMax(2030);

        seekBarXDay.setMax(31);
        seekBarXDay.setMin(1);
        date = Integer.parseInt(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
        month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        Integer[] integers = {year,month,date};

        seekBarXDay.setProgress(date);
        tvDay.setText(date+"");
        seekBarXMonth.setProgress(month);
        tvMonth.setText(month+"");
        seekBarXYear.setProgress(year);
        tvYear.setText(year+"");

        chart.setOnChartValueSelectedListener(this);

        db = Room.databaseBuilder(getActivity().getApplicationContext(),
                DailyInfoDatabase.class, "dailyInfo_database")
                .fallbackToDestructiveMigration()
                .build();

        SharedPreferences mPrefs = getActivity().getApplicationContext().getSharedPreferences("House",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("SelectedHouse","");
        if (!json.isEmpty()){
            house = gson.fromJson(json,House.class);
            String houseName = house.getName();
            tv_houseName.setText(houseName);
        }else {
            tv_houseName.setText("Please select an area");
        }
        //set chart
        setChartBasic();



        seekBarXDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpday = seekBarXDay.getProgress();
                int tmpmonth = seekBarXMonth.getProgress();
                int tmpyear = seekBarXYear.getProgress();
                new fillData().execute(tmpyear,tmpmonth,tmpday);
                new fillChart().execute(tmpyear,tmpmonth,tmpday);
                tvDay.setText(tmpday+"");
            }
        });
        seekBarXMonth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpday = seekBarXDay.getProgress();
                int tmpmonth = seekBarXMonth.getProgress();
                int tmpyear = seekBarXYear.getProgress();
                new fillData().execute(tmpyear,tmpmonth,tmpday);
                new fillChart().execute(tmpyear,tmpmonth,tmpday);
                tvMonth.setText(tmpmonth + "");
            }
        });
        seekBarXYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpday = seekBarXDay.getProgress();
                int tmpmonth = seekBarXMonth.getProgress();
                int tmpyear = seekBarXYear.getProgress();
                new fillData().execute(tmpyear,tmpmonth,tmpday);
                new fillChart().execute(tmpyear,tmpmonth,tmpday);
                tvMonth.setText(tmpyear + "");
            }
        });


        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        new fillData().execute(integers);
        new fillChart().execute(integers);

        return vWaterUsage;
    }

    @Override
    public void onClick(View v) {

        String keyword = timeText.getText().toString();
        monthLayout.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.VISIBLE);
        switch (v.getId()) {
            case R.id.left_btn:
                if (keyword.equals("Daily")) {
                    timeText.setText("Yearly");
                } else if (keyword.equals("Monthly")) {
                    timeText.setText("Daily");
                } else if (keyword.equals("Yearly")) {
                    timeText.setText("Monthly");
                }
                break;
            case R.id.right_btn:
                if (keyword.equals("Daily")) {
                    timeText.setText("Monthly");
                } else if (keyword.equals("Monthly")) {
                    timeText.setText("Yearly");
                } else if (keyword.equals("Yearly")) {
                    timeText.setText("Daily");
                }
                break;
        }
        period = timeText.getText().toString();
        int date = Integer.parseInt(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        Integer[] integers = {year,month,date};
        new fillChart().execute(integers);
        new fillData().execute(integers);
        if (period.equals("Daily")){

        }else if (period.equals("Monthly")){
            dateLayout.setVisibility(View.GONE);

        }else if (period.equals("Yearly")){
            dateLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }



    protected class fillData extends AsyncTask<Integer,Void,Integer>{
        protected int averageUsage=0;
        @Override
        protected Integer doInBackground(Integer... integers) {
            int usage = -1;



            if (period.equals("Daily")){
                int monthlySumUsage = db.InfoDao().getMonthlySumUsage(house.getId(),integers[0],integers[1]);
                usage= db.InfoDao().getDailySumUsage(house.getId(),integers[0],integers[1],integers[2]);
                averageUsage = monthlySumUsage / 31;
            }else if (period.equals("Monthly")){
                int yearlySumUsage = db.InfoDao().getYearlySumUsage(house.getId(),integers[0]);
                usage = db.InfoDao().getMonthlySumUsage(house.getId(),integers[0],integers[1]);
                averageUsage = yearlySumUsage / 12;
            }else if (period.equals("Yearly")){
                usage = db.InfoDao().getYearlySumUsage(house.getId(),integers[0]);
                int totalUsage = db.InfoDao().getTotalSumUsage(house.getId());
                int divider = db.InfoDao().getDistinctYear(house.getId());
                averageUsage = totalUsage /divider ;
            }
            return usage;
        }
        @Override
        protected void onPostExecute(Integer integer){
            if (integer == -1){

            }else {
                tv_totalLiter.setText(integer+"");
                double cpl = Double.parseDouble(house.getCpl());
                double cost = integer * cpl / 1000 ;
                double estCost = averageUsage * cpl / 1000;
                tv_cost.setText(cost+"");
                tv_estCost.setText(estCost+"");
                int ratio;
                if (averageUsage == 0){
                    ratio = 0;
                }else
                    ratio = integer/averageUsage * 100;
                pg_status.setProgress(ratio);
            }
        }
    }

    private void setChartBasic(){
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
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(10f);
        l.setTextSize(12f);
        l.setEnabled(true);

        l.setTextColor(Color.BLACK);

        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = chart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setGranularity(1f);
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

    private void setData(Object object) {

        List<DailyResult> dailyResults;
        List<MonthlyResult> monthlyResults;
        List<YearlyResult> yearlyResults;

        if (period.equals("Daily")){
            dailyResults = (List<DailyResult>) object;
            Map<String,ArrayList<DailyResult>> mapTap = new HashMap<>();
            // increment by 1 hour
            for (DailyResult d : dailyResults) {
                if (mapTap.containsKey(d.getName())){
                    mapTap.get(d.getName()).add(d);
                }
                else{
                    ArrayList<DailyResult> list = new ArrayList<>();
                    list.add(d);
                    mapTap.put(d.getName(),list);
                }
            }
            int j = 0;
            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            for (Map.Entry<String, ArrayList<DailyResult>> entry : mapTap.entrySet()) {
                ArrayList<Entry> values = new ArrayList<>();
                SparseIntArray mapHour = new SparseIntArray();
                for(int i = 0; i <24;i++)
                {
                    mapHour.put(i,0);
                }
                for (DailyResult d:entry.getValue()){
                    mapHour.put(d.getHour(),d.getSumUsage());
                }
                for (int i = 0; i < mapHour.size(); i++) {
                    int key = mapHour.keyAt(i);
                    values.add(new Entry((float) key, (float) mapHour.get(key)));
                }
                if (j>4){
                    j=0;
                }
                LineDataSet set1 = new LineDataSet(values, entry.getKey());
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setValueTextColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
                dataSets.add(set1);
                j++;
                // create a data object with the data sets

            }
            if (!dataSets.isEmpty()){
                LineData data = new LineData(dataSets);
                // set data
                chart.setData(data);
            }else {
                chart.clear();
            }

        }else if (period.equals("Monthly")){
            monthlyResults = (List<MonthlyResult>) object;
            Map<String,ArrayList<MonthlyResult>> mapTap = new HashMap<>();
            // increment by 1 hour
            for (MonthlyResult d : monthlyResults) {
                if (mapTap.containsKey(d.getName())){
                    mapTap.get(d.getName()).add(d);
                }
                else{
                    ArrayList<MonthlyResult> list = new ArrayList<>();
                    list.add(d);
                    mapTap.put(d.getName(),list);
                }
            }
            int j = 0;
            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            for (Map.Entry<String, ArrayList<MonthlyResult>> entry : mapTap.entrySet()) {
                ArrayList<Entry> values = new ArrayList<>();
                SparseIntArray mapDate = new SparseIntArray();
                for(int i = 0; i <31;i++)
                {
                    mapDate.put(i,0);
                }
                for (MonthlyResult d:entry.getValue()){
                    mapDate.put(d.getDate(),d.getUsage());
                }
                for (int i = 0; i < mapDate.size(); i++) {
                    int key = mapDate.keyAt(i);
                    values.add(new Entry((float) key, (float) mapDate.get(key)));
                }
                if (j>3){
                    j=0;
                }
                LineDataSet set1 = new LineDataSet(values, entry.getKey());
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setValueTextColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
                dataSets.add(set1);
                j++;
                // create a data object with the data sets

            }
            if (!dataSets.isEmpty()){
                LineData data = new LineData(dataSets);
                // set data
                chart.setData(data);
            }else {
                chart.clear();
            }

        }else if (period.equals("Yearly")){
            yearlyResults = (List<YearlyResult>) object;
            Map<String,ArrayList<YearlyResult>> mapTap = new HashMap<>();
            // increment by 1 hour
            for (YearlyResult d : yearlyResults) {
                if (mapTap.containsKey(d.getName())){
                    mapTap.get(d.getName()).add(d);
                }
                else{
                    ArrayList<YearlyResult> list = new ArrayList<>();
                    list.add(d);
                    mapTap.put(d.getName(),list);
                }
            }
            int j = 0;
            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            for (Map.Entry<String, ArrayList<YearlyResult>> entry : mapTap.entrySet()) {
                ArrayList<Entry> values = new ArrayList<>();
                SparseIntArray mapMonth = new SparseIntArray();
                for(int i = 0; i <12;i++)
                {
                    mapMonth.put(i,0);
                }
                for (YearlyResult d:entry.getValue()){
                    mapMonth.put(d.getMonth(),d.getUsage());
                }
                for (int i = 0; i < mapMonth.size(); i++) {
                    int key = mapMonth.keyAt(i);
                    values.add(new Entry((float) key, (float) mapMonth.get(key)));
                }
                if (j>3){
                    j=0;
                }
                LineDataSet set1 = new LineDataSet(values, entry.getKey());
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setValueTextColor(ColorTemplate.COLORFUL_COLORS[j]);
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
                dataSets.add(set1);
                j++;
                // create a data object with the data sets

            }
            if (!dataSets.isEmpty()){
                LineData data = new LineData(dataSets);
                // set data
                chart.setData(data);
            }else {
                chart.clear();
            }
        }




    }
    private class fillChart extends AsyncTask<Integer,Void, Object> {

        @Override
        protected Object doInBackground(Integer... integers) {

            Object objects = null;
            if (period.equals("Daily")){
                objects = db.InfoDao().getRecordsByDay(integers[0],integers[1],integers[2],house.getId());
            }else if (period.equals("Monthly")){
                objects = db.InfoDao().getRecordsByMonth(integers[0],integers[1],house.getId());
            }else if (period.equals("Yearly")){
                objects = db.InfoDao().getRecordsByYear(integers[0],house.getId());
            }else
                objects = null;

            return objects;
        }

        @Override
        protected void onPostExecute(Object object) {
            if (object!=null){
                // add data
                setData(object);
                chart.invalidate();
            } else
                tools.toast_long(mContext,"Try again");

        }
    }



    @Override
    public void onStop() {
        super.onStop();
        //db.close();
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;

    }

    private void refreshFrg()
    {
        String tag = this.getClass().getName();
        //remove the last two char
        if (tag.indexOf("$")!=-1)
            tag = tag.substring(0,tag.indexOf("$"));
        Fragment frg = null;
        frg = this;
        FragmentTransaction ft = getFragmentManager ().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(frg).attach(frg).commit();
    }

}