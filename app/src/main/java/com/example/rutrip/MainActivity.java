package com.example.rutrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RuTripTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_or_register_activity);
    }

    // Кнопка Зарегистрироваться
    public void registerUser(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // Кнопка Войти
    public void signInUser(View view) {
        startActivity(new Intent(this, SignInActivity.class));
    }

}