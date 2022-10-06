package com.example.mysensor.ui.notifications;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.divider.MaterialDivider;

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
        int beforecount = addRow();
        fillInData(beforecount);
    }

    public int addRow(){
        if(sharedViewModel.detectionstr == "" || sharedViewModel.detectiontime =="") return 0;
        String[] device = sharedViewModel.detectionstr.split("[;]",0);
        TableLayout table = binding.TableLayout3;
        int beforecount = table.getChildCount();
        for(int j=0;j< device.length;j++) {
            TableRow tr = new TableRow(table.getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));

            //detection msg
            TextView msg = new TextView(table.getContext());
            msg.setLayoutParams(new TableRow.LayoutParams(270, 90));
            msg.setPadding(0, 5, 0, 0);
            msg.setTextSize(20);
            msg.setTextColor(Color.BLACK);

            //detection time
            TextView time = new TextView(table.getContext());
            time.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
            time.setTextSize(20);

            //add to row and table
            tr.addView(msg);
            tr.addView(time);
            table.addView(tr, new TableLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));

            //add divider
            MaterialDivider md = new MaterialDivider(table.getContext());
            md.setLayoutParams(new AppBarLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT,6));
            //table.addView(md, new TableLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
            table.addView(md);

        }
        return beforecount;
    }

    public void fillInData(int beforecount){
        if(sharedViewModel.detectionstr == "" || sharedViewModel.detectiontime =="") return;
        String[] device = sharedViewModel.detectionstr.split("[;]",0);
        String[] dtime = sharedViewModel.detectiontime.split("[;]",0);
        TableLayout table = binding.TableLayout3;
        int num = 0;
        for(int i=beforecount;i < table.getChildCount();i+=2){
            View v = table.getChildAt(i);
            if(v instanceof MaterialDivider) continue;
            TableRow tr = (TableRow) v;
            TextView detection = (TextView) tr.getChildAt(0);
            TextView time = (TextView) tr.getChildAt(1);
            detection.setText("Sensor "+device[num]+" signal detected.");
            time.setText(dtime[num++]);
        }
    }
}