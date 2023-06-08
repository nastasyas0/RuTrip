package com.example.rutrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText edName, edEmail, edPassword;
    private DatabaseReference users;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();
    }

    // Инициализация переменных
    private void init() {
        edName = findViewById(R.id.register_name);
        edEmail = findViewById(R.id.register_email);
        edPassword = findViewById(R.id.register_password);
        users = FirebaseDatabase.getInstance().getReference("Users");
        userAuth = FirebaseAuth.getInstance();
    }

    // Кнопка Зарегистрироваться
    public void signInScreen(View view) {
        String name = edName.getText().toString().trim();
        String em = edEmail.getText().toString().trim();
        String pass = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edEmail.setError("Введите имя");
            Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(em)) {
            edEmail.setError("Введите email");
            Toast.makeText(getApplicationContext(), "Введите email", Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(em) && !android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            edEmail.setError("Неверно указан email");
            Toast.makeText(getApplicationContext(), "Неверно указан email", Toast.LENGTH_SHORT).show();
        }
        if (pass.length() < 6) {
            edPassword.setError("Введите пароль, состоящий более, чем из 5 символов");
            Toast.makeText(getApplicationContext(), "Введите пароль, состоящий более, чем из 5 символов", Toast.LENGTH_SHORT).show();
        }
        // Регистрация пользователя
        else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass)) {
            // Регистрация пользователя в Authentication
            userAuth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // Регистрация пользователя в Realtime Database
                    if (task.isSuccessful()) {
                        FirebaseUser curUser = task.getResult().getUser();
                        String idUser = curUser.getUid(); // получение id текущего пользователя

                        users.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(getApplicationContext(), "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    User newUser = new User(name, em, pass);
                                    users.child(idUser).setValue(newUser);

                                    Toast.makeText(getApplicationContext(), "Пользователь зарегистрирован успешно", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Не удалось зарегистрировать пользователя", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Не удалось зарегистрировать пользователя", Toast.LENGTH_SHORT).show();
        }
    }

    // Кнопка Назад
    public void comeBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
