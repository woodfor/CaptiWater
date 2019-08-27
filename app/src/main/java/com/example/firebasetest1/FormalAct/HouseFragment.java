package com.example.firebasetest1.FormalAct;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.House;

import java.util.ArrayList;
import java.util.List;

public class HouseFragment extends Fragment{
    View vHouse;
    String uuid;
    DailyInfoDatabase db = null;
    Context appContext;
    Context mContext;
    ListView lv_house;
    List<House> houses;
    ArrayAdapter arrayAdapter;
    int uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vHouse = inflater.inflate(R.layout.fragment_house, container, false);
        uuid = getArguments().getString("uuid",null);
        TextView houseName = (TextView) vHouse.findViewById(R.id.frag_house_text);
        lv_house = (ListView) vHouse.findViewById(R.id.lv_house);
        ImageView addAreaButton = (ImageView) vHouse.findViewById(R.id.add_area_btn);
        addAreaButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(),QuestionActivity.class);
            startActivity(intent);
        });

        new fillHouseList().execute();
        return vHouse;
    }

    protected class fillHouseList extends AsyncTask<Void,Void, List<String>>{

        @Override
        protected List<String> doInBackground(Void... voids) {
            db = Room.databaseBuilder(appContext,
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            uid = db.InfoDao().userExists(uuid);
            List<String> strings = new ArrayList<>();
            houses = db.InfoDao().getHouses(uid);
            for (House house: houses){
                strings.add(house.getName());
            }
            return strings;
        }
        @Override
        protected void onPostExecute(List<String> tempStrings){
            arrayAdapter = new ArrayAdapter(mContext,R.layout.list_item, R.id.tv, tempStrings);
            lv_house.setAdapter(arrayAdapter);

            House tmphouse = houses.get(0); //default choose 1
            tools.saveObject(appContext,"House","SelectedHouse",tmphouse);
            //SelectedHouse selectedHouse = new SelectedHouse(tmphouse.getId(),tmphouse.getName(),tmphouse.getBday(),tmphouse.getCpl(),tmphouse.getNop(),tmphouse.getUid());
            //bundle.putParcelable("house",selectedHouse);
            //setArguments(bundle);

            lv_house.setOnItemClickListener((parent, view, position, id) -> {
                House tmpHouse = houses.get((int) id);
                tools.saveObject(appContext,"House","SelectedHouse",tmphouse);

            });
            lv_house.setOnItemLongClickListener((parent, view, position, id) -> {
                House house = houses.get((int) id);
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_show_house_info);
                TextView tv_hn = dialog.findViewById(R.id.tv_houseName);
                TextView tv_bc = dialog.findViewById(R.id.tv_billCycle);
                TextView tv_cpl = dialog.findViewById(R.id.tv_cpl);
                TextView tv_nop = dialog.findViewById(R.id.tv_nop);
                Button btn_del = dialog.findViewById(R.id.btn_rename);
                btn_del.setOnClickListener(view1 -> {
                    new delHouse().execute(house.getId());
                    dialog.dismiss();
                });
                String hn = "House name: "+ house.getName();
                String bc = "Bill cycle: every "+ house.getBday();
                String cpl = "Cost per liter: "+ house.getCpl() + " $/L";
                String nop = "Number of people: "+ house.getNop();
                tv_hn.setText(hn);
                tv_bc.setText(bc);
                tv_cpl.setText(cpl);
                tv_nop.setText(nop);
                dialog.show();
                return false;
            });

        }
    }
    protected class delHouse extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            db.InfoDao().deleteHouse(integers[0]);
           return null;
        }
        @Override
        protected void onPostExecute(Void voids){
            new fillHouseList().execute();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }
}
