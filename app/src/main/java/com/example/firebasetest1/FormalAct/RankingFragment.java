package com.example.firebasetest1.FormalAct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.firebasetest1.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RankingFragment extends Fragment {
    View vRanking;
    ListView lv_ranking;
    int[] images = {R.drawable.ribbon1_orange, R.drawable.ribbon2, R.drawable.ribbon3};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vRanking = inflater.inflate(R.layout.fragment_ranking, container, false);

        lv_ranking = (ListView) vRanking.findViewById(R.id.monthly_ranking_listview);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        //lv_ranking.setAdapter(customAdaptor);

        return vRanking;
    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount(){
            return 3;
        }
        @Override
        public Object getItem(int position){
            return null;
        }
        @Override
        public long getItemId(int position){
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.listview_ranking, null);

            ImageView iv_ranking = (ImageView) view.findViewById(R.id.imageView_ranking);
            TextView tv_suburb = (TextView) view.findViewById(R.id.text_suburb_ranking);
            TextView tv_liter = (TextView) view.findViewById(R.id.text_liter_ranking);

            //iv_ranking.setImageResource();
            //tv_suburb.setText();
            //tv_liter.setText();
            return null;
        }
    }

}
