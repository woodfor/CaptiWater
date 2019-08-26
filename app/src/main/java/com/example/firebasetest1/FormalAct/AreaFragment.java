package com.example.firebasetest1.FormalAct;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firebasetest1.R;

public class AreaFragment extends Fragment implements View.OnClickListener{
    View vArea;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vArea = inflater.inflate(R.layout.fragment_area, container, false);

        TextView houseName = (TextView) vArea.findViewById(R.id.frag_area_text);
        ListView tapListView = (ListView) vArea.findViewById(R.id.tap_listview);
        ImageView addTapButton = (ImageView) vArea.findViewById(R.id.add_tap_btn);
        addTapButton.setOnClickListener(this);

        return vArea;
    }

    @Override
    public void onClick(View v) {

    }
}
