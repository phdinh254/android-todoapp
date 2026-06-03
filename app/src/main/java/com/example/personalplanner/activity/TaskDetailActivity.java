package com.example.personalplanner.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.R;
import com.example.personalplanner.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnChooseDate, btnChooseTime, btnUpdateTask, btnDeleteTask, btnBack;
    private CheckBox chkStatus;

    private DatabaseHelper databaseHelper;

    private int taskId;
    private int status;

    private String selectedDate;
    private String selectedTime;

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
        setContentView(R.layout.activity_task_detail);

        initViews();
        initObjects();
        getDataFromIntent();
        showTaskData();
        handleEvents();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);
        btnBack = findViewById(R.id.btnBack);
        chkStatus = findViewById(R.id.chkStatus);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void getDataFromIntent() {
        taskId = getIntent().getIntExtra("task_id", -1);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        selectedDate = getIntent().getStringExtra("date");
        selectedTime = getIntent().getStringExtra("time");
        status = getIntent().getIntExtra("status", 0);

        if (taskId == -1) {
            Toast.makeText(this, "Không tìm thấy kế hoạch", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtTitle.setText(title == null ? "" : title);
        edtDescription.setText(description == null ? "" : description);
    }

    private void showTaskData() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            selectedDate = dateFormat.format(calendar.getTime());
        }

        if (selectedTime == null || selectedTime.isEmpty()) {
            selectedTime = timeFormat.format(calendar.getTime());
        }

        try {
            calendar.setTime(dateFormat.parse(selectedDate));

            String[] timeParts = selectedTime.split(":");

            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
            }

        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }

        btnChooseDate.setText("Ngày: " + selectedDate);
        btnChooseTime.setText("Giờ: " + selectedTime);
        chkStatus.setChecked(status == 1);
    }

    private void handleEvents() {
        btnChooseDate.setOnClickListener(v -> showDatePicker());
        btnChooseTime.setOnClickListener(v -> showTimePicker());
        btnUpdateTask.setOnClickListener(v -> updateTask());
        btnDeleteTask.setOnClickListener(v -> confirmDeleteTask());
        btnBack.setOnClickListener(v -> finish());
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

    private void updateTask() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        int newStatus = chkStatus.isChecked() ? 1 : 0;

        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tiêu đề kế hoạch");
            edtTitle.requestFocus();
            return;
        }

        executorService.execute(() -> {
            boolean result = databaseHelper.updateTask(
                    taskId,
                    title,
                    description,
                    selectedDate,
                    selectedTime,
                    newStatus
            );

            mainHandler.post(() -> {
                if (result) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void confirmDeleteTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa kế hoạch này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> deleteTask());
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTask() {
        executorService.execute(() -> {
            boolean result = databaseHelper.deleteTask(taskId);

            mainHandler.post(() -> {
                if (result) {
                    Toast.makeText(this, "Xóa kế hoạch thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Xóa kế hoạch thất bại", Toast.LENGTH_SHORT).show();
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
