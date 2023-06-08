package com.example.rutrip;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private String city;
    private SearchView searchView;
    private List<String> citiesList;
    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private StorageReference dbStorage, pathReference;
    private ConstraintLayout constraintLayout;
    private ImageButton ivCity;
    private TextView tvCity;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        constraintLayout = view.findViewById(R.id.popular_city_1);
        setImageCity("Москва", "/moscow2.jpg");
        constraintLayout = view.findViewById(R.id.popular_city_2);
        setImageCity("Санкт-Петербург", "/saint-peterburg.jpg");
        constraintLayout = view.findViewById(R.id.popular_city_3);
        setImageCity("Казань", "/kazan.jpg");
        constraintLayout = view.findViewById(R.id.popular_city_4);
        setImageCity("Калининград", "/kalinigrad.jpeg");
        constraintLayout = view.findViewById(R.id.popular_city_5);
        setImageCity("Владивосток", "/vladivostok.jpg");
        constraintLayout = view.findViewById(R.id.popular_city_6);
        setImageCity("Екатеринбург", "/ekaterinburg.png");
        constraintLayout = view.findViewById(R.id.popular_city_7);
        setImageCity("Тула", "/tula.jpeg");
        constraintLayout = view.findViewById(R.id.popular_city_8);
        setImageCity("Йошкар-Ола", "/yoshkar ola.jpg");

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

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
            }
        });

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
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    // Инициализация переменных
    public void init() {
        citiesList = new ArrayList<>();
        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");
        dbStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://userdatabase-82d0b.appspot.com/Cities");
    }

    // Установка изображения для каждого популярного города
    public void setImageCity(String city, String name_file) {
        tvCity = constraintLayout.findViewById(R.id.tvCity);
        tvCity.setText(city);
        pathReference = dbStorage.child(city + name_file);
        ivCity = constraintLayout.findViewById(R.id.ivCity);
        Glide.with(getActivity().getApplicationContext())
                .load(pathReference)
                .into(ivCity);

        // Кнопка Изображение города
        ivCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CityActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
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