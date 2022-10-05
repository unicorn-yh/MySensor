package com.example.mysensor;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mysensor.databinding.ActivityMainBinding;
import com.example.mysensor.SharedViewModel;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private SharedViewModel sharedViewModel;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.i("MainActivity","SharedViewModel is Initialized.");


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        txt = findViewById(R.id.datanav);
        txt.setText("");
        //txt.setText(SharedViewModel.statusstr);


        //navView.listen

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*MenuItem home = findViewById(R.id.navigation_home);
        home.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                txt.setText("aaa");
                return false;
            }
        });*/
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        String str = "";
        int id = item.getItemId();
        switch(id){
            case R.id.navigation_home:
                txt.setText(getResources().getText(R.string.title_home));
                return true;
            case R.id.navigation_dashboard:
                txt.setText(getResources().getText(R.string.title_dashboard));
                return true;
            case R.id.navigation_notifications:
                txt.setText(getResources().getText(R.string.title_notifications));
                return true;

        }
        return false;
    }*/

}