package com.robo101.patientmonitoringsystem.fragment.patient;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robo101.patientmonitoringsystem.R;

public class MapsFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps_patient, container, false);
    }

    @SuppressLint("MissingPermission")
    private final OnMapReadyCallback callback = googleMap -> {


        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                LatLng deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(deviceLocation).title("Your Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 180));
                Log.e("latitudeA", String.valueOf(deviceLocation));
            } else{
                Log.e("latituden", "location is null");
            }
        });


    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getDeviceLocation();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void getDeviceLocation() {

    }
}