package com.bal.fifthproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class GoogleMapPolygonFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final LatLng DEFAULT_LOCATION = new  LatLng(55.75, 37.60);
    private static final float DEFAULT_ZOOM = 10f;
    private static final String TAG = "GoogleMapPolygonFragment";
    private List<LatLng> polygonPoints = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map_polygon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
        Button addPolygonPointButton =  view.findViewById(R.id.add_polygon_point_button);
        Button drawPolygonButton =  view.findViewById(R.id.draw_polygon_button);

        addPolygonPointButton.setOnClickListener(v->{
            if(mMap != null){
                mMap.setOnMapClickListener(latLng -> {
                    polygonPoints.add(latLng);
                    mMap.setOnMapClickListener(null);
                    addMarker(latLng, "Polygon Point");
                });

            } else {
                Log.e(TAG, "GoogleMap instance is null");
            }

        });

        drawPolygonButton.setOnClickListener(v->{
           drawPolygon();
        });




    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_for_polygon);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        } else {
            Context context = getContext();
            if(context != null){
                Toast.makeText(context, "Map not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void drawPolygon() {
        if(mMap != null && polygonPoints.size() > 2 ){
            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(polygonPoints)
                    .fillColor(Color.BLUE)
                    .clickable(true);


            mMap.addPolygon(polygonOptions);
            Toast.makeText(getContext(), "Polygon drawm with selected points", Toast.LENGTH_LONG).show();
            polygonPoints.clear();
        } else  {
            Toast.makeText(getContext(), "Add at least 3 points to form a polygon", Toast.LENGTH_LONG).show();
        }
    }

    private void addMarker(LatLng latLng, String polygonPoint) {
        if (mMap!= null){
            mMap.addMarker(new MarkerOptions().position(latLng).title(polygonPoint));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
    }

    private void setupMap() {
        if(mMap != null){
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);

            addMarker(DEFAULT_LOCATION, "Default location");
            moveCamera(DEFAULT_LOCATION, DEFAULT_ZOOM);
        }
    }

    private void moveCamera(LatLng defaultLocation, float defaultZoom) {
        if(mMap != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom));

        }

    }
}