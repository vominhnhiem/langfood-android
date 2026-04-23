package com.example.langfood;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.langfood.api.ApiClient;
import com.example.langfood.api.ApiService;
import com.example.langfood.models.Category;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCategoryActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private static final int PICK_IMAGE_REQUEST = 100;
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private ApiService apiService;
    private View layoutEmpty;
    
    private Uri selectedImageUri;
    private ImageView ivDialogImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        initViews();
        apiService = ApiClient.getClient().create(ApiService.class);
        setupRecyclerView();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddCategory).setOnClickListener(v -> showCategoryDialog(null));

        loadCategories();
    }

    private void initViews() {
        rvCategories = findViewById(R.id.rvManageCategory);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(categoryList, this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    adapter.updateData(categoryList);
                    layoutEmpty.setVisibility(categoryList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ManageCategoryActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryDialog(Category category) {
        selectedImageUri = null; // Reset
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        
        EditText etName = view.findViewById(R.id.etCategoryName);
        EditText etDesc = view.findViewById(R.id.etCategoryDesc);
        MaterialCardView cardImage = view.findViewById(R.id.cardCategoryImage);
        ivDialogImage = view.findViewById(R.id.ivCategoryDialogImage);

        cardImage.setOnClickListener(v -> openGallery());

        if (category != null) {
            builder.setTitle("Sửa danh mục");
            etName.setText(category.getName());
            etDesc.setText(category.getDescription());
            // Glide load current image if exists (omitted for brevity, or add Glide here)
        } else {
            builder.setTitle("Thêm danh mục mới");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category == null) {
                if (selectedImageUri == null) {
                    Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
                addCategoryWithImage(name, desc);
            } else {
                // For update, you might want a separate multipart update or just regular update
                category.setName(name);
                category.setDescription(desc);
                updateCategory(category);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (ivDialogImage != null) {
                ivDialogImage.setImageURI(selectedImageUri);
            }
        }
    }

    private void addCategoryWithImage(String name, String desc) {
        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody rbDesc = RequestBody.create(MediaType.parse("text/plain"), desc);
        
        MultipartBody.Part imagePart = prepareImagePart("image");
        if (imagePart == null) return;

        apiService.addCategoryWithImage(rbName, rbDesc, imagePart).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageCategoryActivity.this, "Đã thêm danh mục!", Toast.LENGTH_SHORT).show();
                    loadCategories();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(ManageCategoryActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareImagePart(String partName) {
        try {
            File file = new File(getCacheDir(), "temp_cat.jpg");
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateCategory(Category category) {
        apiService.updateCategory(category.getId(), category).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageCategoryActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    loadCategories();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageCategoryActivity.this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Category category) {
        showCategoryDialog(category);
    }

    @Override
    public void onDeleteClick(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    apiService.deleteCategory(category.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ManageCategoryActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                loadCategories();
                            } else {
                                Toast.makeText(ManageCategoryActivity.this, "Không thể xóa danh mục đang có món ăn", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ManageCategoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
