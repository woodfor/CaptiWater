package com.example.firebasetest1.FragmentDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.firebasetest1.R;

/**
 * showing the page of 'About us'
 */
public class AboutUsFragment extends Fragment {
    View vAboutUs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAboutUs = inflater.inflate(R.layout.fragment_about_us, container, false);
        return vAboutUs;
    }
}
