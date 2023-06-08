package com.example.rutrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class PlacesCityActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private static final String KEY_PHOTO = "photo", KEY_NAME = "name", KEY_MAIN_PHOTO = "main_photo";
    private StorageReference dbStorage, pathReference;
    private TextView tvCity, tvPlace1, tvPlace2, tvPlace3, tvPlace4, tvPlace5, tvCommon, tvSouvenirs;
    private ImageView ivCity, ivPlace1, ivPlace2, ivPlace3, ivPlace4, ivPlace5;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_city);
        init();

        // Получение названия города из другого Activity
        Bundle arguments = getIntent().getExtras();
        if (arguments != null){
            city = arguments.get("city").toString();
            tvCity.setText(city);
        }

        // Получение данных (фото города, фото и названия мест) из БД для раздела Места
        dbCities.document(city).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
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

        setImageTextPlace(tvPlace1, ivPlace1,"1");
        setImageTextPlace(tvPlace2, ivPlace2,"2");
        setImageTextPlace(tvPlace3, ivPlace3,"3");
        setImageTextPlace(tvPlace4, ivPlace4,"4");
        setImageTextPlace(tvPlace5, ivPlace5,"5");

        // Раздел Общее
        tvCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacesCityActivity.this, CityActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

//        // Раздел Сувениры
//        tvSouvenirs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PlacesCityActivity.this, SouvenirsActivity.class);
//                intent.putExtra("city", city);
//                startActivity(intent);
//            }
//        });
    }

    // Инициализация переменных
    private void init() {
        tvCity = findViewById(R.id.textViewCity);
        ivCity = findViewById(R.id.ivCity);
        tvPlace1 = findViewById(R.id.tvPlace1);
        tvPlace2 = findViewById(R.id.tvPlace2);
        tvPlace3 = findViewById(R.id.tvPlace3);
        tvPlace4 = findViewById(R.id.tvPlace4);
        tvPlace5 = findViewById(R.id.tvPlace5);
        ivPlace1 = findViewById(R.id.ivPlace1);
        ivPlace2 = findViewById(R.id.ivPlace2);
        ivPlace3 = findViewById(R.id.ivPlace3);
        ivPlace4 = findViewById(R.id.ivPlace4);
        ivPlace5 = findViewById(R.id.ivPlace5);
        tvCommon = findViewById(R.id.textViewCommon);
        tvSouvenirs = findViewById(R.id.textViewSouvenirs);

        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");

        dbStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://userdatabase-82d0b.appspot.com/Cities");
    }

    // Установка изображения и названия для каждого места в городе
    public void setImageTextPlace(TextView tvPlace, ImageView ivPlace, String num) {
        dbCities.document(city).collection("Places").document(num).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String namePlace = documentSnapshot.getString(KEY_NAME);
                            tvPlace.setText(namePlace);

                            String pathPhoto = documentSnapshot.getString(KEY_MAIN_PHOTO);
                            pathReference = dbStorage.child(pathPhoto);
                            Glide.with(getApplicationContext())
                                    .load(pathReference)
                                    .into(ivPlace);

                            // Кнопка Изображение места
                            getNamePlace(tvPlace, ivPlace, num);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Пока нет информации об этом городе", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Кнопка Изображение места
    public void getNamePlace(TextView tvPlace, ImageView ivPlace, String num) {
        Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
        intent.putExtra("city", city);
        intent.putExtra("num_place", num);
        tvPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        ivPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
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