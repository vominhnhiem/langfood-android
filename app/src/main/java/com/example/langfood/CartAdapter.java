package com.example.langfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.langfood.models.CartItem;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangeListener listener;
    private static final String BASE_URL = "http://192.168.100.192:5289/";

    public interface OnCartChangeListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvFoodName.setText(item.getProduct().getName());
        holder.tvFoodPrice.setText(String.format(Locale.getDefault(), "%,.0fđ", item.getProduct().getPrice()));
        holder.tvQuantity.setText("x" + item.getQuantity());

        Glide.with(context)
                .load(BASE_URL + item.getProduct().getImageUrl())
                .placeholder(R.drawable.lang_food_avt)
                .into(holder.ivFoodImage);

        holder.btnRemove.setOnClickListener(v -> {
            CartManager.getInstance().removeItem(item.getProduct().getId());
            notifyDataSetChanged();
            listener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage, btnRemove;
        TextView tvFoodName, tvFoodPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}