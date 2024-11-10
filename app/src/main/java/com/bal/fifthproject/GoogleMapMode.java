package com.bal.fifthproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.gms.location.LocationServices;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapMode extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final LatLng DEFAULT_LOCATION = new LatLng(60, 30);
    private static final float DEFAULT_ZOOM = 10f;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableMyLocation();
                } else {
                    Toast.makeText(getContext(), "Для отображения вашего местоположения требуется разрешение", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        initMap();

        Button terrainMode = view.findViewById(R.id.btn_map_terrain);
        Button hybridMode = view.findViewById(R.id.btn_map_hybrid);
        Button normalMode = view.findViewById(R.id.btn_map_normal);
        Button satelliteMode = view.findViewById(R.id.btn_map_satellite);

        terrainMode.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        hybridMode.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_HYBRID));
        normalMode.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_NORMAL));
        satelliteMode.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_SATELLITE));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_for_polygon);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Map not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setMapType(int mapType) {
        if (mMap != null) {
            mMap.setMapType(mapType);
        }
    }

    private void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            moveCamera(DEFAULT_LOCATION, DEFAULT_ZOOM);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        enableMyLocation();
    }

    private void moveCamera(LatLng location, float zoom) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);

            // Получение последнего известного местоположения и перемещение камеры
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));
                }
            });
        }
    }
}
