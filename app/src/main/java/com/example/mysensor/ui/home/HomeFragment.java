package com.example.mysensor.ui.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;
import android.widget.TableRow;
import android.widget.TableLayout;

import com.example.mysensor.R;
import com.example.mysensor.SharedViewModel;
import com.google.android.material.chip.*;
import android.widget.GridLayout.LayoutParams;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private SharedViewModel sharedViewModel;
    private TableLayout table;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.i("SensorActivity","SharedViewModel is Initialized.");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        table = binding.tableLayout;
        sharedViewModel.increment = 0;

        //get data from dashboard
        getParentFragmentManager().setFragmentResultListener("dataFrom1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String datastr = result.getString("df1");
                binding.dataFrom1.setText(datastr);
            }
        });

        //send sensor name to dashboard
        String namestr = sharedViewModel.namestr;
        Bundle result = new Bundle();
        result.putString("df2",namestr);
        getParentFragmentManager().setFragmentResult("dataName",result);

        //send signal
        retainTable();
        setupSensor();
        setupChip();
        sendRowSignal();
        //sendTableSignal();
        setupAddButton();
        setupUpdate();
        signalupdate();
        getSensorName();

        //final TextView textView = binding.textView;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getStatus(){
        String statusstr = "";
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(3);
            String name = t.getText().toString();
            statusstr += (name+";");
        }
        return statusstr;
    }

    public void getSensorName(){
        sharedViewModel.namestr = "";
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            String name = t.getText().toString();
            sharedViewModel.namestr += (name+";");
        }
        //binding.namestr.setText(sharedViewModel.namestr);
    }

    public void setupSensor(){
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
                    String namestr = sharedViewModel.namestr;
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

    public void sendRowSignal(){
        //detectSignal

        //get data from dashboard
        int data_count = table.getChildCount();
        double[][] arr = new double[data_count][2];
        for(int i=0;i<data_count;i++){
            for(int j=0;j<2;j++){
                arr[i][j] = 0;
            }
        }
        //String datastr = binding.dataFrom1.getText().toString();
        String datastr = sharedViewModel.data;
        if (datastr == "") {
            binding.statusstr.setText(sharedViewModel.statusstr);
            return;
        }
        else{
            String[] dataarr = datastr.split("[;]", 0);  //each row data
            for(int i=0;i<dataarr.length;i++){
                String[] tmp = dataarr[i].split("[,]", 0);  //split into intensity and resistance
                arr[i][0] = Double.parseDouble(tmp[0]);
                arr[i][1] = Double.parseDouble(tmp[1]);
            }
        }

        //output result from data
        sharedViewModel.statusstr = "";
        for(int i=0;i<data_count;i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            if (true){
                Chip c = (Chip) row.getChildAt(1);
                TextView signaltxt = (TextView) row.getChildAt(3);
                if (c.isChecked()){
                    //tmp = dataarr[i].split("[,]",0);
                    if (detectSignal(arr[i][0],arr[i][1]) ){
                        signaltxt.setText("Signal detected.");
                        signaltxt.setTextColor(Color.RED);
                        signaltxt.setTypeface(null, Typeface.BOLD);
                        sharedViewModel.statusstr += "Signal detected.;";
                        sharedViewModel.detectionstr += Integer.toString(i+1)+";";
                        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                        String dateString = dateFormat.format(new Date()).toString();
                        sharedViewModel.detectiontime += dateString;
                    }
                    else{
                        signaltxt.setText("No signal.");
;                       signaltxt.setTextColor(Color.GRAY);
                        signaltxt.setTypeface(null);
                        sharedViewModel.statusstr += "No signal.;";
                    }
                }
                else{
                    signaltxt.setText("No signal.");
                    signaltxt.setTextColor(Color.GRAY);
                    signaltxt.setTypeface(null);
                    sharedViewModel.statusstr += "No signal.;";
                }
            }
        }
        binding.statusstr.setText(sharedViewModel.statusstr);
    }

    public void sendTableSignal(){
        for(int i=0;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            //sendRowSignal(row);
        }
    }

    public void setupChip(){
        for(int i=0;i<table.getChildCount();i++){
            final int num = i;
            TableRow row = (TableRow) table.getChildAt(i);
            Chip c = (Chip) row.getChildAt(1);
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c.isChecked()){
                        updateCheck();
                    }
                    else{
                       updateCheck();
                    }
                    sendRowSignal();
                }
            });
        }
    }

    public void updateCheck(){
        sharedViewModel.checkstr = "";
        for(int i=0;i<table.getChildCount();i++) {
            final int num = i;
            TableRow row = (TableRow) table.getChildAt(i);
            Chip c = (Chip) row.getChildAt(1);
            if(c.isChecked()){
                sharedViewModel.checkstr += "true;";
            }
            else{
                sharedViewModel.checkstr += "false;";
            }
        }
    }



    public void setupAddButton(){
        FloatingActionButton addbtn = binding.addbtn;
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRow();
                sharedViewModel.real_data_count++;
                sharedViewModel.increment++;
                binding.namestr.setText(Integer.toString(sharedViewModel.increment));
                setupChip();
            }
        });
    }

    public void addRow(){
        //TableLayout table = binding.tableLayout;
        int data_count = table.getChildCount();
        TableRow tr = new TableRow(table.getContext());
        tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        //sensor name
        TextView name = new TextView(table.getContext());
        String sname = "Sensor "+Integer.toString(data_count+1);
        name.setText(sname);
        name.setLayoutParams(new TableRow.LayoutParams(160,LayoutParams.MATCH_PARENT));
        name.setPadding(92,18,0,0);
        name.setTextSize(20);
        name.setTypeface(null,Typeface.BOLD);

        //chip
        Chip c = new Chip(table.getContext());
        c.setLayoutParams(new TableRow.LayoutParams(80,LayoutParams.WRAP_CONTENT));
        c.setCheckable(true);
        c.setPadding(0,0,35,0);
        c.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(table.getContext(), R.color.teal_200)));
        c.setCheckedIconVisible(true);

        //space
        Space s = new Space(table.getContext());
        s.setLayoutParams(new TableRow.LayoutParams(10,LayoutParams.WRAP_CONTENT));

        //signal status
        TextView signaltxt = new TextView(table.getContext());
        signaltxt.setText("No signal.");
        signaltxt.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

        tr.addView(name);
        tr.addView(c);
        tr.addView(s);
        tr.addView(signaltxt);
        table.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        getSensorName();

        String namestr = sharedViewModel.namestr;
        Bundle result = new Bundle();
        result.putString("df2",namestr);
        getParentFragmentManager().setFragmentResult("dataName",result);
    }

    public void setupUpdate(){
        Button upbtn = binding.updatebtn2;
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setcheck();
                sendRowSignal();
            }
        });
    }
    public void setcheck(){
        if (sharedViewModel.checkstr == ""){
            return;
        }
        int len = table.getChildCount();
        Chip[] c = new Chip[len];
        for(int i=0;i<len;i++){
            TableRow tr = (TableRow) table.getChildAt(i);
            Chip ct = (Chip) tr.getChildAt(1);
            c[i] = ct;
        }
        String[] checkarr = sharedViewModel.checkstr.split("[;]",0);
        for(int i=0;i< checkarr.length;i++){
            if(checkarr[i].equals("true")){
                c[i].setChecked(true);
            }
            else{
                c[i].setChecked(false);
            }
        }
    }

    public void signalupdate(){
        String statusstr = sharedViewModel.statusstr;
        if(statusstr == "") return;
        String[] rowstatus = statusstr.split("[;]", 0);
        for(int i=0;i<rowstatus.length;i++){
            TableRow row = (TableRow) table.getChildAt(i);
            TextView signaltxt = (TextView) row.getChildAt(3);
            if(rowstatus[i].equals("No signal.")){
                signaltxt.setText("No signal.");
                signaltxt.setTextColor(Color.GRAY);
                signaltxt.setTypeface(null);
            }
            else{
                signaltxt.setText("Signal detected.");
                signaltxt.setTextColor(Color.RED);
                signaltxt.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    public void retainTable(){
        int count = sharedViewModel.real_data_count;
        if(count == 0) return;
        else{
            for(int i=0;i<count;i++){
                addRow();
            }
        }
    }
}

