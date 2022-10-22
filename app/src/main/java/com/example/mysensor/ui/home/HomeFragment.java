package com.example.mysensor.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TableRow;
import android.widget.TableLayout;

import com.example.mysensor.R;
import com.example.mysensor.SharedViewModel;
import com.google.android.material.chip.*;
import android.widget.GridLayout.LayoutParams;
import android.content.res.ColorStateList;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
//import com.example.mysensor.ui.dashboard.DashboardFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.mysensor.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedViewModel sharedViewModel;
    private TableLayout table;
    private int trigger = 0;
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

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
        if(sharedViewModel.firstlaunch == 0){
            loadData();
            sharedViewModel.firstlaunch++;
        }

        binding.namestr.setText(Integer.toString(++sharedViewModel.firstlaunch));
        binding.namestr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.updatebtn2.performClick();
            }
        });


        //set fragment to immersive mode
        root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //perform click to button
        //View decorView = getActivity().getWindow().getDecorView();
        /*root.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                binding.updatebtn2.performClick();
            }
        });*/




        //get data from dashboard
        getParentFragmentManager().setFragmentResultListener("dataFrom1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String datastr = result.getString("df1");
                binding.dataFrom1.setText(datastr);
                binding.updatebtn2.performClick();
            }
        });

        //send sensor name to dashboard
        String namestr = sharedViewModel.namestr;
        Bundle result = new Bundle();
        result.putString("df2",namestr);
        getParentFragmentManager().setFragmentResult("dataName",result);

        //send signal
        retainTable();
        //setupSensor();
        setupChip();
        //sendTableSignal();
        setupAddButton();
        setupUpdate();
        signalupdate();
        //getSensorName();

        //final TextView textView = binding.textView;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public void saveData(){
        SharedPreferences sp = this.getActivity().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("namestr",sharedViewModel.namestr);
        editor.putString("statusstr", sharedViewModel.statusstr);
        editor.putString("data",sharedViewModel.data);
        editor.putString("detectionstr",sharedViewModel.detectionstr);
        editor.putString("detectiontime",sharedViewModel.detectiontime);
        editor.putString("checkstr",sharedViewModel.checkstr);
        editor.putInt("real_data_count",sharedViewModel.real_data_count);
        editor.putInt("increment",sharedViewModel.increment);
        editor.putString("intensity_constraint",sharedViewModel.intensity_constraint);
        editor.putString("r_constraint",sharedViewModel.r_constraint);
        editor.apply();
        //Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sp1 = this.getActivity().getApplicationContext().getSharedPreferences("MyUserPrefs",Context.MODE_PRIVATE);
        sharedViewModel.namestr = sp1.getString("namestr","");
        sharedViewModel.statusstr = sp1.getString("statusstr","");
        sharedViewModel.detectionstr = sp1.getString("detectionstr","");
        sharedViewModel.detectiontime = sp1.getString("detectiontime","");
        sharedViewModel.real_data_count = sp1.getInt("real_data_count",0);
        sharedViewModel.increment = sp1.getInt("increment",0);
        sharedViewModel.data = sp1.getString("data","");
        sharedViewModel.checkstr = sp1.getString("checkstr","");
        sharedViewModel.intensity_constraint = sp1.getString("intensity_constraint","");
        sharedViewModel.r_constraint = sp1.getString("r_constraint","");
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
                    saveData();
                }
            });
        }
    }

    public boolean detectSignal(double intensity,double RR){

        if(sharedViewModel.intensity_constraint == "" || sharedViewModel.r_constraint == ""){
            return false;
        }

        try{
            if (intensity<=Double.parseDouble(sharedViewModel.intensity_constraint) && RR>=Double.parseDouble(sharedViewModel.r_constraint)){
                return true;
            }
        }
        catch (Exception e){
            return false;
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
            //binding.statusstr.setText(sharedViewModel.statusstr);
            return;
        }
        else{
            String[] dataarr = datastr.split("[;]", 0);  //each row data
            for(int i=0;i<dataarr.length;i++){
                String[] tmp = dataarr[i].split("[,]", 0);  //split into intensity and resistance
                if(tmp[0] == "" || !isNumeric(tmp[0])){
                    arr[i][0] = 0;
                }
                else{
                    arr[i][0] = Double.parseDouble(tmp[0]);
                }
                if(tmp[1] == "" || !isNumeric(tmp[1])){
                    arr[i][1] = 0;
                }
                else{
                    arr[i][1] = Double.parseDouble(tmp[1]);
                }
            }
        }

        String beforestr = sharedViewModel.statusstr;
        String[] beforestatus = new String[data_count];
        if(sharedViewModel.statusstr != "") {
            beforestatus = sharedViewModel.statusstr.split("[;]",0);
        }

        //output result from data
        sharedViewModel.statusstr = "";
        for(int i=0;i<data_count;i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            Chip c = (Chip) row.getChildAt(1);
            TextView signaltxt = (TextView) row.getChildAt(3);
            if (c.isChecked()){
                if (detectSignal(arr[i][0],arr[i][1]) ){
                    signaltxt.setText("Signal detected.");
                    signaltxt.setTextColor(Color.RED);
                    signaltxt.setTypeface(null, Typeface.BOLD);
                    sharedViewModel.statusstr += "Signal detected.;";
                    if(beforestr == "" || beforestatus[i].equals("No signal.")){
                        sharedViewModel.detectionstr += Integer.toString(i+1)+";";
                        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                        String dateString = dateFormat.format(new Date()).toString();
                        sharedViewModel.detectiontime += dateString+";";
                    }
                }
                else{
                    signaltxt.setText("No signal.");
                    ;                       signaltxt.setTextColor(Color.GRAY);
                    signaltxt.setTypeface(null);
                    sharedViewModel.statusstr += "No signal.;";
                }
            }
            else{
                sharedViewModel.statusstr += "No signal.;";
            }
        }
        saveData();
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
                    updateCheck();
                    sendRowSignal();
                    signalupdate();
                }
            });
        }
    }

    public void updateCheck(){
        sharedViewModel.checkstr = "";
        Chip[] chips = new Chip[table.getChildCount()];
        for(int i=0;i<table.getChildCount();i++) {
            final int num = i;
            TableRow row = (TableRow) table.getChildAt(i);
            Chip c = (Chip) row.getChildAt(1);
            chips[i] = c;
            if(c.isChecked()){
                sharedViewModel.checkstr += "true;";
            }
            else{
                sharedViewModel.checkstr += "false;";
            }
        }
        saveData();
        //sharedViewModel.chips = Arrays.copyOf(chips, chips.length);
    }

    public void setupAddButton(){
        FloatingActionButton addbtn = binding.addbtn;
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRow();
                sharedViewModel.real_data_count++;
                sharedViewModel.increment++;
                binding.scroll.fullScroll(View.FOCUS_DOWN);
                setupChip();
                saveData();
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
        name.setPadding(0,18,0,0);
        name.setGravity(Gravity.CENTER);
        name.setTextSize(20);
        name.setTypeface(null,Typeface.BOLD);

        //chip
        Chip c = new Chip(table.getContext());
        c.setLayoutParams(new TableRow.LayoutParams(80,LayoutParams.WRAP_CONTENT));
        c.setCheckable(true);
        c.onSaveInstanceState();
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

