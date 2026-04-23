package com.example.langfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.langfood.models.Product;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rcvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Khởi tạo CartManager để load giỏ hàng từ server ngay khi vào app
        CartManager.getInstance().init(this);

        // 1. Xử lý tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Ánh xạ và thiết lập RecyclerView
        initViews();
        setupRecyclerView();

        // 3. Load dữ liệu từ API
        loadProducts();

        // 4. Thiết lập sự kiện Click navigation
        setupNavigation();
    }

    private void initViews() {
        rcvProducts = findViewById(R.id.rcvProducts);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(productList);
        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
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
        // Load lại danh sách mỗi khi quay lại trang Home (để cập nhật món mới đăng)
        loadProducts();
    }
}
