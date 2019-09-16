package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


public class WaterUsageFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    View vWaterUsage;
    House house =null;
    TextView tv_totalLiter;
    TextView tv_cost;
    TextView tv_estCost;
    TextView tv_houseName;
    Context mContext;
    private ProgressBar pg_status;
    TextView timeText;


    private static com.github.mikephil.charting.charts.LineChart chart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        ImageView leftBtn =  vWaterUsage.findViewById(R.id.left_btn);
        ImageView rightBtn = vWaterUsage.findViewById(R.id.right_btn);
        pg_status = vWaterUsage.findViewById(R.id.pg_status);
        tv_houseName = vWaterUsage.findViewById(R.id.tv_houseName);
        tv_totalLiter = vWaterUsage.findViewById(R.id.liter_text);
        tv_cost = vWaterUsage.findViewById(R.id.cost_text);
        tv_estCost = vWaterUsage.findViewById(R.id.estimated_cost_text);
        timeText =  vWaterUsage.findViewById(R.id.time_text);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        house = (House) tools.getHouse(getActivity().getApplicationContext());
        if (house==null){
            tools.toast_long(getActivity().getApplicationContext(),"House object error");
            getActivity().finish();
        }else{
            tv_houseName.setText(house.getName());
        }
        //set chart


        return vWaterUsage;
    }

    @Override
    public void onClick(View v) {

        String keyword = timeText.getText().toString();
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

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

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



    /*private void setData(Object object) {

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




    }*/




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