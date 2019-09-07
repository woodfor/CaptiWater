package com.example.firebasetest1.FormalAct;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.HouseDAO;

import java.util.List;

public class HouseFragment extends Fragment{
    View vHouse;
    String uuid;
    DailyInfoDatabase db = null;
    Context appContext;
    Context mContext;
    ListView lv_house;
    List<HouseDAO> houseDAOS;
    ArrayAdapter arrayAdapter;
    int uid;
    House house;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vHouse = inflater.inflate(R.layout.fragment_house, container, false);
        uuid = getArguments().getString("uuid",null);
        TextView houseName = (TextView) vHouse.findViewById(R.id.tv_houseName);
        try{
            house = (House) tools.getObject(getActivity().getApplicationContext(),"House","House","House");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (house!=null){
            houseName.setText(house.getName());
        }else {
            houseName.setText("");
        }

        lv_house = (ListView) vHouse.findViewById(R.id.lv_house);
        ImageView addAreaButton = (ImageView) vHouse.findViewById(R.id.add_area_btn);
        addAreaButton.setOnClickListener(view -> {

            showDialog();
            // Intent intent = new Intent(getActivity(),QuestionActivity.class);
            //startActivity(intent);
        });

     //   new fillHouseList().execute();
        return vHouse;
    }

    private void showDialog(){
        final EditText edt_name = new EditText(mContext);
        final AlertDialog setNameDialog = new AlertDialog.Builder(mContext)
                .setTitle("Please input name of new area")
                .setView(edt_name)
                .setPositiveButton("Ok",null)
                .setNegativeButton("No",null)
                .show();
        setNameDialog.setCancelable(false);
        Button btn_positive = setNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn_positive.setOnClickListener(v -> {
            String tmp = edt_name.getText().toString().trim();
            if (!(tmp.isEmpty()))
            {
               // new insertHouse().execute(tmp);
                setNameDialog.dismiss();
            }
            else
                edt_name.setError("please input name");
        });
        setNameDialog.show();
    }

 /*   protected class insertHouse extends AsyncTask<String,Void,Long>{

        @Override
        protected Long doInBackground(String... strings) {
            HouseDAO newHouse  = new HouseDAO(strings[0],houseDAO.getBday(),houseDAO.getCpl(),houseDAO.getNop(),houseDAO.getUid());
            return db.InfoDao().insertHouse(newHouse);
        }
        @Override
        protected void onPostExecute(Long result){
            if (result!=0){
                tools.toast_long(getContext(),"Add successfully");
                new fillHouseList().execute();
            }
            else {
                tools.toast_long(getContext(),"Insert Failed, Try again");
            }
        }
    }*/

 /*   protected class fillHouseList extends AsyncTask<Void,Void, List<String>>{

        @Override
        protected List<String> doInBackground(Void... voids) {
            db = Room.databaseBuilder(appContext,
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            uid = db.InfoDao().userExists(uuid);
            List<String> strings = new ArrayList<>();
            houseDAOS = db.InfoDao().getHouses(uid);
            for (HouseDAO houseDAO: houseDAOS){
                strings.add(houseDAO.getName());
            }
            if(houseDAOS.isEmpty()){

            }else{
                tools.saveObject(appContext,"HouseDAO","SelectedHouse",houseDAOS.get(0));
            }
            return strings;
        }
        @Override
        protected void onPostExecute(List<String> tempStrings){
            arrayAdapter = new ArrayAdapter(mContext,R.layout.list_item, R.id.tv, tempStrings);
            lv_house.setAdapter(arrayAdapter);

//            HouseDAO tmphouse = houseDAOS.get(0); //default choose 1
  //          tools.saveObject(appContext,"HouseDAO","SelectedHouse",tmphouse);
            //SelectedHouse selectedHouse = new SelectedHouse(tmphouse.getId(),tmphouse.getName(),tmphouse.getBday(),tmphouse.getCpl(),tmphouse.getNop(),tmphouse.getUid());
            //bundle.putParcelable("houseDAO",selectedHouse);
            //setArguments(bundle);

            lv_house.setOnItemClickListener((parent, view, position, id) -> {
                HouseDAO tmpHouse = houseDAOS.get((int) id);
                tools.saveObject(appContext,"HouseDAO","SelectedHouse",tmpHouse);

            });
            lv_house.setOnItemLongClickListener((parent, view, position, id) -> {
                HouseDAO houseDAO = houseDAOS.get((int) id);
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_show_house_info);
                TextView tv_hn = dialog.findViewById(R.id.tv_houseName);
                TextView tv_bc = dialog.findViewById(R.id.tv_billCycle);
                TextView tv_cpl = dialog.findViewById(R.id.tv_cpl);
                TextView tv_nop = dialog.findViewById(R.id.tv_nop);
                Button btn_del = dialog.findViewById(R.id.btn_rename);
                btn_del.setOnClickListener(view1 -> {
                    new delHouse().execute(houseDAO.getId());
                    dialog.dismiss();
                });
                String hn = "HouseDAO name: "+ houseDAO.getName();
                String bc = "Bill cycle: every "+ houseDAO.getBday();
                String cpl = "Cost per liter: "+ houseDAO.getCpl() + " $/L";
                String nop = "Number of people: "+ houseDAO.getNop();
                tv_hn.setText(hn);
                tv_bc.setText(bc);
                tv_cpl.setText(cpl);
                tv_nop.setText(nop);
                dialog.show();
                return false;
            });

        }
    }*/
 /*   protected class delHouse extends AsyncTask<Integer,Void,Void>{

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
*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
    @Override
    public void onStop() {
        super.onStop();

    }
}
