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

public class SignInActivity extends AppCompatActivity {

    private EditText edEmail, edPassword;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        init();
    }

    // Инициализация переменных
    private void init() {
        edEmail = findViewById(R.id.signin_email);
        edPassword = findViewById(R.id.signin_password);
        userAuth = FirebaseAuth.getInstance();
    }

    // Кнопка Войти
    public void homeScreen(View view) {
        String em = edEmail.getText().toString();
        String pass = edPassword.getText().toString();

        if (!TextUtils.isEmpty(em)) {
            if (!TextUtils.isEmpty(pass)) {
                userAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Вход выполнен успешно", Toast.LENGTH_SHORT).show();

                            // Проверка почты
                            if (em.contains("@mirea") || em.contains("@rutrip")) {
                                startActivity(new Intent(SignInActivity.this, RuTripActivity.class));
                            }
                            else {
                                startActivity(new Intent(SignInActivity.this, FragmentActivity.class));
                            }
                            finish();
                        }
                        else {
                            edPassword.setError("Ошибка доступа");
                            edEmail.setError("Ошибка доступа");
                            Toast.makeText(getApplicationContext(), "При входе в аккаунт произошла ошибка", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                edPassword.setError("Введите пароль");
                Toast.makeText(getApplicationContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            edEmail.setError("Введите email");
            Toast.makeText(getApplicationContext(), "Введите email", Toast.LENGTH_SHORT).show();
        }
    }

    // Кнопка Назад
    public void comeBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
