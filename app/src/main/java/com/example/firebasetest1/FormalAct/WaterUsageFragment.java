package com.example.firebasetest1.FormalAct;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.Chart.MyMarkerView;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.GsonBuilder.DateDeserializer;
import com.example.firebasetest1.GsonBuilder.TimeDeserializer;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.Model.Report;
import com.example.firebasetest1.RestClient.Model.Tap;
import com.example.firebasetest1.RestClient.RestClient;
import com.example.firebasetest1.Room.Model.DailyResult;
import com.example.firebasetest1.Room.Model.MonthlyResult;
import com.example.firebasetest1.Room.Model.YearlyResult;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class WaterUsageFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    public static House house =null;
    View vWaterUsage;
    TextView tv_totalLiter;
    TextView tv_cost;
    TextView tv_estCost;
    TextView tv_houseName;
    Context mContext;
    TextView timeText;
    ImageView leftBtn;
    ImageView rightBtn;
    private ProgressBar pg_status;
    private EditText edt_date;
    private String[] selection = {"Daily","Monthly","Yearly"};
    private int index = 0;
    private RequestQueue queue;
    private LineChart lineChart;
    private PieChart pieChart;
    private  ArrayList<Integer> colors = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        leftBtn =  vWaterUsage.findViewById(R.id.left_btn);
        rightBtn = vWaterUsage.findViewById(R.id.right_btn);
        pg_status = vWaterUsage.findViewById(R.id.pg_status);
        tv_houseName = vWaterUsage.findViewById(R.id.tv_titleName);
        tv_totalLiter = vWaterUsage.findViewById(R.id.liter_text);
        tv_cost = vWaterUsage.findViewById(R.id.cost_text);
        tv_estCost = vWaterUsage.findViewById(R.id.estimated_cost_text);
        timeText =  vWaterUsage.findViewById(R.id.time_text);
        edt_date = vWaterUsage.findViewById(R.id.edt_date);
        lineChart = vWaterUsage.findViewById(R.id.lc_water_usage);
        pieChart = vWaterUsage.findViewById(R.id.pc_water_usage);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        house = (House) tools.getHouse(getActivity().getApplicationContext());
        if (house==null){
            tools.toast_long(getActivity().getApplicationContext(),"House object error");
            getActivity().finish();
        }else{
            tv_houseName.setText(house.getName());
        }
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        tv_estCost.setText(155 * house.getNop() + "");

        final Calendar today = Calendar.getInstance();
        final Calendar myCalendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        final DatePickerDialog.OnDateSetListener date  = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edt_date.setText(sdf.format(myCalendar.getTime()));
        };

        final MonthPickerDialog.Builder yearMonthPickerbuilder = new MonthPickerDialog.Builder(getActivity(),(selectedMonth, selectedYear) -> {
            myCalendar.set(Calendar.YEAR, selectedYear);
            myCalendar.set(Calendar.MONTH, selectedMonth);
            edt_date.setText(new SimpleDateFormat("MM-yyyy",Locale.US).format(myCalendar.getTime()));
        },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

        final MonthPickerDialog.Builder yearPickerbuilder = new MonthPickerDialog.Builder(getActivity(),(selectedMonth, selectedYear) -> {
            myCalendar.set(Calendar.YEAR, selectedYear);
            myCalendar.set(Calendar.MONTH, selectedMonth);
            edt_date.setText(new SimpleDateFormat("yyyy",Locale.US).format(myCalendar.getTime()));
        },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

        timeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (index){
                    case 0:
                        edt_date.setText(sdf.format(today.getTime()));
                        edt_date.setOnClickListener(v -> {
                            DatePickerDialog dialog =new DatePickerDialog(mContext, date, today
                                    .get(Calendar.YEAR), today.get(Calendar.MONTH),
                                    today.get(Calendar.DAY_OF_MONTH));
                            dialog.getDatePicker().setMaxDate(new Date().getTime());

                            dialog.show();
                        });
                        break;
                    case 1:
                        edt_date.setText(new SimpleDateFormat("MM-yyyy",Locale.US).format(today.getTime()));
                        edt_date.setOnClickListener(view -> {
                            yearMonthPickerbuilder
                                    .setMaxYear(today.get(Calendar.YEAR))
                                    .setMaxMonth(today.get(Calendar.MONTH))
                                    .build().show();
                        });
                        break;
                    case 2:
                        edt_date.setText(String.valueOf(today.get(Calendar.YEAR)));
                        edt_date.setOnClickListener(view -> {
                            yearPickerbuilder.setMaxYear(today.get(Calendar.YEAR))
                                    .showYearOnly().build().show();
                        });
                        break;
                }
            }
        });

        edt_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{

                    getSummary(index,edt_date.getText().toString());
                }catch (Exception e){

                }

            }
        });

        index = 0 ;
        leftBtn.callOnClick();
//        timeText.setText("Daily");
//        try {
//            getSummary(index,edt_date.getText().toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        //set chart
        setLineChart();
        setPieChart(pieChart);
        //set color
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return vWaterUsage;
    }


    private void getSummary(int chosen, String date) throws ParseException {
        String period="";
        String time="";
        int number = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        if(chosen == 0){
            period = "date";
            time = format.format(new SimpleDateFormat("dd-MM-yyyy",Locale.US).parse(date));
            number = 155 * house.getNop();
            tv_estCost.setText(number + "");
        }else if (chosen==1){
            period = "month";
            time = format.format(new SimpleDateFormat("MM-yyyy",Locale.US).parse(date));
            number = 155*30*house.getNop();
            tv_estCost.setText(number + "");
        }else if (chosen == 2){
            period = "year";
            time = format.format(new SimpleDateFormat("yyyy",Locale.US).parse(date));
            number = 155*365*house.getNop();
            tv_estCost.setText(number + "");
        }
        setLineChartData(period,time);
        final int divider = number;
        String url = RestClient.BASE_URL + "report/sum/" + period +"/" + house.getHid()+"/"+time;
        StringRequest getRequest = new StringRequest(Request.Method.GET,url,
                response -> {
                    if (!response.isEmpty()){
                        double usage = Double.parseDouble(response)/1000;
                        pg_status.setProgress((int) usage*100/ (divider == 0 ? 1 : divider));
                        tv_cost.setText(usage + "");
                        tv_totalLiter.setText(usage+"");
                    }else {
                        tools.toast_long(mContext,"get data error");
                    }
                },error -> {
                    tools.toast_long(mContext,error.toString());
                });
        queue.add(getRequest);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.left_btn:
                rightBtn.setVisibility(View.VISIBLE);
                if (index!=0){
                    index --;
                }
                break;
            case R.id.right_btn:
                leftBtn.setVisibility(View.VISIBLE);
                if (index!=2){
                    index ++;
                }
                break;
        }

        if (!timeText.getText().toString().equals(selection[index])){
            timeText.setText(selection[index]);
        }
        if (index ==0){
            leftBtn.setVisibility(View.INVISIBLE);
        }else if (index == 2){
            rightBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


    private void setLineChart(){
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(10f);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setEnabled(true);
        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setGranularity(1f);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);

    }


 /*   protected class fillData extends AsyncTask<Integer,Void,Integer>{
        protected int averageUsage=0;
        @Override
        protected Integer doInBackground(Integer... integers) {
            int usage = -1;



            if (period.equals("Daily")){
                int monthlySumUsage = db.InfoDao().getMonthlySumUsage(houseDAO.getId(),integers[0],integers[1]);
                usage= db.InfoDao().getDailySumUsage(houseDAO.getId(),integers[0],integers[1],integers[2]);
                averageUsage = monthlySumUsage / 31;
            }else if (period.equals("Monthly")){
                int yearlySumUsage = db.InfoDao().getYearlySumUsage(houseDAO.getId(),integers[0]);
                usage = db.InfoDao().getMonthlySumUsage(houseDAO.getId(),integers[0],integers[1]);
                averageUsage = yearlySumUsage / 12;
            }else if (period.equals("Yearly")){
                usage = db.InfoDao().getYearlySumUsage(houseDAO.getId(),integers[0]);
                int totalUsage = db.InfoDao().getTotalSumUsage(houseDAO.getId());
                int divider = db.InfoDao().getDistinctYear(houseDAO.getId());
                averageUsage = totalUsage /divider ;
            }
            return usage;
        }
        @Override
        protected void onPostExecute(Integer integer){
            if (integer == -1){

            }else {
                tv_totalLiter.setText(integer+"");
                double cpl = Double.parseDouble(houseDAO.getCpl());
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
    }*/



    private void setLineChartData(String period,String date) {
        String url = RestClient.BASE_URL + "report/" + period +"/" + house.getHid() + "/" + date;
            JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        if (!response.toString().equals("[]") && !response.toString().isEmpty()) {
                            if (period .equals("date")) {
                                Type listType = new TypeToken<List<DailyResult>>() {
                                }.getType();
                                List<DailyResult> dailyResults = new Gson().fromJson(response.toString(), listType);
                                //set pie chart
                                Map<String,Integer> mapPie = new HashMap<>();

                                Map<String, ArrayList<DailyResult>> mapTap = new HashMap<>();
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
                                        if (mapPie.containsKey(d.getArea())){
                                             mapPie.put(d.getArea(),mapPie.get(d.getArea()) + (int) d.getSumUsage());
                                        }else {
                                            mapPie.put(d.getArea(),(int) d.getSumUsage());
                                        }
                                    }
                                    //set piechart

                                ArrayList<PieEntry> entries = new ArrayList<>();

                                for (Map.Entry<String,Integer> cursor : mapPie.entrySet())
                                {
                                    entries.add(new PieEntry(cursor.getValue(),cursor.getKey()));
                                }
                                PieDataSet dataSet = new PieDataSet(entries, "Label(ml)");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(colors);
                                PieData piedata = new PieData(dataSet);
                                piedata.setValueTextSize(11f);
                                piedata.setValueTextColor(Color.BLACK);
                                piedata.setValueTypeface(Typeface.DEFAULT);
                                pieChart.setData(piedata);
                                pieChart.invalidate();


                                //set linechart
                                    int j = 0;
                                    List<ILineDataSet> dataSets = new ArrayList<>();
                                    for (Map.Entry<String, ArrayList<DailyResult>> entry : mapTap.entrySet()) {
                                        ArrayList<Entry> values = new ArrayList<>();
                                        SparseIntArray mapHour = new SparseIntArray();
                                        for(int i = 0; i <24;i++)
                                        {
                                            mapHour.put(i,0);
                                        }
                                        for (DailyResult d:entry.getValue()){
                                            mapHour.put(d.getHour(),(int) d.getSumUsage());
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
                                        lineChart.setData(data);
                                    }else {
                                        lineChart.clear();
                                    }

                                }  else if (period.equals("month")){
                                        Type listType = new TypeToken<List<MonthlyResult>>() {
                                        }.getType();
                                        List<MonthlyResult> monthlyResults = new Gson().fromJson(response.toString(), listType);
                                            Map<String,ArrayList<MonthlyResult>> mapTap = new HashMap<>();
                                            Map<String,Integer> mapPie = new HashMap<>();
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
                                                if (mapPie.containsKey(d.getArea())){
                                                    mapPie.put(d.getArea(),mapPie.get(d.getArea()) + (int) d.getUsage());
                                                }else {
                                                    mapPie.put(d.getArea(),(int) d.getUsage());
                                                }
                                            }
                                            //set pie chart

                                            ArrayList<PieEntry> entries = new ArrayList<>();

                                            for (Map.Entry<String,Integer> cursor : mapPie.entrySet())
                                            {
                                                entries.add(new PieEntry(cursor.getValue(),cursor.getKey()));
                                            }
                                            PieDataSet dataSet = new PieDataSet(entries, "Label(ml)");
                                            dataSet.setSliceSpace(3f);
                                            dataSet.setSelectionShift(5f);
                                            dataSet.setColors(colors);
                                            PieData piedata = new PieData(dataSet);
                                            piedata.setValueTextSize(11f);
                                            piedata.setValueTextColor(Color.BLACK);
                                            piedata.setValueTypeface(Typeface.DEFAULT);
                                            pieChart.setData(piedata);
                                            pieChart.invalidate();

                                            //set line chart
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
                                                    mapDate.put(d.getDate(),(int) d.getUsage());
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
                                                lineChart.setData(data);
                                            }else {
                                                lineChart.clear();
                                            }

                                        }else if (period.equals("year")) {
                                Type listType = new TypeToken<List<YearlyResult>>() {
                                }.getType();
                                List<YearlyResult> yearlyResults = new Gson().fromJson(response.toString(), listType);
                                Map<String, ArrayList<YearlyResult>> mapTap = new HashMap<>();
                                Map<String,Integer> mapPie = new HashMap<>();
                                // increment by 1 hour
                                for (YearlyResult d : yearlyResults) {
                                    if (mapTap.containsKey(d.getName())) {
                                        mapTap.get(d.getName()).add(d);
                                    } else {
                                        ArrayList<YearlyResult> list = new ArrayList<>();
                                        list.add(d);
                                        mapTap.put(d.getName(), list);
                                    }
                                    if (mapPie.containsKey(d.getArea())){
                                        mapPie.put(d.getArea(),mapPie.get(d.getArea()) + (int) d.getUsage());
                                    }else {
                                        mapPie.put(d.getArea(),(int) d.getUsage());
                                    }
                                }
                                //set piechart
                                ArrayList<PieEntry> entries = new ArrayList<>();

                                for (Map.Entry<String,Integer> cursor : mapPie.entrySet())
                                {
                                    entries.add(new PieEntry(cursor.getValue(),cursor.getKey()));
                                }
                                PieDataSet dataSet = new PieDataSet(entries, "Label(ml)");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(colors);
                                PieData piedata = new PieData(dataSet);
                                piedata.setValueTextSize(11f);
                                piedata.setValueTextColor(Color.BLACK);
                                piedata.setValueTypeface(Typeface.DEFAULT);
                                pieChart.setData(piedata);
                                pieChart.invalidate();

                                //set linechart
                                int j = 0;
                                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                for (Map.Entry<String, ArrayList<YearlyResult>> entry : mapTap.entrySet()) {
                                    ArrayList<Entry> values = new ArrayList<>();
                                    SparseIntArray mapMonth = new SparseIntArray();
                                    for (int i = 0; i < 12; i++) {
                                        mapMonth.put(i, 0);
                                    }
                                    for (YearlyResult d : entry.getValue()) {
                                        mapMonth.put(d.getMonth(), (int) d.getUsage());
                                    }
                                    for (int i = 0; i < mapMonth.size(); i++) {
                                        int key = mapMonth.keyAt(i);
                                        values.add(new Entry((float) key, (float) mapMonth.get(key)));
                                    }
                                    if (j > 3) {
                                        j = 0;
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
                                if (!dataSets.isEmpty()) {
                                    LineData data = new LineData(dataSets);
                                    // set data
                                    lineChart.setData(data);
                                } else {
                                    lineChart.clear();
                                }
                            }

                        }else {
                            tools.toast_long(mContext,"No data for this period");
                        }
                    },
                    error -> {
                        tools.toast_long(mContext, error.toString());
                    });
            queue.add(getRequest);

    }

    private void setPieChart(PieChart chart){
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
        chart.setRotationEnabled(true);
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