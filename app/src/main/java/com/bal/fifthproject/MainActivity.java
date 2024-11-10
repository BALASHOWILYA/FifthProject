package com.bal.fifthproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import org.koin.java.KoinJavaComponent;

public class MainActivity extends AppCompatActivity {

    /*
    private GoogleMapFragment googleMapFragment = (GoogleMapFragment) KoinJavaComponent.inject(GoogleMapFragment.class).getValue();
    private GoogleMapPolygonFragment googleMapPolygonFragment = (GoogleMapPolygonFragment) KoinJavaComponent.inject(GoogleMapPolygonFragment.class).getValue();
    private GoogleMapMode googleMapMode = (GoogleMapMode) KoinJavaComponent.inject(GoogleMapMode.class).getValue();
    private DistanceFragment distanceFragment = (DistanceFragment) KoinJavaComponent.inject(DistanceFragment.class).getValue();
    */
    private GoogleMapFragment googleMapFragment = new GoogleMapFragment();
    private GoogleMapPolygonFragment googleMapPolygonFragment = new GoogleMapPolygonFragment();
    private GoogleMapMode googleMapMode = new GoogleMapMode();
    private DistanceFragment distanceFragment = new DistanceFragment();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addFragment(googleMapFragment, R.id.fragment_container);
    }

    private  void addFragment(Fragment fragment, int containerId){
        if(getSupportFragmentManager().findFragmentById(containerId) == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(containerId,fragment)
                    .commit();
        } else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId,fragment)
                    .commit();
        }


    }
}