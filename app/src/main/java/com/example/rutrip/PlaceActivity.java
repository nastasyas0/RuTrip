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

public class PlaceActivity extends AppCompatActivity {

    private FirebaseFirestore dbStore;
    private CollectionReference dbCities;
    private StorageReference dbStorage, pathReference;
    private static final String KEY_PHOTO = "photo", KEY_NAME = "name", KEY_DESCRIPTION = "description", KEY_ADDRESS = "address";
    private static final String KEY_PHOTO_1 = "photo1", KEY_PHOTO_2 = "photo2", KEY_PHOTO_3 = "photo3";
    private TextView tvCity, tvPlace, tvDescription, tvAddress;
    private String city, numPlace;
    private ImageView ivCity, ivPhoto1, ivPhoto2, ivPhoto3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        init();

        // Получение названия города из другого Activity
        Bundle arguments = getIntent().getExtras();
        if (arguments != null){
            city = arguments.get("city").toString();
            tvCity.setText(city);
            numPlace = arguments.get("num_place").toString();
        }

        // Получение данных (фото города, название, фото, описание и адрес места) из БД для выбранного места
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

        setImageTextPlace();
    }

    // Инициализация переменных
    private void init() {
        tvCity = findViewById(R.id.textViewCity);
        ivCity = findViewById(R.id.ivCity);
        tvPlace = findViewById(R.id.textViewPlace);
        tvDescription = findViewById(R.id.textViewDescription);
        tvAddress = findViewById(R.id.textViewAddress);
        ivPhoto1 = findViewById(R.id.photo1);
        ivPhoto2 = findViewById(R.id.photo2);
        ivPhoto3 = findViewById(R.id.photo3);

        dbStore = FirebaseFirestore.getInstance();
        dbCities = dbStore.collection("Cities");

        dbStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://userdatabase-82d0b.appspot.com/Cities");
    }

    // Установка названия, изображений, описания и адреса места
    public void setImageTextPlace() {
        dbCities.document(city).collection("Places").document(numPlace).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String namePlace = documentSnapshot.getString(KEY_NAME);
                            tvPlace.setText(namePlace);  // название места

                            String discPlace = documentSnapshot.getString(KEY_DESCRIPTION);
                            tvDescription.setText(discPlace);  // описание места

                            String adPlace = documentSnapshot.getString(KEY_ADDRESS);
                            tvAddress.setText(adPlace);  // адрес места

                            String pathPhoto = documentSnapshot.getString(KEY_PHOTO_1);
                            getPhotos(ivPhoto1, pathPhoto);  // 1 фото места
                            pathPhoto = documentSnapshot.getString(KEY_PHOTO_2);
                            getPhotos(ivPhoto2, pathPhoto);  // 2 фото места
                            pathPhoto = documentSnapshot.getString(KEY_PHOTO_3);
                            getPhotos(ivPhoto3, pathPhoto);  // 3 фото места
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Пока нет информации об этом месте", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Получение изображений места
    public void getPhotos(ImageView ivPhoto, String pathPhoto) {
        pathReference = dbStorage.child(pathPhoto);
        Glide.with(getApplicationContext())
                .load(pathReference)
                .into(ivPhoto);
    }

    // Кнопка Назад
    public void comeBack(View view) {
        startActivity(new Intent(PlaceActivity.this, PlacesCityActivity.class));
    }
}