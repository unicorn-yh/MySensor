package com.example.mysensor.ui.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;
import android.widget.TableRow;
import android.widget.TableLayout;

import com.example.mysensor.R;
import com.google.android.material.chip.*;
import android.widget.GridLayout.LayoutParams;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import java.util.*;
//import com.example.mysensor.ui.dashboard.DashboardFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.mysensor.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //get data from dashboard
        getParentFragmentManager().setFragmentResultListener("dataFrom1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String datastr = result.getString("df1");
                binding.dataFrom1.setText(datastr);
            }
        });

        //send sensor name to dashboard
        TableLayout table = binding.tableLayout;
        String namestr = getSensorName();
        Bundle result = new Bundle();
        result.putString("df2",namestr);
        getParentFragmentManager().setFragmentResult("dataName",result);
        /*Button updatebtn2 = binding.updatebtn2;
        updatebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableLayout table = binding.tableLayout;
                String namestr = getSensorName();
                Bundle result = new Bundle();
                result.putString("df2",namestr);
                getParentFragmentManager().setFragmentResult("dataName",result);
            }
        });*/

        //send signal
        setupSensor();
        setupChip();
        sendTableSignal();
        setupAddButton();
        setupUpdate();

        //final TextView textView = binding.textView;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getSensorName(){
        String namestr = "";
        TableLayout table = binding.tableLayout;
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            String name = t.getText().toString();
            namestr += (name+";");
        }
        return namestr;
    }

    public void setupSensor(){
        TableLayout table = binding.tableLayout;
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            t.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //send sensor name to dashboard
                    TableLayout table = binding.tableLayout;
                    String namestr = getSensorName();
                    //binding.updatebtn2.setClickable(true);
                    //binding.updatebtn2.performClick();
                    //binding.updatebtn2.setClickable(false);
                    Bundle result = new Bundle();
                    result.putString("df2", namestr);
                    getParentFragmentManager().setFragmentResult("dataName", result);
                }
            });
        }
    }

    public boolean detectSignal(double intensity,double RR){
        if (intensity<=650 && RR>=6){
            return true;
        }
        return false;
    }

    public void sendRowSignal(TableRow trow){ //detectSignal

        //get data from dashboard
        TableLayout table = binding.tableLayout;
        int data_count = table.getChildCount();
        double[][] arr = new double[data_count][2];
        for(int i=0;i<data_count;i++){
            for(int j=0;j<2;j++){
                arr[i][j] = 0;
            }
        }
        String datastr = binding.dataFrom1.getText().toString();
        if(datastr != ""){
            String[] dataarr = datastr.split("[;]", 0);  //each row data
            for(int i=0;i<dataarr.length;i++){
                String[] tmp = dataarr[i].split("[,]", 0);  //split into intensity and resistance
                arr[i][0] = Double.parseDouble(tmp[0]);
                arr[i][1] = Double.parseDouble(tmp[1]);
            }
        }

        //output result from data
        for(int i=0;i<data_count;i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            if (trow == row){
                Chip c = (Chip) row.getChildAt(1);
                TextView signaltxt = (TextView) row.getChildAt(3);
                if (true){
                    //tmp = dataarr[i].split("[,]",0);
                    if (detectSignal(arr[i][0],arr[i][1]) ){
                        signaltxt.setText("Signal detected.");
                        signaltxt.setTextColor(Color.RED);
                        signaltxt.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        signaltxt.setText("No signal.");
                        signaltxt.setTextColor(Color.GRAY);
                        signaltxt.setTypeface(null);
                    }
                }
                else{
                    signaltxt.setText(" — ");
                    signaltxt.setTextColor(Color.GRAY);
                    signaltxt.setTypeface(null);
                }
            }
        }
    }

    public void sendTableSignal(){
        TableLayout table = binding.tableLayout;
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            sendRowSignal(row);
        }
    }


    public void setupChip(){
        TableLayout table = binding.tableLayout;
        for(int i=0;i<table.getChildCount();i++){
            TableRow row = (TableRow) table.getChildAt(i);
            Chip c = (Chip) row.getChildAt(1);
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c.isChecked() == true){
                        sendRowSignal(row);
                    }
                    else{
                        TextView signaltxt = (TextView) row.getChildAt(3);
                        signaltxt.setText("No signal.");
                        signaltxt.setTextColor(Color.GRAY);
                        signaltxt.setTypeface(null);
                    }

                }
            });
        }
    }

    public void setupAddButton(){
        FloatingActionButton addbtn = binding.addbtn;
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableLayout tl = binding.tableLayout;
                int data_count = tl.getChildCount();
                TableRow tr = new TableRow(tl.getContext());
                tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

                //sensor name
                TextView name = new TextView(tl.getContext());
                name.setText("Sensor "+Integer.toString(data_count+1));
                name.setLayoutParams(new TableRow.LayoutParams(160,LayoutParams.MATCH_PARENT));
                name.setPadding(92,18,0,0);
                name.setTextSize(20);
                name.setTypeface(null,Typeface.BOLD);

                //chip
                Chip c = new Chip(tl.getContext());
                c.setLayoutParams(new TableRow.LayoutParams(80,LayoutParams.WRAP_CONTENT));
                c.setCheckable(true);
                c.setPadding(0,0,35,0);
                c.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(tl.getContext(), R.color.teal_200)));
                c.setCheckedIconVisible(true);

                //space
                Space s = new Space(tl.getContext());
                s.setLayoutParams(new TableRow.LayoutParams(10,LayoutParams.WRAP_CONTENT));

                //signal status
                TextView signaltxt = new TextView(tl.getContext());
                signaltxt.setText("No signal.");
                signaltxt.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

                tr.addView(name);
                tr.addView(c);
                tr.addView(s);
                tr.addView(signaltxt);
                tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

                TableLayout table = binding.tableLayout;
                String namestr = getSensorName();
                Bundle result = new Bundle();
                result.putString("df2",namestr);
                getParentFragmentManager().setFragmentResult("dataName",result);

            }
        });
    }

    public void setupUpdate(){
        Button upbtn = binding.updatebtn2;
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTableSignal();
            }
        });
    }

}