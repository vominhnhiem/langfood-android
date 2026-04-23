package com.example.langfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.langfood.api.ApiClient;
import com.example.langfood.api.ApiService;
import com.example.langfood.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etEmail, etPhone, etKtxBuilding, etKtxRoom, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Ánh xạ View
        initViews();

        // 2. Nút Đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());

        // 3. Link về Đăng nhập
        tvLoginLink.setOnClickListener(v -> {
            finish(); // Quay lại màn hình Login
        });
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etKtxBuilding = findViewById(R.id.etKtxBuilding);
        etKtxRoom = findViewById(R.id.etKtxRoom);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void handleRegister() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String ktxBuilding = etKtxBuilding.getText().toString().trim();
        String ktxRoom = etKtxRoom.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin cơ bản!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ktxBuilding.isEmpty() || ktxRoom.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin Ký túc xá!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo object User
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setUsername(username);
        newUser.setPhoneNumber(phone);
        newUser.setKtxBuilding(ktxBuilding);
        newUser.setKtxRoom(ktxRoom);
        newUser.setPasswordHash(pass); 
        newUser.setRoleId(1); // 1: Buyer

        // Gọi API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.register(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("REGISTER_ERROR", t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}