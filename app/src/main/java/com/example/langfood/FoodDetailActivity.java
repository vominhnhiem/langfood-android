package com.example.langfood;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.langfood.models.Product;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood, btnBack;
    private TextView txtFoodName, txtFoodPrice, txtFoodDescription;
    private Button btnAddToCart;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        initViews();
        displayProductDetails();
        
        btnBack.setOnClickListener(v -> finish());
        
        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                CartManager.getInstance().addToCart(currentProduct, 1);
                Toast.makeText(this, "Đã thêm " + currentProduct.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        imgFood = findViewById(R.id.imgFood);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodPrice = findViewById(R.id.txtFoodPrice);
        txtFoodDescription = findViewById(R.id.txtFoodDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);
    }

    private void displayProductDetails() {
        // Nhận dữ liệu từ Intent
        int id = getIntent().getIntExtra("PRODUCT_ID", -1);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double price = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);
        String desc = getIntent().getStringExtra("PRODUCT_DESC");
        String imagePath = getIntent().getStringExtra("PRODUCT_IMAGE");

        // Tạo đối tượng Product hiện tại
        currentProduct = new Product();
        currentProduct.setId(id);
        currentProduct.setName(name);
        currentProduct.setPrice(price);
        currentProduct.setDescription(desc);
        currentProduct.setImageUrl(imagePath);

        // Hiển thị text
        txtFoodName.setText(name);
        txtFoodPrice.setText(String.format(Locale.getDefault(), "%,.0fđ", price));
        txtFoodDescription.setText(desc);
        btnAddToCart.setText("THÊM VÀO GIỎ HÀNG - " + String.format(Locale.getDefault(), "%,.0fđ", price));

        // Load ảnh từ Server bằng Glide
        String imageUrl = "http://192.168.100.192:5289/" + imagePath;
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.lang_food_avt)
                .error(R.drawable.lang_food_avt)
                .into(imgFood);
    }
}