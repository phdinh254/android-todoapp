package com.example.personalplanner.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.R;
import com.example.personalplanner.database.DatabaseHelper;
import com.example.personalplanner.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnChooseDate, btnChooseTime, btnSaveTask, btnCancel;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private String selectedDate = "";
    private String selectedTime = "";

    private final Calendar calendar = Calendar.getInstance();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        initObjects();
        setDefaultDateTime();
        handleEvents();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
    }

    private void setDefaultDateTime() {
        selectedDate = dateFormat.format(calendar.getTime());
        selectedTime = timeFormat.format(calendar.getTime());

        btnChooseDate.setText("Ngày: " + selectedDate);
        btnChooseTime.setText("Giờ: " + selectedTime);
    }

    private void handleEvents() {
        btnChooseDate.setOnClickListener(v -> showDatePicker());
        btnChooseTime.setOnClickListener(v -> showTimePicker());
        btnSaveTask.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    selectedDate = dateFormat.format(calendar.getTime());
                    btnChooseDate.setText("Ngày: " + selectedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    selectedTime = timeFormat.format(calendar.getTime());
                    btnChooseTime.setText("Giờ: " + selectedTime);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tiêu đề kế hoạch");
            edtTitle.requestFocus();
            return;
        }

        int userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        int defaultCategoryId = 0; // Chưa triển khai chọn danh mục

        executorService.execute(() -> {
            boolean result = databaseHelper.addTask(
                    title,
                    description,
                    selectedDate,
                    selectedTime,
                    defaultCategoryId,
                    userId
            );

            mainHandler.post(() -> {
                if (result) {
                    Toast.makeText(this, "Thêm kế hoạch thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Thêm kế hoạch thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
