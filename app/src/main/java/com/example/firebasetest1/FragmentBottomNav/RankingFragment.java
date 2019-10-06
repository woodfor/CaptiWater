package com.example.firebasetest1.FragmentBottomNav;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.RankResult;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingFragment extends Fragment {
    private View vRanking;
    private ListView lv_ranking;
    int[] images = {R.drawable.ribbon1_orange, R.drawable.ribbon2, R.drawable.ribbon3};
    private Context mContext;
    private Context appContext;
    private List<RankResult> ranks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vRanking = inflater.inflate(R.layout.fragment_ranking, container, false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_house).setChecked(true);
        lv_ranking = (ListView) vRanking.findViewById(R.id.monthly_ranking_listview);

        //lv_ranking.setAdapter(customAdaptor);
        getRanking();
        return vRanking;
    }

    private void getRanking(){
        RequestQueue queue = Volley.newRequestQueue(appContext);
        String url = RestClient.BASE_URL + "report/MonthlyRank";
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.toString().equals("[]") || response.toString().isEmpty()) {
                        tools.toast_long(mContext, "No Ranking this month");
                    }else {
                        Type listType = new TypeToken<List<RankResult>>(){}.getType();
                        ranks = new Gson().fromJson(response.toString(),listType);
                        if (ranks != null){
                            ArrayAdapter<RankResult> arrayAdapter = new CustomAdaptor(mContext,R.layout.listview_ranking,ranks);
                            lv_ranking.setAdapter(arrayAdapter);
                        }
                    }
                },error -> tools.toast_long(mContext,error.toString())
        ){
                    @Override
                    public Map<String, String> getHeaders(){
                        Map<String, String> params = new HashMap<>();
                        params.put("Accept", "application/json");
                        return params;
                    }
        };
        queue.add(getRequest);
    }

    class CustomAdaptor extends ArrayAdapter<RankResult> {
        private Context context;
        private List<RankResult> rankResults;
        CustomAdaptor(@NonNull Context context, int resource, @NonNull List<RankResult> objects) {
            super(context, resource, objects);
            this.context = context;
            this.rankResults = objects;
        }
        private class ViewHolder {

            private ImageView iv_ranking;
            private TextView tv_houseName;
            private TextView tv_liter;
        }
        @Override
        public RankResult getItem(int position){
            return rankResults.get(position);
        }
        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            final ViewHolder holder;
            RankResult rankResult = getItem(position);
            if (convertView == null){
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_ranking,parent,false);
                holder.iv_ranking = convertView.findViewById(R.id.imageView_ranking);
                holder.tv_houseName = convertView.findViewById(R.id.tv_houseName);
                holder.tv_liter = convertView.findViewById(R.id.text_liter_ranking);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_liter.setText(rankResult.getTotalLiter() + " L");
            holder.tv_houseName.setText(rankResult.getHouseName());
            switch (position){
                case 0:
                    holder.iv_ranking.setImageResource(R.drawable.ribon_1);
                    break;
                case 1:
                    holder.iv_ranking.setImageResource(R.drawable.ribbon2);
                    break;
                case 2:
                    holder.iv_ranking.setImageResource(R.drawable.ribbon3);
                    break;
                default:
                    holder.iv_ranking.setVisibility(View.INVISIBLE);
                    break;
            }

            return convertView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }

}
