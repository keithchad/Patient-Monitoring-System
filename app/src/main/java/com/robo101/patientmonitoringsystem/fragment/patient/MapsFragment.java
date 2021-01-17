package com.robo101.patientmonitoringsystem.fragment.patient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.constants.Constants;

public class MapsFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_patient, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        MaterialButton buttonFindHospital = view.findViewById(R.id.buttonFindHospitalNearby);

        buttonFindHospital.setOnClickListener(v -> Toast.makeText(getContext(), "Still in Development", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("MissingPermission")
    private final OnMapReadyCallback callback = googleMap -> {
        if (checkPermission()) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        LatLng deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(deviceLocation).title("Your Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 180));
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Could not get Location!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private boolean checkPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(requireContext(), locationPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{locationPermission}, Constants.PERMISSION_REQUEST_CODE);
            return false;
        }
    }

}