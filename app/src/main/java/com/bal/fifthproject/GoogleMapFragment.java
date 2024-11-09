package com.bal.fifthproject;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {


    private static final LatLng DEFAULT_LOCATION = new LatLng(55.75, 37.60);
    private static final float DEFAULT_ZOOM = 10f;
    private static final String TAG = "GoogleMapFragment";

    private List<LatLng> markerLocations = new ArrayList<>();
    private GoogleMap mMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();

        Button addMarkerButton = view.findViewById(R.id.add_marker_button);
        Button drawLinesButton = view.findViewById(R.id.draw_lines_button);

        addMarkerButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.setOnMapClickListener(latLng -> {
                    markerLocations.add(latLng);
                    mMap.setOnMapClickListener(null);
                    addMarker(latLng, "New Marker");
                });
                Toast.makeText(getContext(), "Tap on the map to place a marker", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "GoogleMap instance is null");
            }
        });

        // Button to draw lines between markers
        drawLinesButton.setOnClickListener(v -> drawPolyline());
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Map not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        loadMarkersInBackground();
    }

    private void loadMarkersInBackground() {
        new Thread(() -> {
            List<LatLng> loadedMarkers = loadMarkerData();
            new Handler(Looper.getMainLooper()).post(() -> {
                for (LatLng location : loadedMarkers) {
                    addMarker(location, "Loaded Marker");
                }
            });
        }).start();
    }

    private void addMarker(LatLng location, String title) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location).title(title));
        }
    }

    private List<LatLng> loadMarkerData() {
        List<LatLng> loadedMarkers = new ArrayList<>();
        loadedMarkers.add(new LatLng(55.80, 37.70));
        loadedMarkers.add(new LatLng(55.60, 37.75));
        loadedMarkers.add(new LatLng(55.70, 37.80));
        return loadedMarkers;
    }

    private void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            addMarker(DEFAULT_LOCATION, "Default Marker");
            moveCamera(DEFAULT_LOCATION, DEFAULT_ZOOM);
        } else {
            Log.e(TAG, "GoogleMap instance is null");
        }
    }

    private void moveCamera(LatLng location, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
    }

    // Method to draw a polyline (curved lines) between all markers
    private void drawPolyline() {
        if (mMap != null && !markerLocations.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions().clickable(true);

            for (LatLng location : markerLocations) {
                polylineOptions.add(location);
            }

            mMap.addPolyline(polylineOptions);
            Toast.makeText(getContext(), "Polyline drawn between markers", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "No markers to draw a line between");
        }
    }
}
