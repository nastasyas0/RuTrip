package com.example.rutrip;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutrip.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private static final int REQUEST_CALL = 1;

    private FirebaseAuth userAuth;
    private DatabaseReference users, dataUser;
    private String name, email, city;
    private int isTrue;

    FragmentAccountBinding binding;

    public AccountFragment() {
        super(R.layout.fragment_account);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Кнопка Телеграм бот
        Button tgBotButton = view.findViewById(R.id.tgBotButton);
        tgBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://web.telegram.org/z/#6107680735");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        // Кнопка Поддержка
        Button supButton = view.findViewById(R.id.supButton);
        supButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        // Кнопка Выйти
        Button btn_sign_out = view.findViewById(R.id.button_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            }
        });

        // Установить данные пользователя в аккаунте
        userAuth = FirebaseAuth.getInstance();
        String idUser = userAuth.getCurrentUser().getUid();

        users = FirebaseDatabase.getInstance().getReference("Users");
        dataUser = users.child(idUser);

        ValueEventListener valEvListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);

                TextView tvName = view.findViewById(R.id.name_textView);
                TextView tvEmail = view.findViewById(R.id.email_textView);
                tvName.setText(name);
                tvEmail.setText(email);

                city = snapshot.child("city").getValue(String.class);
                if (isCityInDB()) {  // Поле Город есть в БД
                    TextView tvCity = view.findViewById(R.id.city_textView);
                    tvCity.setText(city);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        dataUser.addListenerForSingleValueEvent(valEvListener);

        // Кнопка Редактировать профиль
        ImageButton btn_update_prof = view.findViewById(R.id.update_profile);
        btn_update_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
            }
        });
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((FragmentActivity)getActivity()), new String[] {android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:89152819200"));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Отказано в доступе", Toast.LENGTH_SHORT).show();
            }
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