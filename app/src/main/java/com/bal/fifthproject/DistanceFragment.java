package com.bal.fifthproject;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DistanceFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_distance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calculateDistance(view);
    }

    private void calculateDistance(View view) {
        Location loc1 = new Location("");
        loc1.setLatitude(45.0);
        loc1.setLongitude(48.0);

        Location loc2 = new Location("");
        loc1.setLatitude(50.0);
        loc1.setLongitude(52.0);

        float distanceInMeters = loc1.distanceTo(loc2);
        Log.d("tag", String.valueOf(distanceInMeters));



    }
}