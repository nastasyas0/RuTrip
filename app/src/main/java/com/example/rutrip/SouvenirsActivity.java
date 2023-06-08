package com.example.rutrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SouvenirsActivity extends AppCompatActivity {

    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private static final String KEY_PHOTO = "photo", KEY_NAME = "name", KEY_DESCRIPTION = "description";
    private static final String KEY_PHOTO_1 = "photo1", KEY_PHOTO_2 = "photo2";
    private StorageReference dbStorage, pathReference;
    private TextView tvCity, tvCommon, tvPlaces;
    private TextView tvSouv1, tvSouv2, tvSouv3, tvDesc1, tvDesc2, tvDesc3;
    private ImageView ivCity, ivPhoto_1_1, ivPhoto_1_2, ivPhoto_2_1, ivPhoto_2_2, ivPhoto_3_1, ivPhoto_3_2;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souvenirs);
        init();

        // Получение названия города из другого Activity
        Bundle arguments = getIntent().getExtras();
        if (arguments != null){
            city = arguments.get("city").toString();
            tvCity.setText(city);
        }

        // Получение данных (фото города, фото, названия и описания сувениров) из БД для раздела Места
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

        setSouvenir(tvSouv1, tvDesc1, ivPhoto_1_1, ivPhoto_1_2, "1");
        setSouvenir(tvSouv2, tvDesc2, ivPhoto_2_1, ivPhoto_2_2, "2");
        setSouvenir(tvSouv3, tvDesc3, ivPhoto_3_1, ivPhoto_3_2, "3");

        // Раздел Общее
        tvCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SouvenirsActivity.this, CityActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        // Раздел Места
        tvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SouvenirsActivity.this, PlacesCityActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
    }

    // Инициализация переменных
    public void init() {
        tvCity = findViewById(R.id.textViewCity);
        ivCity = findViewById(R.id.ivCity);
        tvCommon = findViewById(R.id.textViewCommon);
        tvPlaces = findViewById(R.id.textViewPlaces);

        tvSouv1 = findViewById(R.id.tvNameSouvenir1);
        tvSouv2 = findViewById(R.id.tvNameSouvenir2);
        tvSouv3 = findViewById(R.id.tvNameSouvenir3);
        tvDesc1 = findViewById(R.id.textViewDescription1);
        tvDesc2 = findViewById(R.id.textViewDescription2);
        tvDesc3 = findViewById(R.id.textViewDescription3);
        ivPhoto_1_1 = findViewById(R.id.photo_1_1);
        ivPhoto_1_2 = findViewById(R.id.photo_1_2);
        ivPhoto_2_1 = findViewById(R.id.photo_2_1);
        ivPhoto_2_2 = findViewById(R.id.photo_2_2);
        ivPhoto_3_1 = findViewById(R.id.photo_3_1);
        ivPhoto_3_2 = findViewById(R.id.photo_3_2);

        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");
        dbStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://userdatabase-82d0b.appspot.com/Cities");
    }

    // Установка названия, описания и изображения для каждого сувенира
    public void setSouvenir(TextView NameSouvenir, TextView DescSouvenir, ImageView ivPhoto1, ImageView ivPhoto2, String num) {
        dbCities.document(city).collection("Souvenirs").document(num).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nameSouvenir = documentSnapshot.getString(KEY_NAME); // Название сувенира
                            NameSouvenir.setText(nameSouvenir);

                            String descSouvenir = documentSnapshot.getString(KEY_DESCRIPTION); // Описание сувенира
                            DescSouvenir.setText(descSouvenir);

                            String pathPhoto = documentSnapshot.getString(KEY_PHOTO_1);
                            getPhotos(ivPhoto1, pathPhoto);  // 1 фото
                            pathPhoto = documentSnapshot.getString(KEY_PHOTO_2);
                            getPhotos(ivPhoto2, pathPhoto);  // 2 фото
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Пока нет информации об этом городе", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Получение изображений сувенира
    public void getPhotos(ImageView ivPhoto, String pathPhoto) {
        pathReference = dbStorage.child(pathPhoto);
        Glide.with(getApplicationContext())
                .load(pathReference)
                .into(ivPhoto);
    }
}