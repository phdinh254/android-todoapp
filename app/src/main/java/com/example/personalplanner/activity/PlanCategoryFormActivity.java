package com.example.personalplanner.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.R;
import com.example.personalplanner.data.local.DatabaseHelper;
import com.example.personalplanner.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanCategoryFormActivity extends AppCompatActivity {
    private static final String[] COLORS =
            {"#1F6F68", "#3568A8", "#8A5A9E", "#C06A3D", "#3C7A57", "#A04A59"};

    private EditText edtCategoryName;
    private EditText edtCategoryCode;
    private EditText edtCategoryNote;
    private Spinner spinnerCategoryColor;
    private MaterialButton btnSaveCategory;
    private MaterialButton btnDeleteCategory;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int categoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_category_form);

        edtCategoryName = findViewById(R.id.edtCategoryName);
        edtCategoryCode = findViewById(R.id.edtCategoryCode);
        edtCategoryNote = findViewById(R.id.edtCategoryNote);
        spinnerCategoryColor = findViewById(R.id.spinnerCategoryColor);
        btnSaveCategory = findViewById(R.id.btnSaveCategory);
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_color_names,
                android.R.layout.simple_spinner_item
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryColor.setAdapter(colorAdapter);
        readCategory();

        btnSaveCategory.setOnClickListener(v -> saveCategory());
        btnDeleteCategory.setOnClickListener(v -> confirmDelete());
        btnBack.setOnClickListener(v -> finish());
    }

    private void readCategory() {
        categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId == -1) {
            btnDeleteCategory.setVisibility(View.GONE);
            return;
        }
        edtCategoryName.setText(getIntent().getStringExtra("category_name"));
        edtCategoryCode.setText(getIntent().getStringExtra("category_code"));
        edtCategoryNote.setText(getIntent().getStringExtra("note"));
        String color = getIntent().getStringExtra("color");
        for (int index = 0; index < COLORS.length; index++) {
            if (COLORS[index].equals(color)) {
                spinnerCategoryColor.setSelection(index);
                break;
            }
        }
    }

    private void saveCategory() {
        String name = edtCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            edtCategoryName.setError(getString(R.string.error_category_name_required));
            edtCategoryName.requestFocus();
            return;
        }
        btnSaveCategory.setEnabled(false);
        int userId = sessionManager.getUserId();
        String code = edtCategoryCode.getText().toString().trim();
        String note = edtCategoryNote.getText().toString().trim();
        String color = COLORS[spinnerCategoryColor.getSelectedItemPosition()];
        executorService.execute(() -> {
            boolean success = categoryId == -1
                    ? databaseHelper.addCategory(name, code, note, color, userId) != -1
                    : databaseHelper.updateCategory(categoryId, name, code, note, color, userId);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, R.string.category_saved, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    btnSaveCategory.setEnabled(true);
                    Toast.makeText(this, R.string.category_save_failed, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_category)
                .setMessage(R.string.confirm_delete_category)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteCategory())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteCategory() {
        executorService.execute(() -> {
            boolean deleted = databaseHelper.deleteCategory(categoryId, sessionManager.getUserId());
            runOnUiThread(() -> {
                Toast.makeText(this,
                        deleted ? R.string.category_deleted : R.string.category_delete_failed,
                        Toast.LENGTH_SHORT).show();
                if (deleted) {
                    finish();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }
}
