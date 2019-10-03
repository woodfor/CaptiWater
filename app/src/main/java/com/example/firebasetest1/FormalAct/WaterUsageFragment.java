package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.Chart.MyMarkerView;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.RestClient;
import com.example.firebasetest1.RestClient.Model.DailyResult;
import com.example.firebasetest1.RestClient.Model.MonthlyResult;
import com.example.firebasetest1.RestClient.Model.YearlyResult;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;


public class WaterUsageFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    public static House house =null;
    View vWaterUsage;


    Context mContext;
    TextView timeText;
    ImageView leftBtn;
    ImageView rightBtn;
    private TextView tv_leftLiter;
    private String today = new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Calendar.getInstance().getTime()) ;

    private String[] selection = {"Daily","Monthly","Yearly"};
    private int index = 0;
    private RequestQueue queue;
    private LineChart lineChart;

    private  ArrayList<Integer> colors = new ArrayList<>();
    private ViewPager viewPager;
    private  CircleIndicator indicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        indicator = vWaterUsage.findViewById(R.id.indicator);
        leftBtn =  vWaterUsage.findViewById(R.id.left_btn);
        rightBtn = vWaterUsage.findViewById(R.id.right_btn);

        tv_leftLiter = vWaterUsage.findViewById(R.id.tv_liter_left);
        timeText =  vWaterUsage.findViewById(R.id.time_text);
        lineChart = vWaterUsage.findViewById(R.id.lc_water_usage);

        viewPager = vWaterUsage.findViewById(R.id.vp);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        house = (House) tools.getHouse(getActivity().getApplicationContext());
        if (house==null){
            tools.toast_long(getActivity().getApplicationContext(),"House object error");
            getActivity().finish();
        }

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());



        setLineChart();

        index = 0 ;
//
        timeText.setText(selection[index]);


        //set chart



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

        getSummary();
        return vWaterUsage;
    }


    private void getSummary()  {
        String period="";
        if(index == 0){
            period = "date";
        }else if (index==1){
            period = "month";
        }else if (index == 2){
            period = "year";
        }
        final String thisPeriod = period;
        //set total usage
        String url = RestClient.BASE_URL + "report/sum/" + thisPeriod +"/" + house.getHid()+"/"+ today;
        StringRequest getRequest = new StringRequest(Request.Method.GET,url,
                response -> {
                    if (!response.isEmpty()){
                        double usage = Double.parseDouble(response)/1000;

                        viewPager.setAdapter(new MyViewPagerAdapter(mContext,thisPeriod,usage));
                        indicator.setViewPager(viewPager);
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
                if (index!=0){
                    index --;
                }
                break;
            case R.id.right_btn:
                if (index!=2){
                    index ++;
                }
                break;
        }

        if (!timeText.getText().toString().equals(selection[index])){
            timeText.setText(selection[index]);
        }

        getSummary();

        //change color
        if (index ==0){
            leftBtn.setImageResource(R.drawable.left_arrow_grey);
          //  leftBtn.setVisibility(View.INVISIBLE);
        }else if (index == 2){
            rightBtn.setImageResource(R.drawable.right_arrow_grey);
           // rightBtn.setVisibility(View.INVISIBLE);
        }else {
            leftBtn.setImageResource(R.drawable.left_arrow);
            rightBtn.setImageResource(R.drawable.right_arrow);
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




    private void setLineChartData(String period,PieChart pieChart) {
        String url = RestClient.BASE_URL + "report/" + period +"/" + house.getHid() + "/" + today;
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


    private class MyViewPagerAdapter extends PagerAdapter {

        private Context context;
        private double usedLiter;
        private String period;

        public MyViewPagerAdapter(Context context,String period,double usedLiter) {
            this.context = context;
            this.usedLiter = usedLiter;
            this.period = period;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpage_waterusage, collection, false);
            ConstraintLayout constraintLayout = layout.findViewById(R.id.constraintLayout);
            PieChart pieChart = layout.findViewById(R.id.pc_water_usage);
            TextView tv_limitLiter = layout.findViewById(R.id.tv_limit_liter);
            TextView tv_totalLiter = layout.findViewById(R.id.liter_text);
            ProgressBar pg_status = layout.findViewById(R.id.pg_status);
            setPieChart(pieChart);
            int number = 0;

            switch(index){
                case 0:
                    number = 155 * house.getNop();
                    tv_limitLiter.setText("Used of "+ number + " L");
                    break;
                case 1:
                    number = 155*30*house.getNop();
                    tv_limitLiter.setText("Used of "+number + " L");
                    break;
                case 2:
                    number = 155*365*house.getNop();
                    tv_limitLiter.setText("Used of "+ number + " L");
                    break;
            }
            setLineChartData(period,pieChart);
            int limitLiter = number;
            pg_status.setProgress((int) usedLiter*100/ (limitLiter == 0 ? 1 : limitLiter));
            tv_totalLiter.setText(usedLiter+" L");
            tv_leftLiter.setText((int) (limitLiter - usedLiter)/house.getNop() + " left for the " + period  +" per person." );
            switch (position){
                case 0:
                    constraintLayout.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.GONE);
                    break;
                case 1:
                    constraintLayout.setVisibility(View.GONE);
                    pieChart.setVisibility(View.VISIBLE);
                    break;

            }
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            container.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

}