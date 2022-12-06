package com.carditectgroup.carditect.fragment.patient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.adapter.DietAdapter;
import com.carditectgroup.carditect.model.Diet;

import java.util.ArrayList;
import java.util.List;

public class DietFragment extends Fragment {

    private List<Diet> list;
    private DietAdapter dietAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        initialize(view);
        return view;
    }

    public void initialize(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshDiet);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDiet);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout.setOnRefreshListener(this::populateData);

        list = new ArrayList<>();
        dietAdapter = new DietAdapter(getContext(), list);

        recyclerView.setAdapter(dietAdapter);
        populateData();
    }

    private void populateData() {
        Diet diet = new Diet("Balanced Diet", "Contains different kinds of foods in certain quantities so that the requirement for calories, proteins, minerals and vitamins are met.", R.drawable.balanced_diet);
        list.add(diet);
        Diet diet1 = new Diet("Mediterranean Diet", "Consists of fat, saturated fats not exceeding 8 percent of calorie intake.", R.drawable.mediterrenean_diet);
        list.add(diet1);
        Diet diet2 = new Diet("Veganism", "It is a diet that involves not eating anything that involves an animal product", R.drawable.vegan_diet);
        list.add(diet2);
        Diet diet3 = new Diet("Low-Carbohydrate Diet", "Limits the amount of carbohydrates consumption.", R.drawable.lowcab_diet);
        list.add(diet3);
        Diet diet4 = new Diet("Low-Fat Diet", "It is useful in weight loss. This is a calorie-restricted diet.", R.drawable.lowfat_diet);
        list.add(diet4);
        Diet diet5 = new Diet("Diabetic Diet", "Helps in controlling blood sugar", R.drawable.diabetic_diet);
        list.add(diet5);
        Diet diet6 = new Diet("Paleo Diet", "Is a natural way of eating, one that almost abandons all intake of sugar.", R.drawable.paleo_diet);
        list.add(diet6);
    }
}