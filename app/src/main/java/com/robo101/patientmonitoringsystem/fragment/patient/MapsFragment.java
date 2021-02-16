package com.robo101.patientmonitoringsystem.fragment.patient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.utils.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment supportMapFragment;
    private double currentLat = 0;
    private double currentLong = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_patient, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        MaterialButton buttonFindHospital = view.findViewById(R.id.buttonFindHospitalNearby);
        supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        buttonFindHospital.setOnClickListener(v -> {

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=" + currentLat + "," + currentLong +
                    "&radius=5000" +
                    "&types=hospital" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.google_maps_key);

            new PlaceTask().execute(url);
        });

        if (checkPermission()) {
            getCurrentLocation();
        }

    }

    private void getCurrentLocation() {

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {

                currentLat = location.getLatitude();
                currentLong = location.getLongitude();

                supportMapFragment.getMapAsync(googleMap -> {

                    map = googleMap;

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(currentLat, currentLong),
                            10
                    ));
                });

            } else {

                Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean checkPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(getContext(), locationPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{locationPermission}, Constants.PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

         if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {

        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream stream = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        String data = builder.toString();

        reader.close();

        return data;
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();

            List<HashMap<String, String>> mapList = null;

            JSONObject object;

            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {

            map.clear();

            for (int i = 0; i <hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);

                double lat = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lat")));
                double lng = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lng")));

                String name = hashMapList.get("name");

                LatLng latLng = new LatLng(lat, lng);

                MarkerOptions options = new MarkerOptions();

                options.position(latLng);
                options.title(name);
                map.addMarker(options);
            }
        }
    }


}