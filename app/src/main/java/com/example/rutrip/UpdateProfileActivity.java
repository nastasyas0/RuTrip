package com.example.rutrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rutrip.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private EditText edName, edEmail, edPassword, edCity;
    private String idUser, userEmail, userPassword, name, email, password, city;
    private Button button_save, button_cancel;
    private DatabaseReference users, dataUser;
    private FirebaseAuth userAuth;
    private int isTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        init();

        // Кнопка Сохранить
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataProfile();
            }
        });

        // Кнопка Отмена
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                AccountFragment account_fragment = new AccountFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, account_fragment);
                ft.commit();
            }
        });

        isCityInDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Установить в поля Имя, Email, Пароль и, если есть, Город данные пользователя
        ValueEventListener valEvListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                userEmail = snapshot.child("email").getValue(String.class);
                userPassword = snapshot.child("password").getValue(String.class);

                edName.setText(name);
                edEmail.setText(userEmail);
                edPassword.setText(userPassword);

                // Проверка города
                if (isCityInDB()) {
                    String city = snapshot.child("city").getValue(String.class);
                    edCity.setText(city);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        dataUser.addListenerForSingleValueEvent(valEvListener);
    }

    // Инициализация переменных
    public void init() {
        edName = findViewById(R.id.update_name);
        edEmail = findViewById(R.id.update_email);
        edPassword = findViewById(R.id.update_password);
        edCity = findViewById(R.id.update_city);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);

        userAuth = FirebaseAuth.getInstance();
        idUser = userAuth.getCurrentUser().getUid();  // id текущего пользователя
        users = FirebaseDatabase.getInstance().getReference("Users");
        dataUser = users.child(idUser);
    }

    // Обновление данных
    private void updateDataProfile() {
        name = edName.getText().toString().trim();
        email = edEmail.getText().toString().trim();
        password = edPassword.getText().toString().trim();
        city = edCity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edEmail.setError("Введите имя");
            Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Введите email");
            Toast.makeText(getApplicationContext(), "Введите email", Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Неверно указан email");
            Toast.makeText(getApplicationContext(), "Неверно указан email", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6) {
            edPassword.setError("Введите пароль, состоящий более, чем из 5 символов");
            Toast.makeText(getApplicationContext(), "Введите пароль, состоящий более, чем из 5 символов", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Изменение данных пользователя
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(email).setDisplayName(password)
                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Данные пользователя обновлены", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            if (isCityInDB()) {  // Поле Город есть в БД
                if (TextUtils.isEmpty(city)) {  // удаление поля из БД
                    dataUser.orderByChild("city").getRef().removeValue();
                }
                else {
                    users.child(idUser).child("city").setValue(city);
                }
            }
            else {  // Поле Город нет в БД
                users.child(idUser).child("city").setValue(city);

            }
            users.child(idUser).child("name").setValue(name);
            users.child(idUser).child("email").setValue(email);
            users.child(idUser).child("password").setValue(password);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            AccountFragment account_fragment = new AccountFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, account_fragment);
            ft.commit();
        }
        else {
            Toast.makeText(getApplicationContext(), "Не удалось обновить данные", Toast.LENGTH_SHORT).show();
        }
    }

    // Проверка на существование поля Город в БД
    public boolean isCityInDB() {
        isTrue = 1;  // Поле Город есть в БД
        ValueEventListener valEvListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("city").exists()) {  // если существует поле city в БД
                    isTrue = 1;
                }
                else {
                    isTrue = 0;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        dataUser.addListenerForSingleValueEvent(valEvListener);

        if (isTrue == 1) {
            return true;
        }
        else {
            return false;
        }
    }
}