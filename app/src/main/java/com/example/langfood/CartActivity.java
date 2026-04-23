package com.example.langfood;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.langfood.api.ApiClient;
import com.example.langfood.api.ApiService;
import com.example.langfood.models.CartItem;
import com.example.langfood.models.Order;
import com.example.langfood.models.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ImageView btnBack;
    private ApiService apiService;
    private String currentUserId;
    private String currentFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        apiService = ApiClient.getClient().create(ApiService.class);
        
        SharedPreferences prefs = getSharedPreferences("LangFoodPrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", "");
        currentFullName = prefs.getString("FULL_NAME", "");

        setupRecyclerView();

        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> handleCheckout());

        updateUI();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnThanhToan);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
    }

    private void updateUI() {
        double total = CartManager.getInstance().getTotalPrice();
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%,.0fđ", total));
        
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);
        } else {
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
        }
    }

    @Override
    public void onQuantityChanged() {
        updateUI();
    }

    private void handleCheckout() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) return;

        // Tạo đối tượng Order
        Order order = new Order();
        order.setBuyerId(currentUserId);
        order.setBuyerName(currentFullName); // Thêm tên người mua vào đây
        order.setTotalAmount(CartManager.getInstance().getTotalPrice());
        order.setStatus("Pending");
        
        // Chuyển CartItem sang OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProductId(item.getProduct().getId());
            oi.setQuantity(item.getQuantity());
            oi.setUnitPrice(item.getProduct().getPrice());
            orderItems.add(oi);
        }
        order.setOrderItems(orderItems);

        // Gửi lên Backend
        apiService.createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    CartManager.getInstance().clearCart();
                    finish();
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
