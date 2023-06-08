package com.example.rutrip;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private String city;
    private SearchView searchView;
    private ArrayList<String> citiesList;
    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        // Заполнение списка всех городов, которые есть в БД
        dbCities.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                citiesList.add(document.getId());
                            }
                        }
                    }
                });

        // Строка поиска
        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, citiesList);
        listView.setVisibility(View.INVISIBLE);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {  // при отправке
                city = searchView.getQuery().toString().trim();

                // Проверка на существование города
                checkCity(city);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {  // каждый раз, когда вводится какой-либо символ
                if (TextUtils.isEmpty(newText)) {
                    listView.setVisibility(View.INVISIBLE);
                }
                else {
                    listView.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        // Установка выбранного текста из ListView в SearchView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(adapter.getItem(position), false);
            }
        });
    }

    // Инициализация переменных
    public void init() {
        citiesList = new ArrayList<String>();
        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");
    }

    // Проверка на существование города
    private void checkCity(String city) {
        boolean check = false;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getApplicationContext().getAssets().open("cities_russia.txt")));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(city)) {
                    check = true;
                    if (citiesList.contains(city)) {
                        Intent intent = new Intent(getActivity(), CityActivity.class);
                        intent.putExtra("city", city);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(), "Пока нет информации об этом городе", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (!check) {
                Toast.makeText(getActivity().getApplicationContext(), "Введите корректное название города", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}