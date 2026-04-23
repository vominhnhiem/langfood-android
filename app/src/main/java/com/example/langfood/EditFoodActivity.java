package com.example.langfood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.bumptech.glide.Glide;
import com.example.langfood.api.ApiClient;
import com.example.langfood.api.ApiService;
import com.example.langfood.models.Category;
import com.example.langfood.models.Product;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFoodActivity extends AppCompatActivity {

    private EditText etFoodName, etFoodPrice, etFoodDescription;
    private Spinner spCategory;
    private SwitchCompat swAvailable;
    private ImageView ivFoodImage, btnBack;
    private Button btnUpdateFood;
    private ApiService apiService;
    private int productId;
    private Product currentProduct;
    private List<Category> categoryList = new ArrayList<>();
    private static final String BASE_URL = "http://192.168.100.192:5289/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        initViews();
        apiService = ApiClient.getClient().create(ApiService.class);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        
        loadCategories();

        btnBack.setOnClickListener(v -> finish());
        btnUpdateFood.setOnClickListener(v -> updateProduct());
    }

    private void initViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etFoodPrice = findViewById(R.id.etFoodPrice);
        etFoodDescription = findViewById(R.id.etFoodDescription);
        spCategory = findViewById(R.id.spCategory);
        swAvailable = findViewById(R.id.swAvailable);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        btnBack = findViewById(R.id.btnBack);
        btnUpdateFood = findViewById(R.id.btnUpdateFood);
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    setupSpinner();
                    
                    // Sau khi có danh mục mới load chi tiết món để set selection
                    if (productId != -1) {
                        loadProductDetails();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EditFoodActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void loadProductDetails() {
        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    displayProduct(currentProduct);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(EditFoodActivity.this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProduct(Product product) {
        etFoodName.setText(product.getName());
        etFoodPrice.setText(String.valueOf((int) product.getPrice()));
        etFoodDescription.setText(product.getDescription());
        swAvailable.setChecked(product.isAvailable());

        // Set selection cho Spinner dựa trên categoryId của sản phẩm
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == product.getCategoryId()) {
                spCategory.setSelection(i);
                break;
            }
        }

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/")) {
            imageUrl = BASE_URL.substring(0, BASE_URL.length() - 1) + imageUrl;
        } else {
            imageUrl = BASE_URL + imageUrl;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.lang_food_avt)
                .error(R.drawable.lang_food_avt)
                .into(ivFoodImage);
    }

    private void updateProduct() {
        String name = etFoodName.getText().toString().trim();
        String priceStr = etFoodPrice.getText().toString().trim();
        String description = etFoodDescription.getText().toString().trim();
        boolean isAvailable = swAvailable.isChecked();
        Category selectedCategory = (Category) spCategory.getSelectedItem();

        if (name.isEmpty() || priceStr.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        currentProduct.setName(name);
        currentProduct.setPrice(Double.parseDouble(priceStr));
        currentProduct.setDescription(description);
        currentProduct.setAvailable(isAvailable);
        currentProduct.setCategoryId(selectedCategory.getId());

        apiService.updateProduct(productId, currentProduct).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditFoodActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditFoodActivity.this, "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UPDATE_ERROR", t.getMessage());
                Toast.makeText(EditFoodActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
