package com.example.firebasetest1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View vDisplayUnit;
    private static com.github.mikephil.charting.charts.LineChart chart;
    private SeekBar seekBarX;
    private TextView tvX;
    private static DailyInfoDatabase db = null;
    private static List<DailyInfo> dailyInfos;
    private static String uuid;
    private static final String TAG = "lineChart";
    private Context mContext;
    private Context appContext;
    private OnFragmentInteractionListener mListener;
    //private static int[] colourSet = {ColorTemplate.getHoloBlue(),ColorTemplate.COLORFUL_COLORS,Color.YELLOW,Color.GREEN};
    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vDisplayUnit = inflater.inflate(R.layout.fragment_report, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Report");
       getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tvX = vDisplayUnit.findViewById(R.id.tvXMax);
        seekBarX = vDisplayUnit.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        chart = vDisplayUnit.findViewById(R.id.lineChart);
        chart.setOnChartValueSelectedListener(this);
        //search database
        SharedPreferences sharedPrefs = appContext.getSharedPreferences(
                "PREF_UNIQUE_ID", Context.MODE_PRIVATE);
        uuid = sharedPrefs.getString("PREF_UNIQUE_ID", null);
        db = Room.databaseBuilder(appContext,
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
        return vDisplayUnit;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        tvX.setText(String.valueOf(seekBarX.getProgress()));
        new addGraphChart().execute(seekBarX.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private static void setData() {

        Map<String,ArrayList<DailyInfo>> mapTap = new HashMap<>();
        // increment by 1 hour
        for (DailyInfo d : dailyInfos) {
            if (mapTap.containsKey(d.getName())){
                mapTap.get(d.getName()).add(d);
            }
            else{
                ArrayList<DailyInfo> list = new ArrayList<>();
                list.add(d);
                mapTap.put(d.getName(),list);
            }
        }
        int j = 0;
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (Map.Entry<String, ArrayList<DailyInfo>> integerIntegerEntry : mapTap.entrySet()) {
            ArrayList<Entry> values = new ArrayList<>();
            SparseIntArray mapHour = new SparseIntArray();
            for(int i = 0; i <24;i++)
            {
                mapHour.put(i,0);
            }
            for (DailyInfo d : integerIntegerEntry.getValue()){
                int sum = mapHour.get(d.getHour(),-1)!=-1 ? mapHour.get(d.getHour()):0;
                mapHour.put(d.getHour() ,sum + d.getUsage());
            }
            for (int i = 0; i < mapHour.size(); i++) {
                int key = mapHour.keyAt(i);
                values.add(new Entry((float) key, (float) mapHour.get(key)));
            }
            if (j>3){
                j=0;
            }
            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(values, integerIntegerEntry.getKey());
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

    private static class addGraphChart extends AsyncTask<Integer,Void, List<DailyInfo>> {



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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
