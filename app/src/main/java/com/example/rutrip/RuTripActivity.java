package com.example.rutrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RuTripActivity extends AppCompatActivity {

    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private ArrayList<String> citiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ru_trip);
        init();

        dbCities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        citiesList.add(document.getId());
                    }
                    ListView listView = findViewById(R.id.lvCities);
                    listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, citiesList));
                }
            }
        });
    }

    // Инициализация переменных
    public void init() {
        citiesList = new ArrayList<>();
        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");
    }
}