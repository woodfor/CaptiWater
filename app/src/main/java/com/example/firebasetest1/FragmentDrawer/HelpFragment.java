package com.example.firebasetest1.FragmentDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.firebasetest1.R;

public class HelpFragment extends Fragment {
    View vHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vHelp = inflater.inflate(R.layout.fragment_help, container, false);
        return vHelp;
    }
}
