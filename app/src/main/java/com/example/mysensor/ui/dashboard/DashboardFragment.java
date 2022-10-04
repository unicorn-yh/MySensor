package com.example.mysensor.ui.dashboard;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.mysensor.R;
import com.example.mysensor.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button updatebtn = binding.updatebtn;
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableLayout table = binding.tableLayout2;
                String datastr = getData();
                Bundle result = new Bundle();
                result.putString("df1",datastr);
                getParentFragmentManager().setFragmentResult("dataFrom1",result);

            }
        });

        //get name from home
        getParentFragmentManager().setFragmentResultListener("dataName", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String namestr = result.getString("df2");
                binding.dataName.setText(namestr);
            }
        });

        //setupData();
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
        Button upbtn = binding.updatebtn;
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namestr = binding.dataName.getText().toString();
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
                }

                //add new row
                int diff = namearr.length-data_count;
                if(diff > 0){
                    for(int j=0;j<diff;j++){
                        TableRow tr = new TableRow(table.getContext());
                        tr.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));

                        //sensor name
                        TextView name = new TextView(table.getContext());
                        name.setText(namearr[i++]);
                        name.setLayoutParams(new TableRow.LayoutParams(160, GridLayout.LayoutParams.MATCH_PARENT));
                        name.setPadding(92,30,0,0);
                        name.setTextSize(20);
                        name.setTypeface(null, Typeface.BOLD);

                        //intensity
                        EditText intensity = new EditText(table.getContext());
                        intensity.setLayoutParams(new TableRow.LayoutParams(145, GridLayout.LayoutParams.MATCH_PARENT));
                        intensity.setPadding(145,10,0,20);
                        intensity.setTextSize(20);
                        intensity.setText("0");
                        intensity.setTypeface(null, Typeface.NORMAL);

                        //resistance
                        EditText resistance = new EditText(table.getContext());
                        resistance.setLayoutParams(new TableRow.LayoutParams(145, GridLayout.LayoutParams.MATCH_PARENT));
                        resistance.setPadding(95,10,0,20);
                        resistance.setTextSize(20);
                        resistance.setText("0");
                        resistance.setTypeface(null, Typeface.NORMAL);

                        //add to row and table
                        tr.addView(name);
                        tr.addView(intensity);
                        tr.addView(resistance);
                        table.addView(tr, new TableLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                    }

                }

            }
        });
    }
}