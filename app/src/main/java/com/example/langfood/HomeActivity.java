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
    private List<Product> allProducts = new ArrayList<>(); // Giữ toàn bộ data gốc
    private List<Product> filteredList = new ArrayList<>(); // Data đang hiển thị
    private List<Category> categoryList = new ArrayList<>();
    private EditText editSearch;
    private ApiService apiService;
    private int selectedCategoryId = -1; // -1 nghĩa là xem tất cả

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
        fetchAllProducts(); // Luôn tải tất cả về để tự lọc
        setupNavigation();
        setupSearch();
    }

    private void initViews() {
        rcvProducts = findViewById(R.id.rcvProducts);
        rcvCategories = findViewById(R.id.rcvCategories);
        editSearch = findViewById(R.id.editSearch);
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductAdapter(filteredList);
        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvProducts.setAdapter(productAdapter);

        categoryAdapter = new CategoryHomeAdapter(categoryList, category -> {
            selectedCategoryId = category.getId();
            applyFilters(); // Chạy hàm lọc khi đổi category
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
                    categoryList.add(new Category(-1, "Tất cả"));
                    categoryList.addAll(response.body());
                    categoryAdapter.updateData(categoryList);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private void fetchAllProducts() {
        apiService.getProducts(null).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());
                    applyFilters(); // Hiển thị dữ liệu lần đầu
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
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
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // HÀM LỌC CHÍNH (Kết hợp cả Category và Search)
    private void applyFilters() {
        filteredList.clear();
        String searchQuery = editSearch.getText().toString().toLowerCase().trim();

        for (Product p : allProducts) {
            // 1. Kiểm tra Category
            boolean matchesCategory = (selectedCategoryId == -1) || (p.getCategoryId() == selectedCategoryId);
            
            // 2. Kiểm tra Search
            boolean matchesSearch = searchQuery.isEmpty() || 
                                   p.getName().toLowerCase().contains(searchQuery) ||
                                   (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchQuery));

            if (matchesCategory && matchesSearch) {
                filteredList.add(p);
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void setupNavigation() {
        findViewById(R.id.cardAvatar).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.btnHeaderCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.btnNavHistory).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnNavChat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllProducts(); // Cập nhật lại khi quay lại từ màn hình khác
    }
}
