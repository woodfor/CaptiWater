package com.example.firebasetest1.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetest1.Chart.MyPieChart;
import com.example.firebasetest1.FragmentBottomNav.WaterUsageFragment;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import org.jetbrains.annotations.NotNull;

/**
 * The Class is for showing the chart after the click in pie chart in waterUsageFragment
 *
 * @author Junjie Lu
 */
public class ChartActivity extends AppCompatActivity {

    /**
     * Initial the ListView and send in the data from pie chart.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        PieChart pieChart = findViewById(R.id.pc_water_usage);
        ListView listView = findViewById(R.id.lv_chart);
        PieData pieData = WaterUsageFragment.openPieData;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Detail of Pie chart");
        if (pieData == null) {
            tools.toast_long(getApplicationContext(), "No data available");
            finish();
        }

        MyPieChart.setPieChart(pieChart);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        ArrayAdapter arrayAdapter = new ChartListAdapter(this, R.layout.listview_chart, pieData);
        listView.setAdapter(arrayAdapter);


    }

    /**
     * Register back button in the toolbar.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    /**
     * The class is for generating customize ListVIew
     */
    private class ChartListAdapter extends ArrayAdapter {
        Context context;
        PieData pieData;

        public ChartListAdapter(@NonNull Context context, int resource, PieData pieData) {
            super(context, resource);
            this.context = context;
            this.pieData = pieData;
        }

        @Override
        public int getCount() {
            return pieData.getDataSet().getEntryCount();
        }

        public View getView(int i, View view, @NotNull ViewGroup viewGroup) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.listview_chart, viewGroup, false);
                holder.space = view.findViewById(R.id.colorSpace);
                holder.tv_name = view.findViewById(R.id.tv_chartLabel);
                holder.tv_percentage = view.findViewById(R.id.tv_chartpecent);
                view.setTag(holder);
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (ViewHolder) view.getTag();
            }
            int colorcodes[] = pieData.getColors();
            holder.space.setBackgroundColor(colorcodes[i]);
            holder.tv_name.setText(pieData.getDataSet().getEntryForIndex(i).getLabel());
            holder.tv_percentage.setText(pieData.getDataSet().getEntryForIndex(i).getValue() + " ml");
            return view;
        }

        private class ViewHolder {

            private LinearLayout space;
            private TextView tv_name;
            private TextView tv_percentage;
        }
    }

}
