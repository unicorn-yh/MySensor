package com.example.mysensor.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.example.mysensor.MyBluetoothService;
import com.example.mysensor.DeviceScanActivity;
import com.example.mysensor.R;
import com.example.mysensor.SharedViewModel;
import com.example.mysensor.databinding.FragmentDashboardBinding;
import com.example.mysensor.BluetoothLeService;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SharedViewModel sharedViewModel;
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ArrayAdapter adapter = null;
    LeDeviceListAdapter adapter2;
    ArrayList list = new ArrayList();
    ArrayList rlist = new ArrayList();
    ConnectedThread connectedThread;
    DeviceScanActivity DSA;
    BluetoothLeService BLS;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //loadData();

        root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.i("DataActivity", "SharedViewModel is Initialized.");

        //send data to home
        //String datastr = getData();
        String datastr = "";
        Bundle result = new Bundle();
        result.putString("df1", datastr);
        getParentFragmentManager().setFragmentResult("dataFrom1", result);

        //get name from home
        getParentFragmentManager().setFragmentResultListener("dataName", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String namestr = result.getString("df2");
                binding.dataName.setText(namestr);
            }
        });

        /*if (sharedViewModel.data == "") {
            sharedViewModel.data = getData();
            binding.data.setText(sharedViewModel.data);
        }*/

        //setupData();
        //updateData();
        //fillInData();
        //updateConstraint();
        //setSensorName();
        onBluetooth();
        connectBluetooth();


        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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

    public void saveData() {
        SharedPreferences sp = this.getActivity().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("data", sharedViewModel.data);
        editor.putInt("real_data_count", sharedViewModel.real_data_count);
        editor.putInt("increment", sharedViewModel.increment);
        editor.putString("intensity_constraint", sharedViewModel.intensity_constraint);
        editor.putString("r_constraint", sharedViewModel.r_constraint);
        editor.apply();
        //Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sp1 = this.getActivity().getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        sharedViewModel.real_data_count = sp1.getInt("real_data_count", 0);
        sharedViewModel.increment = sp1.getInt("increment", 0);
        sharedViewModel.data = sp1.getString("data", "");
    }

    //set sensor name
    /*public void setSensorName() {
        String namestr = binding.dataName.getText().toString();
        //binding.textView12.setText(namestr);
        if (namestr == "") {
            return;
        }
        String[] namearr = namestr.split("[;]", 0);
        TableLayout table = binding.tableLayout2;
        for (int i = 1; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView t = (TextView) row.getChildAt(0);
            t.setText(namearr[i]);
        }
    }*/

    //get data
    /*public String getData() {
        TableLayout table = binding.tableLayout2;
        int data_count = table.getChildCount() - 1;
        String datastr = "";
        //double[][] data_arr = new double[data_count][2];
        for (int i = 0; i <= data_count; i++) {
            if (i == 0) continue;
            TableRow row = (TableRow) table.getChildAt(i);
            String intensity = ((TextView) row.getChildAt(1)).getText().toString();
            String resistance = ((TextView) row.getChildAt(2)).getText().toString();
            if (intensity == "" || !isNumeric(intensity)) {
                intensity = "0";
            }
            if (resistance == "" || !isNumeric(intensity)) {
                resistance = "0";
            }
            datastr += (intensity + "," + resistance + ";");
        }
        return datastr;
    }*/

    //setup data
    /*public void setupData() {
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

        EditText incons = binding.intensityConstraint;
        EditText rcons = binding.rConstraint;
        if (sharedViewModel.intensity_constraint != "") {
            incons.setText(sharedViewModel.intensity_constraint);
        } else {
            sharedViewModel.intensity_constraint = incons.getText().toString();
        }
        if (sharedViewModel.r_constraint != "") {
            rcons.setText(sharedViewModel.r_constraint);
        } else {
            sharedViewModel.r_constraint = rcons.getText().toString();
        }


        int add_data_count = sharedViewModel.real_data_count;
        if (add_data_count == 0) return;

            //add new row
        else {
            TableLayout table = binding.tableLayout2;
            int beforecount = table.getChildCount();
            for (int j = 0; j < add_data_count; j++) {
                TableRow tr = new TableRow(table.getContext());
                tr.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));

                //sensor name
                TextView name = new TextView(table.getContext());
                name.setText("Sensor " + Integer.toString(beforecount + j));
                name.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                name.setPadding(65, 30, 0, 0);
                name.setTextSize(16);
                name.setGravity(Gravity.CENTER);
                name.setTypeface(null, Typeface.BOLD);

                //intensity
                EditText intensity = new EditText(table.getContext());
                intensity.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                intensity.setPadding(0, 10, 0, 20);
                intensity.setTextSize(16);
                intensity.setText("0");
                intensity.setInputType(InputType.TYPE_CLASS_NUMBER);
                intensity.setGravity(Gravity.CENTER);
                intensity.setTypeface(null, Typeface.NORMAL);

                //resistance
                EditText resistance = new EditText(table.getContext());
                resistance.setLayoutParams(new TableRow.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
                resistance.setPadding(0, 10, 0, 20);
                resistance.setTextSize(16);
                resistance.setText("0");
                resistance.setInputType(InputType.TYPE_CLASS_NUMBER);
                resistance.setGravity(Gravity.CENTER);
                resistance.setTypeface(null, Typeface.NORMAL);

                //add to row and table
                tr.addView(name);
                tr.addView(intensity);
                tr.addView(resistance);
                table.addView(tr, new TableLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
            }

            for (int i = 0; i < sharedViewModel.increment; i++) {
                sharedViewModel.data += "0,0;";
            }

        }
        //saveData();
        //binding.data.setText(sharedViewModel.data);
    }*/

    //fill in zero
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

    //update data
   /* public void updateData() {
        try {
            TableLayout table = binding.tableLayout2;
            int len = table.getChildCount();

            for (int i = 1; i < len; i++) {
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
        } catch (Exception e) {
            return;
        }

    }*/

    //fill in data

    /*public void fillInData() {
        try {
            TableLayout table = binding.tableLayout2;
            String[][] arr = new String[table.getChildCount() - 1][2];

            //get data
            String[] dataarr = sharedViewModel.data.split("[;]", 0);  //each row data
            for (int i = 0; i < dataarr.length; i++) {
                String[] tmp = dataarr[i].split("[,]", 0);  //split into intensity and resistance
                arr[i][0] = tmp[0];
                arr[i][1] = tmp[1];
            }

            //insert data into table
            for (int i = 1; i < table.getChildCount(); i++) {
                TableRow tr = (TableRow) table.getChildAt(i);
                TextView intensity = (TextView) tr.getChildAt(1);
                TextView resistance = (TextView) tr.getChildAt(2);
                intensity.setText(arr[i - 1][0]);
                resistance.setText(arr[i - 1][1]);
            }
        } catch (Exception e) {
            return;
        }

    }*/

    //update constraint
    /*public void updateConstraint() {
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
    }*/

    public void onBluetooth() {
        BA = BluetoothAdapter.getDefaultAdapter();

        if (BA == null) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        Button BTbtn = binding.bluetoothBtn;

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            //Toast.makeText(getActivity().getApplicationContext(), "Turned on.", Toast.LENGTH_LONG).show();
                            BTbtn.setBackgroundColor(getResources().getColor(R.color.teal_200));
                            BTbtn.setText("BLUETOOTH TURNED ON");
                            //visible();
                            list();
                            //binding.title.setText("Device: "+getLocalBluetoothName());
                            //BluetoothSocket bs = BluetoothAdapter.LeScanCallback();

                        }
                    }
                });

        BTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String s = BTbtn.getText().toString();
                    if (s.contains("TURNED OFF") || s.contains("CONNECT")) {
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        //getActivity().startActivityForResult(turnOn, 0);
                        someActivityResultLauncher.launch(turnOn);


                    } else {
                        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            BA.disable();
                            //binding.title.setText("disable");
                            //Toast.makeText(getActivity().getApplicationContext(), "Turned off.", Toast.LENGTH_LONG).show();
                        }
                        if (true) {
                            BTbtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            BTbtn.setText("BLUETOOTH TURNED OFF");
                            list.clear();
                            adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                            binding.devicelist.setAdapter(adapter);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Connection error.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void visible() {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                        }
                    }
                });
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //startActivityForResult(getVisible, 0);
        someActivityResultLauncher.launch(getVisible);
    }


    public void list() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            pairedDevices = BA.getBondedDevices();
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName());
            }
            Toast.makeText(getActivity().getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
            adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
            binding.devicelist.setAdapter(adapter);
            /*LeDeviceListAdapter adapter2 = new LeDeviceListAdapter();
            DSA.scanLeDevice(true);
            binding.devicelist.setAdapter(adapter2);
            adapter2.clear();*/
        }

        binding.devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    BluetoothGatt gatt;
                    binding.statusText.setText("Connected");
                    Iterator<BluetoothDevice> itr = pairedDevices.iterator();
                    BluetoothDevice device = null;
                    for (int i = 0; itr.hasNext(); i++) {
                        device = itr.next();
                        if (i == position) {
                            break;
                        }
                    }
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    //binding.title.setText("");
                    binding.statusText.setText(device.getName()+" connected");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        binding.statusText.setText("Connected");
                        Toast.makeText(getActivity().getApplicationContext(), device.getName()+" connected.", Toast.LENGTH_SHORT).show();
                        rlist();
                        //gatt = device.connectGatt(getActivity().getApplicationContext(), true, BLS.mGattCallback);
                    }

                }
                catch (Exception e){
                    binding.statusText.setText("Connected");
                    Toast.makeText(getActivity().getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*binding.devicelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                binding.title.setText("0");
                    binding.title.setText("00");
                    BluetoothGatt gatt;
                    binding.title.setText("1");
                    int pos = adapter.getPosition(this);
                    binding.title.setText("2");
                    binding.statusText.setText("Connected");
                    binding.title.setText("3");
                    Iterator<BluetoothDevice> itr = pairedDevices.iterator();
                    binding.title.setText("4");
                    BluetoothDevice device = null;
                    binding.title.setText("5");
                    for (int i = 0; itr.hasNext(); i++) {
                        device = itr.next();
                        if (i == 1) {
                            break;
                        }
                    }
                    binding.title.setText("6");
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    binding.title.setText("7");
                    binding.title.setText(device.getName());
                //gatt = device.connectGatt(getActivity().getApplicationContext(), false, mGattCallback);
            }
        });*/

        /*catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void rlist(){
        rlist.add(10);
        rlist.add(6);
        //Toast.makeText(getActivity().getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, rlist);
        binding.rlist.setAdapter(adapter);
    }

    public String getLocalBluetoothName() {

        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        String name = "";
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            name = BA.getName();
        }
        if (name == null) {
            name = BA.getAddress();
        }
        return name;
    }

    public void connectBluetooth() {

    }


    private static final String TAG = "MYSENSOR";
    //private Handler handlers; // handler that gets info from Bluetooth service

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    binding.statusText.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    binding.statusText.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    binding.statusText.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    binding.statusText.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    //binding.title.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    serverSocket = BA.listenUsingL2capChannel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    connectedThread = new ConnectedThread(socket);
                    connectedThread.start();

                    break;
                }
            }
        }
    }

    class BleDefinedUUIDs {
        //Service对应的UUID
        class Service {
            final UUID BLE_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        }

        ;

        class Characteristic {
            //characteristic的UUID
//        final static public UUID HEART_RATE_MEASUREMENT   = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
            final UUID BLE_SERVICE_READ = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
            final public UUID MANUFACTURER_STRING = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
            final public UUID MODEL_NUMBER_STRING = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
            final public UUID FIRMWARE_REVISION_STRING = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
            final public UUID APPEARANCE = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");
            final public UUID BODY_SENSOR_LOCATION = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb");
        }

        class Descriptor {
            final UUID CHAR_CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            //mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RecyclerView.ViewHolder viewHolder = null;
            // General ListView optimization code.
            /*if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }*/

            BluetoothDevice device = mLeDevices.get(i);
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            final String deviceName = device.getName();
            /*if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());*/

            return view;
        }
    }
}