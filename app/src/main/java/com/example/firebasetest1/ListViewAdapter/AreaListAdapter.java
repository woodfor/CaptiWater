package com.example.firebasetest1.ListViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.Area;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AreaListAdapter extends ArrayAdapter<Area> {
    private Context context;
    private List<Area> areas;

    public AreaListAdapter(@NonNull Context context, int resource, @NonNull List<Area> areas) {
        super(context, resource, areas);
        this.context = context;
        this.areas = areas;
    }

    @Override
    public Area getItem(int i) {
        return areas.get(i);
    }

    @NotNull
    @Override
    public View getView(int i, View view, @NotNull ViewGroup viewGroup) {
        final ViewHolder holder;
        Area area = getItem(i);
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.listview_area, viewGroup, false);
            holder.iv_option = view.findViewById(R.id.imageView_popup_area);
            holder.tv_areaNames = view.findViewById(R.id.text_area_list);
            view.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }


        holder.tv_areaNames.setText(area.getName());
        holder.iv_option.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.iv_option);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_area, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.lock_area:
                        //myRef.child("turn").setValue(0);
                        break;
                    case R.id.remove_area:
                        break;
                    case R.id.rename_area:
                        break;

                }
                return true;
            });
            popupMenu.show();
        });


        return view;
    }

//    @Override
//    public void onClick(View view) {
//        int position = (Integer) view.getTag();
//        Area area = (Area) getItem(position);
//        tools.saveObject(context.getApplicationContext(),"area","area", areas.get(position));
//        Intent intent = new Intent(context.getActivity(), TapActivity.class);
//        getActivity().startActivity(intent);
//
//    }

    private class ViewHolder {

       private ImageView iv_option;
       private TextView tv_areaNames;
    }
}
