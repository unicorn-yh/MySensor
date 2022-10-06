package com.example.mysensor.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.GravityInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.mysensor.R;
import com.example.mysensor.SharedViewModel;
import com.example.mysensor.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //loadData();

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.i("DataActivity","SharedViewModel is Initialized.");

        //send data to home
        String datastr = getData();
        Bundle result = new Bundle();
        result.putString("df1",datastr);
        getParentFragmentManager().setFragmentResult("dataFrom1",result);

        //get name from home
        getParentFragmentManager().setFragmentResultListener("dataName", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String namestr = result.getString("df2");
                binding.dataName.setText(namestr);
            }
        });

        if(sharedViewModel.data == ""){
            sharedViewModel.data = getData();
            binding.data.setText(sharedViewModel.data);
        }

        setupData();
        updateData();
        fillInData();
        updateConstraint();
        //setSensorName();

        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void saveData(){
        SharedPreferences sp = this.getActivity().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("data",sharedViewModel.data);
        editor.putInt("real_data_count",sharedViewModel.real_data_count);
        editor.putInt("increment",sharedViewModel.increment);
        editor.putString("intensity_constraint",sharedViewModel.intensity_constraint);
        editor.putString("r_constraint",sharedViewModel.r_constraint);
        editor.apply();
        //Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sp1 = this.getActivity().getApplicationContext().getSharedPreferences("MyUserPrefs",Context.MODE_PRIVATE);
        sharedViewModel.real_data_count = sp1.getInt("real_data_count",0);
        sharedViewModel.increment = sp1.getInt("increment",0);
        sharedViewModel.data = sp1.getString("data","");
    }

    public void setSensorName(){
        String namestr = binding.dataName.getText().toString();
        //binding.textView12.setText(namestr);
        if(namestr == "") {
            return;
        }
        String[] namearr = namestr.split("[;]", 0);
        TableLayout table = binding.tableLayout2;
        for(int i=1;i<table.getChildCount();i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            t.setText(namearr[i]);
        }
    }

    public String getData(){
        TableLayout table = binding.tableLayout2;
        int data_count = table.getChildCount()-1;
        String datastr = "";
        //double[][] data_arr = new double[data_count][2];
        for(int i=0;i<=data_count;i++) {
            if(i==0) continue;
            TableRow row = (TableRow) table.getChildAt(i);
            String intensity = ((TextView) row.getChildAt(1)).getText().toString();
            String resistance = ((TextView) row.getChildAt(2)).getText().toString();
            if (intensity == ""){
                intensity = "0";
            }
            if (resistance == ""){
                resistance= "0";
            }
            datastr += (intensity+","+resistance+";");
        }
        return datastr;
    }

    public void setupData(){
       /* String namestr = binding.dataName.getText().toString();
        if(namestr == "") {
            binding.textView12.setText("*");
            return;
        }
        String[] namearr = namestr.split("[;]", 0);
        TableLayout table = binding.tableLayout2;
        int data_count = table.getChildCount()-1;
        int i = 1;
        //renew sensor name
        for(i=1;i<data_count;i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            t.setText(namearr[i-1]);
        }*/

        EditText incons = binding.intensityConstraint;
        EditText rcons = binding.rConstraint;
        if(sharedViewModel.intensity_constraint != ""){
            incons.setText(sharedViewModel.intensity_constraint);
        }
        else{
            sharedViewModel.intensity_constraint = incons.getText().toString();
        }
        if(sharedViewModel.r_constraint != ""){
            rcons.setText(sharedViewModel.r_constraint);
        }
        else{
            sharedViewModel.r_constraint = rcons.getText().toString();
        }


        int add_data_count = sharedViewModel.real_data_count;
        if(add_data_count == 0) return;

        //add new row
        else{
            TableLayout table = binding.tableLayout2;
            int beforecount = table.getChildCount();
            for(int j=0;j<add_data_count;j++){
                TableRow tr = new TableRow(table.getContext());
                tr.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));

                //sensor name
                TextView name = new TextView(table.getContext());
                name.setText("Sensor "+Integer.toString(beforecount+j));
                name.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                name.setPadding(92,30,0,0);
                name.setTextSize(16);
                name.setTypeface(null, Typeface.BOLD);

                //intensity
                EditText intensity = new EditText(table.getContext());
                intensity.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                intensity.setPadding(0,10,0,20);
                intensity.setTextSize(16);
                intensity.setText("0");
                intensity.setGravity(Gravity.CENTER);
                intensity.setTypeface(null, Typeface.NORMAL);

                //resistance
                EditText resistance = new EditText(table.getContext());
                resistance.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                resistance.setPadding(0,10,0,20);
                resistance.setTextSize(16);
                resistance.setText("0");
                resistance.setGravity(Gravity.CENTER);
                resistance.setTypeface(null, Typeface.NORMAL);

                //add to row and table
                tr.addView(name);
                tr.addView(intensity);
                tr.addView(resistance);
                table.addView(tr, new TableLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
            }

            for(int i=0;i<sharedViewModel.increment;i++){
                sharedViewModel.data += "0,0;";
            }

        }
        //saveData();
        //binding.data.setText(sharedViewModel.data);
    }

    /*public void fillZero(){
        TableLayout table = binding.tableLayout2;
        for(int i=1;i<table.getChildCount();i++){
            TableRow tr = (TableRow) table.getChildAt(i);
            TextView intensity = (TextView) tr.getChildAt(1);
            TextView resistance = (TextView) tr.getChildAt(2);
            if(intensity.getText().toString() == ""){
                intensity.setText("0");
            }
            if(resistance.getText().toString() == ""){
                resistance.setText("0");
            }
        }
    }*/

    public void updateData(){
        TableLayout table = binding.tableLayout2;
        int len = table.getChildCount();

        for(int i=1;i<len;i++){
            TableRow tr = (TableRow) table.getChildAt(i);
            TextView tv1 = (TextView) tr.getChildAt(1);
            TextView tv2 = (TextView) tr.getChildAt(2);

            //actionlistener for intensity
            tv1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sharedViewModel.data = getData();
                    saveData();
                    //binding.data.setText(sharedViewModel.data);
                }
            });

            //actionlister for resistance
            tv2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sharedViewModel.data = getData();
                    saveData();
                    //binding.data.setText(sharedViewModel.data);
                    //send data to sensor

                }
            });
        }
    }

    public void fillInData(){
        TableLayout table = binding.tableLayout2;
        String[][] arr = new String[table.getChildCount()-1][2];

        //get data
        String[] dataarr = sharedViewModel.data.split("[;]", 0);  //each row data
        for(int i=0;i<dataarr.length;i++){
            String[] tmp = dataarr[i].split("[,]", 0);  //split into intensity and resistance
            arr[i][0] = tmp[0];
            arr[i][1] = tmp[1];
        }

        //insert data into table
        for(int i=1;i<table.getChildCount();i++){
            TableRow tr = (TableRow) table.getChildAt(i);
            TextView intensity = (TextView) tr.getChildAt(1);
            TextView resistance = (TextView) tr.getChildAt(2);
            intensity.setText(arr[i-1][0]);
            resistance.setText(arr[i-1][1]);
        }
    }

    public void updateConstraint(){
        EditText intensityConstraint = binding.intensityConstraint;
        EditText rConstraint = binding.rConstraint;

        //intensity constraint listener
        intensityConstraint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sharedViewModel.intensity_constraint = intensityConstraint.getText().toString();
                saveData();
            }
        });

        //resistance constraint listener
        rConstraint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sharedViewModel.r_constraint = rConstraint.getText().toString();
                saveData();
            }
        });
    }

}