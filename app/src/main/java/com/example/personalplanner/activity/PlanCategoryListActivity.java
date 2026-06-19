package com.example.personalplanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.R;
import com.example.personalplanner.adapter.PlanCategoryAdapter;
import com.example.personalplanner.data.local.DatabaseHelper;
import com.example.personalplanner.data.model.PlanCategory;
import com.example.personalplanner.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanCategoryListActivity extends AppCompatActivity {
    private PlanCategoryAdapter adapter;
    private TextView txtEmptyCategories;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_category_list);

        RecyclerView recyclerCategories = findViewById(R.id.recyclerCategories);
        txtEmptyCategories = findViewById(R.id.txtEmptyCategories);
        MaterialButton btnAddCategory = findViewById(R.id.btnAddCategory);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        adapter = new PlanCategoryAdapter(this::openCategory);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerCategories.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v ->
                startActivity(new Intent(this, PlanCategoryFormActivity.class)));
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        executorService.execute(() -> {
            ArrayList<PlanCategory> categories = databaseHelper.getCategories(sessionManager.getUserId());
            runOnUiThread(() -> {
                adapter.setData(categories);
                txtEmptyCategories.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void openCategory(PlanCategory category) {
        Intent intent = new Intent(this, PlanCategoryFormActivity.class);
        intent.putExtra("category_id", category.getCategoryId());
        intent.putExtra("category_name", category.getCategoryName());
        intent.putExtra("category_code", category.getCategoryCode());
        intent.putExtra("note", category.getNote());
        intent.putExtra("color", category.getColor());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }
}
