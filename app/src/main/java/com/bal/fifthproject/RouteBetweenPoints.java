package com.bal.fifthproject;

import android.graphics.Color;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RouteBetweenPoints extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final LatLng DEFAULT_LOCATION = new  LatLng(55.75, 37.60);
    private static final float DEFAULT_ZOOM = 10f;
    private static final String TAG = "GoogleMapPolygonFragment";
    private List<LatLng> pointsLocations = new ArrayList<>();
    private Polyline currentPolyline;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_between_points, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addMarkerButton = view.findViewById(R.id.btn_add_point);
        Button drawRouteButton = view.findViewById(R.id.btn_add_route);


        addMarkerButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.setOnMapClickListener(latLng -> {
                    pointsLocations.add(latLng);
                    mMap.setOnMapClickListener(null);
                    addMarker(latLng, "New Marker");
                });
                Toast.makeText(getContext(), "Tap on the map to place a marker", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "GoogleMap instance is null");
            }
        });

        drawRouteButton.setOnClickListener(v -> {
            drawRoute();
        });




    }

    private void drawRoute() {
        if(mMap != null && pointsLocations.size() > 1 ){
            LatLng origin = pointsLocations.get(0);
            LatLng destination = pointsLocations.get(1);
            String url = makeURL(origin.latitude, origin.longitude, destination.latitude, destination.longitude);
            fetchRoute(url);
            Toast.makeText(getContext(), "Route is created", Toast.LENGTH_LONG).show();
            pointsLocations.clear();
        } else  {
            Toast.makeText(getContext(), "Add at least 2 points to make a route", Toast.LENGTH_LONG).show();
        }
    }


    private void addMarker(LatLng latLng, String polygonPoint) {
        if (mMap!= null){
            mMap.addMarker(new MarkerOptions().position(latLng).title(polygonPoint));
        }
    }

    private String makeURL( double sourcelat, double sourcelog, double destlat, double destlong) {
        StringBuilder urlString = new StringBuilder();

        urlString.append("https://maps.googleapis.com/maps/api/directions/json");

        urlString.append("?origin=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));

        urlString.append("&desctination=");
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlong));

        urlString.append("&sensor=false");
        urlString.append("&mode=driving");
        urlString.append("&alternatives = true");
        urlString.append("&key=AIzaSyDXzupZiXD0-4-njfgcD0cvRNbg8RGXB7M");
        return urlString.toString();




    }

    private void fetchRoute(String url){
        executorService.execute(()->{
            List<LatLng> routePoints = new ArrayList<>();

            try {
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null){
                    json.append(line);

                }
                reader.close();
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray routes = jsonObject.getJSONArray("route");
                if(routes.length() > 0){
                    JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
                    JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

                    for (int i = 0; i < steps.length(); i++){
                        JSONObject step = legs.getJSONObject(i);
                        JSONObject startLocation = step.getJSONObject("start_location");


                    }
                }
            }

            catch (Exception e){

            }

        });

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