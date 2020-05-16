package com.shevaalex.android.rickmortydatabase.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentSplashScreenBinding;

public class SplashFragment extends Fragment {
    private Activity a;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            a = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FragmentSplashScreenBinding binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        ActionBar actionBar = ((AppCompatActivity) a).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View view = binding.getRoot();
        new Handler().postDelayed(() -> {
            a.onBackPressed();
            if (actionBar != null) {
                actionBar.show();
            }
        }, 1500);
        return view;
    }
}
