package com.example.mysensor;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.chip.Chip;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Item> selected = new MutableLiveData<Item>();
    public String statusstr = "";
    public static String namestr = "";
    public static int real_data_count = 0;
    public static String checkstr = "";
    public static Chip[] chips;
    public static String data = "";
    public static int increment = 0;
    public static String detectionstr = "";
    public static String detectiontime = "";
    public static int firstlaunch = 0;
    public static String intensity_constraint = "";  //default 650
    public static String r_constraint = "";          //default 6

    public SharedViewModel(){
        Log.i("SharedViewModel","ViewModel is Created.");

    }


    @Override
    protected  void onCleared(){
        super.onCleared();
        Log.i("SharedViewModel","ViewModel is Destroyed.");
    }

    public void select(Item item) {
        selected.setValue(item);
    }

    public LiveData getSelected() {
        return selected;
    }
}
