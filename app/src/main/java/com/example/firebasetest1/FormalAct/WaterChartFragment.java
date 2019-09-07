package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.firebasetest1.Chart.MyMarkerView;
import com.example.firebasetest1.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaterChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WaterChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaterChartFragment extends Fragment implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View vDisplayUnit;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //
    private Context mContext;
    //
    private SeekBar seekBarXDay;
    private SeekBar seekBarXMonth;
    private SeekBar seekBarXYear;
    private TextView tvDay;
    private TextView tvMonth;
    private TextView tvYear;
    private LineChart chart;
    LinearLayout yearLayout;
    LinearLayout monthLayout;
    LinearLayout dateLayout;
    int date;
    int year;
    int month;
    
    private OnFragmentInteractionListener mListener;

    public WaterChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaterChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaterChartFragment newInstance(String param1, String param2) {
        WaterChartFragment fragment = new WaterChartFragment();
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
        vDisplayUnit =  inflater.inflate(R.layout.fragment_water_chart, container, false);
        chart = vDisplayUnit.findViewById(R.id.lc_water_usage);
        tvDay = vDisplayUnit.findViewById(R.id.tvXMaxDay);
        tvMonth = vDisplayUnit.findViewById(R.id.tvXMaxMonth);
        tvYear = vDisplayUnit.findViewById(R.id.tvXMaxYear);
        seekBarXMonth = vDisplayUnit.findViewById(R.id.seekBarMonth);
        seekBarXYear = vDisplayUnit.findViewById(R.id.seekBarYear);
        seekBarXDay = vDisplayUnit.findViewById(R.id.seekBarDay);

        yearLayout = vDisplayUnit.findViewById(R.id.year);
        monthLayout = vDisplayUnit.findViewById(R.id.month);
        dateLayout = vDisplayUnit.findViewById(R.id.date);

        yearLayout.setVisibility(View.VISIBLE);
        monthLayout.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.VISIBLE);

        seekBarXMonth.setMax(12);
        seekBarXMonth.setMin(1);

        seekBarXYear.setMin(17);
        seekBarXYear.setMax(30);

        seekBarXDay.setMax(31);
        seekBarXDay.setMin(1);
        date = Integer.parseInt(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
        month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        Integer[] integers = {year,month,date};

        setChartBasic();

        seekBarXDay.setProgress(date);
        tvDay.setText(date+"");
        seekBarXMonth.setProgress(month);
        tvMonth.setText(month+"");
        seekBarXYear.setProgress(year-2000);
        tvYear.setText(year+"");

        seekBarXDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    int tmpday = seekBarXDay.getProgress();
                    int tmpmonth = seekBarXMonth.getProgress();
                    int tmpyear = 2000 + seekBarXYear.getProgress();
                    //     new fillData().execute(tmpyear,tmpmonth,tmpday);

                    tvDay.setText(tmpday+"");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarXMonth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    int tmpday = seekBarXDay.getProgress();
                    int tmpmonth = seekBarXMonth.getProgress();
                    int tmpyear = 2000 + seekBarXYear.getProgress();
                    // new fillData().execute(tmpyear,tmpmonth,tmpday);

                    tvMonth.setText(tmpmonth + "");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarXYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    int tmpday = seekBarXDay.getProgress();
                    int tmpmonth = seekBarXMonth.getProgress();
                    int tmpyear = 2000 + seekBarXYear.getProgress();
                    //   new fillData().execute(tmpyear,tmpmonth,tmpday);
                    tvYear.setText(tmpyear + "");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });






        chart.setOnChartValueSelectedListener(this);
        return  vDisplayUnit;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

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
