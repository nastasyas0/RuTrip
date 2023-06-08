package com.example.rutrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rutrip.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CityActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private static final String KEY_PHOTO = "photo", KEY_DESCRIPTION = "description";
    private StorageReference dbStorage, pathReference;
    private TextView tvCity, tvDescription, tvPlaces, tvSouvenirs;
    private ImageView ivCity;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        init();

        // Получение названия города из другого Activity
        Bundle arguments = getIntent().getExtras();
        if (arguments != null){
            city = arguments.get("city").toString();
            tvCity.setText(city);
        }

        // Получение данных (фото и описание) из БД для раздела Общее
        dbCities.document(city).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);
                            if (description.contains("_n")) {
                                description = description.replace("_n","\n");
                                tvDescription.setText(description);
                            }
                            else {
                                tvDescription.setText(description);
                            }

                            String pathPhoto = documentSnapshot.getString(KEY_PHOTO);
                            pathReference = dbStorage.child(pathPhoto);
                            Glide.with(getApplicationContext())
                                    .load(pathReference)
                                    .into(ivCity);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Пока нет информации об этом городе", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
                    }
                });

        // Раздел Места
        tvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlacesCityActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

//        // Раздел Сувениры
//        tvSouvenirs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CityActivity.this, SouvenirsActivity.class);
//                intent.putExtra("city", city);
//                startActivity(intent);
//            }
//        });
    }

    // Инициализация переменных
    private void init() {
        tvCity = findViewById(R.id.textViewCity);
        tvDescription = findViewById(R.id.textViewDescription);
        ivCity = findViewById(R.id.ivCity);
        tvPlaces = findViewById(R.id.textViewPlaces);
        tvSouvenirs = findViewById(R.id.textViewSouvenirs);

        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");

        dbStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://userdatabase-82d0b.appspot.com/Cities");
    }

    // Кнопка Назад
    public void comeBack(View view) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HomeFragment home_fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, home_fragment);
        ft.commit();
    }
}