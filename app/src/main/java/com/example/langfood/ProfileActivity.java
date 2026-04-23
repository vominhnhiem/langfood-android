package com.example.langfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserRole, tvPhone, tvEmail, tvBuilding;
    private TextView btnEditProfile, btnChangePassword, btnMyAddresses, btnAddFood, btnManageFood, btnRegisterPartner, btnLogout, btnShipperManage;
    private View dividerAddFood, dividerManageFood, dividerShipperManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Ánh xạ
        initViews();

        // 2. Load thông tin và kiểm tra quyền Seller
        loadUserInfo();

        // 3. Xử lý click
        setupClickListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvBuilding = findViewById(R.id.tvBuilding);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnMyAddresses = findViewById(R.id.btnMyAddresses);
        btnAddFood = findViewById(R.id.btnAddFood);
        dividerAddFood = findViewById(R.id.dividerAddFood);
        btnManageFood = findViewById(R.id.btnManageFood);
        dividerManageFood = findViewById(R.id.dividerManageFood);
        btnShipperManage = findViewById(R.id.btnShipperManage);
        dividerShipperManage = findViewById(R.id.dividerShipperManage);
        btnRegisterPartner = findViewById(R.id.btnRegisterPartner);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("LangFoodPrefs", MODE_PRIVATE);
        
        String fullName = prefs.getString("FULL_NAME", "Chưa cập nhật");
        String username = prefs.getString("USERNAME", "");
        String phone = prefs.getString("PHONE", "Chưa có SĐT");
        String building = prefs.getString("BUILDING", "");
        String room = prefs.getString("ROOM", "");
        int roleId = prefs.getInt("ROLE_ID", 1); // 1: Buyer, 2: Seller

        tvUserName.setText(fullName);
        tvPhone.setText("SĐT: " + phone);
        tvEmail.setText("Username: " + username);
        
        if (!building.isEmpty() && !room.isEmpty()) {
            tvBuilding.setText("Ký túc xá: Tòa " + building + " - Phòng " + room);
        } else {
            tvBuilding.setText("Ký túc xá: Chưa cập nhật");
        }

        // Kiểm tra nếu là Seller (Role 2) thì hiện nút Quản lý & Đăng đồ ăn
        if (roleId == 2) { // Seller
            tvUserRole.setText("Sinh viên - Seller");
            btnManageFood.setVisibility(View.VISIBLE); // Chỉ hiện Quản lý món ăn
            btnAddFood.setVisibility(View.GONE);       // Ẩn Đăng món ăn mới (đã có trong Quản lý)
            btnRegisterPartner.setVisibility(View.GONE); // Đã là đối tác thì ẩn nút đăng ký

            if (dividerManageFood != null) dividerManageFood.setVisibility(View.VISIBLE);
            if (dividerAddFood != null) dividerAddFood.setVisibility(View.GONE);
        } else if (roleId == 3) { // Role của Shipper
            tvUserRole.setText("Shipper");
            btnShipperManage.setVisibility(View.VISIBLE);
            if (dividerShipperManage != null) dividerShipperManage.setVisibility(View.VISIBLE);
            btnRegisterPartner.setVisibility(View.GONE);
            
            btnShipperManage.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, ShipperManageActivity.class));
            });
        } else {
            tvUserRole.setText("Sinh viên - Buyer");
            btnAddFood.setVisibility(View.GONE);
            btnManageFood.setVisibility(View.GONE);
            btnShipperManage.setVisibility(View.GONE);
            if (dividerAddFood != null) dividerAddFood.setVisibility(View.GONE);
            if (dividerManageFood != null) dividerManageFood.setVisibility(View.GONE);
            if (dividerShipperManage != null) dividerShipperManage.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Nút Quản lý món ăn
        btnManageFood.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ManageFoodActivity.class));
        });

        // Nút Đăng món ăn mới
        btnAddFood.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, AddFoodActivity.class));
        });

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng chỉnh sửa thông tin đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnMyAddresses.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng quản lý địa chỉ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        if (btnRegisterPartner != null) {
            btnRegisterPartner.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng Đăng ký Partner đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("LangFoodPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
