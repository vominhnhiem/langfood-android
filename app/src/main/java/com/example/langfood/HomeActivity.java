package com.example.langfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.langfood.api.ApiClient;
import com.example.langfood.api.ApiService;
import com.example.langfood.models.Category;
import com.example.langfood.models.Product;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rcvProducts, rcvCategories;
    private ProductAdapter productAdapter;
    private CategoryHomeAdapter categoryAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private EditText editSearch;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        apiService = ApiClient.getClient().create(ApiService.class);

        CartManager.getInstance().init(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerViews();
        loadCategories();
        loadProducts(null); // Mặc định load tất cả khi mới vào
        setupNavigation();
        setupSearch();
    }

    private void initViews() {
        rcvProducts = findViewById(R.id.rcvProducts);
        rcvCategories = findViewById(R.id.rcvCategories);
        editSearch = findViewById(R.id.editSearch);
    }

    private void setupRecyclerViews() {
        // Products RecyclerView
        productAdapter = new ProductAdapter(filteredList);
        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvProducts.setAdapter(productAdapter);

        // Categories RecyclerView (Horizontal)
        categoryAdapter = new CategoryHomeAdapter(categoryList, category -> {
            // Xử lý lọc khi click vào category
            if (category.getId() == -1) {
                // Nếu là mục "Tất cả"
                loadProducts(null);
            } else {
                // Nếu là một thể loại cụ thể
                loadProducts(category.getId());
            }
            Toast.makeText(this, "Đang xem: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        rcvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    // 1. Thêm mục "Tất cả" vào đầu danh sách (ID giả là -1)
                    categoryList.add(new Category(-1, "Tất cả"));
                    
                    // 2. Thêm các danh mục từ server
                    categoryList.addAll(response.body());
                    categoryAdapter.updateData(categoryList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_ERROR", "Categories fail: " + t.getMessage());
            }
        });
    }

    private void loadProducts(Integer categoryId) {
        apiService.getProducts(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    // Sau khi nhận data mới, vẫn áp dụng filter từ ô tìm kiếm (nếu có)
                    filter(editSearch.getText().toString());
                } else {
                    Toast.makeText(HomeActivity.this, "Không thể lấy danh sách món ăn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(HomeActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            String query = text.toLowerCase().trim();
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query) || 
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(query))) {
                    filteredList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void setupNavigation() {
        findViewById(R.id.cardAvatar).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        findViewById(R.id.btnHeaderCart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        findViewById(R.id.btnNavHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        findViewById(R.id.btnNavChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
