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
    private TextView btnEditProfile, btnChangePassword, btnMyAddresses, btnAddFood, btnManageFood, btnRegisterPartner, btnLogout, btnShipperManage, btnManageCategory;
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
        btnManageCategory = findViewById(R.id.btnManageCategory);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("LangFoodPrefs", MODE_PRIVATE);
        
        String fullName = prefs.getString("FULL_NAME", "Chưa cập nhật");
        String username = prefs.getString("USERNAME", "");
        String phone = prefs.getString("PHONE", "Chưa có SĐT");
        String building = prefs.getString("BUILDING", "");
        String room = prefs.getString("ROOM", "");
        int roleId = prefs.getInt("ROLE_ID", 1); // 1: Buyer, 2: Seller, 3: Shipper, 0: Admin (giả định)

        tvUserName.setText(fullName);
        tvPhone.setText("SĐT: " + phone);
        tvEmail.setText("Username: " + username);
        
        if (!building.isEmpty() && !room.isEmpty()) {
            tvBuilding.setText("Ký túc xá: Tòa " + building + " - Phòng " + room);
        } else {
            tvBuilding.setText("Ký túc xá: Chưa cập nhật");
        }

        // Kiểm tra quyền hiển thị
        if (roleId == 2) { // Seller
            tvUserRole.setText("Sinh viên - Seller");
            btnManageFood.setVisibility(View.VISIBLE);
            btnAddFood.setVisibility(View.GONE);
            btnRegisterPartner.setVisibility(View.GONE);
            btnManageCategory.setVisibility(View.GONE); // Chỉ Admin mới được quản lý category

            if (dividerManageFood != null) dividerManageFood.setVisibility(View.VISIBLE);
        } else if (roleId == 3) { // Shipper
            tvUserRole.setText("Shipper");
            btnShipperManage.setVisibility(View.VISIBLE);
            if (dividerShipperManage != null) dividerShipperManage.setVisibility(View.VISIBLE);
            btnRegisterPartner.setVisibility(View.GONE);
            btnManageCategory.setVisibility(View.GONE);
        } else {
            tvUserRole.setText("Sinh viên - Buyer");
            btnAddFood.setVisibility(View.GONE);
            btnManageFood.setVisibility(View.GONE);
            btnShipperManage.setVisibility(View.GONE);
            btnManageCategory.setVisibility(View.GONE);
        }
        
        // Nếu là Admin (role 0) thì luôn hiện Quản lý danh mục
        if (roleId == 0) {
            tvUserRole.setText("Administrator");
            btnManageCategory.setVisibility(View.VISIBLE);
            
            // Admin có thể muốn xem cả quản lý món ăn hoặc không tùy bạn, hiện tại tôi chỉ giữ category
            btnManageFood.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Nút Quản lý danh mục món ăn
        btnManageCategory.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ManageCategoryActivity.class));
        });

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

        if (btnShipperManage != null) {
            btnShipperManage.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, ShipperManageActivity.class));
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
