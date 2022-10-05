package com.example.mysensor.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mysensor.SharedViewModel;
import com.example.mysensor.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.i("DetectionActivity","SharedViewModel is Initialized.");

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.detect.setText(sharedViewModel.detectionstr);
        getDetectionRecord();

        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getDetectionRecord(){
        addRow();
        fillInData();
    }

    public void addRow(){

    }

    public void fillInData(){
        String[] device = sharedViewModel.detectionstr.split("[;]",0);
        String[] dtime = sharedViewModel.detectiontime.split("[;]",0);
        TableLayout table = binding.TableLayout3;
        for(int i=0;i<table.getChildCount();i++){
            TableRow tr = (TableRow) table.getChildAt(i);
            TextView detection = (TextView) tr.getChildAt(0);
            TextView time = (TextView) tr.getChildAt(1);
            detection.setText(device[i]);
            time.setText(dtime[i]);
        }
    }
}