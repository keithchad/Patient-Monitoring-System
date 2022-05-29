package com.robo101.patientmonitoringsystem.alarm.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.robo101.patientmonitoringsystem.R;

import com.robo101.patientmonitoringsystem.alarm.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View root;

    DatabaseHelper databaseHelper;


    private List<HomeItem> homeItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
//                textView.setText(s);
        });
        recyclerView = root.findViewById(R.id.recycler_view_medicine);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        homeItems = new ArrayList<>();

        loadMedicines();

        ExtendedFloatingActionButton fabAddMedicine = root.findViewById(R.id.fab_add_medicine);
        fabAddMedicine.setOnClickListener(view -> {

            Toast.makeText(getContext(), "Showing Add Dialog", Toast.LENGTH_SHORT).show();
            AddDialog addMedicineDialog = new AddDialog(HomeFragment.this);
            if (getFragmentManager() != null) {
                addMedicineDialog.show(getFragmentManager(), "Add_Dialog");
            }
        });

        return root;
    }

    public void loadMedicines() {
        databaseHelper = new DatabaseHelper(getContext());
        homeItems = databaseHelper.getMedicineList();

        adapter = new HomeAdapter(homeItems, getActivity());
        recyclerView.setAdapter(adapter);
    }
}