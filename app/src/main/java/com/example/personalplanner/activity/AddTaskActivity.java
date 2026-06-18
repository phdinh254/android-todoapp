package com.example.personalplanner.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.R;
import com.example.personalplanner.data.local.DatabaseHelper;
import com.example.personalplanner.data.model.PlanCategory;
import com.example.personalplanner.notification.ReminderScheduler;
import com.example.personalplanner.utils.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTaskActivity extends AppCompatActivity {
    private EditText edtTitle;
    private EditText edtDescription;
    private EditText edtDuration;
    private EditText edtLocation;
    private EditText edtRoom;
    private EditText edtSubject;
    private EditText edtRepeatUntil;
    private EditText edtWage;
    private Button btnChooseDate;
    private Button btnChooseTime;
    private Button btnChooseEndTime;
    private Button btnSaveTask;
    private Spinner spinnerCategory;
    private Spinner spinnerPlanType;
    private Spinner spinnerPriority;
    private Spinner spinnerRepeatRule;
    private Spinner spinnerReminderLead;
    private SwitchMaterial switchReminder;
    private CheckBox chkSubmitted;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private final ArrayList<PlanCategory> categories = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String selectedDate;
    private String selectedTime;
    private String selectedEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtDuration = findViewById(R.id.edtDuration);
        edtLocation = findViewById(R.id.edtLocation);
        edtRoom = findViewById(R.id.edtRoom);
        edtSubject = findViewById(R.id.edtSubject);
        edtRepeatUntil = findViewById(R.id.edtRepeatUntil);
        edtWage = findViewById(R.id.edtWage);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnChooseEndTime = findViewById(R.id.btnChooseEndTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        spinnerCategory = findViewById(R.id.spinnerCourse);
        spinnerPlanType = findViewById(R.id.spinnerPlanType);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerRepeatRule = findViewById(R.id.spinnerRepeatRule);
        spinnerReminderLead = findViewById(R.id.spinnerReminderLead);
        switchReminder = findViewById(R.id.switchReminder);
        chkSubmitted = findViewById(R.id.chkSubmitted);
        Button btnManageCategories = findViewById(R.id.btnManageCourses);
        Button btnCancel = findViewById(R.id.btnCancel);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        bindSpinner(spinnerPriority, R.array.priority_names);
        bindSpinner(spinnerPlanType, R.array.plan_type_names);
        bindSpinner(spinnerRepeatRule, R.array.repeat_rule_names);
        bindSpinner(spinnerReminderLead, R.array.reminder_lead_names);
        spinnerPriority.setSelection(1);

        selectedDate = dateFormat.format(calendar.getTime());
        selectedTime = timeFormat.format(calendar.getTime());
        endCalendar.setTime(calendar.getTime());
        endCalendar.add(Calendar.MINUTE, 60);
        selectedEndTime = timeFormat.format(endCalendar.getTime());
        updateDateTimeLabels();

        btnChooseDate.setOnClickListener(v -> showDatePicker());
        btnChooseTime.setOnClickListener(v -> showTimePicker(calendar, true));
        btnChooseEndTime.setOnClickListener(v -> showTimePicker(endCalendar, false));
        btnSaveTask.setOnClickListener(v -> savePlan());
        btnManageCategories.setOnClickListener(v ->
                startActivity(new Intent(this, CourseListActivity.class)));
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void bindSpinner(Spinner spinner, int arrayRes) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayRes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadCategories() {
        executorService.execute(() -> {
            ArrayList<PlanCategory> result = databaseHelper.getCategories(sessionManager.getUserId());
            result.add(0, new PlanCategory(0, getString(R.string.uncategorized_course),
                    "", "", "#607D8B", sessionManager.getUserId()));
            runOnUiThread(() -> {
                categories.clear();
                categories.addAll(result);
                ArrayAdapter<PlanCategory> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            });
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            selectedDate = dateFormat.format(calendar.getTime());
            updateDateTimeLabels();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(Calendar target, boolean startTime) {
        new TimePickerDialog(this, (view, hour, minute) -> {
            target.set(Calendar.HOUR_OF_DAY, hour);
            target.set(Calendar.MINUTE, minute);
            if (startTime) {
                selectedTime = timeFormat.format(target.getTime());
            } else {
                selectedEndTime = timeFormat.format(target.getTime());
            }
            updateDateTimeLabels();
        }, target.get(Calendar.HOUR_OF_DAY), target.get(Calendar.MINUTE), true).show();
    }

    private void savePlan() {
        String title = edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            edtTitle.setError(getString(R.string.error_task_title_required));
            edtTitle.requestFocus();
            return;
        }
        int duration = parseInt(edtDuration, -1);
        if (duration <= 0) {
            edtDuration.setError(getString(R.string.error_duration));
            edtDuration.requestFocus();
            return;
        }
        if (categories.isEmpty()) {
            Toast.makeText(this, R.string.course_loading, Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        PlanCategory category = categories.get(spinnerCategory.getSelectedItemPosition());
        String description = edtDescription.getText().toString().trim();
        int priority = spinnerPriority.getSelectedItemPosition();
        String planType = valueFromArray(R.array.plan_type_values, spinnerPlanType.getSelectedItemPosition());
        String repeatRule = valueFromArray(R.array.repeat_rule_values, spinnerRepeatRule.getSelectedItemPosition());
        int reminderMinutes = Integer.parseInt(valueFromArray(
                R.array.reminder_lead_values, spinnerReminderLead.getSelectedItemPosition()));
        boolean reminderEnabled = switchReminder.isChecked();
        double wage = parseDouble(edtWage);
        String repeatUntil = edtRepeatUntil.getText().toString().trim();
        ArrayList<String> dates = buildRepeatDates(selectedDate, repeatUntil, repeatRule);
        setSaving(true);
        executorService.execute(() -> {
            int saved = 0;
            long firstPlanId = -1;
            for (String date : dates) {
                if (databaseHelper.hasTimeConflict(userId, date, selectedTime, selectedEndTime, -1)) {
                    continue;
                }
                long planId = databaseHelper.addStudyPlan(
                        title, description, date, selectedTime, selectedEndTime,
                        category.getCategoryId(), planType, priority, duration,
                        reminderEnabled, reminderMinutes,
                        edtLocation.getText().toString(), edtRoom.getText().toString(),
                        edtSubject.getText().toString(), repeatRule, repeatUntil,
                        wage, chkSubmitted.isChecked(), userId);
                if (planId != -1) {
                    saved++;
                    if (firstPlanId == -1) {
                        firstPlanId = planId;
                    }
                    if (reminderEnabled) {
                        ReminderScheduler.schedule(this, (int) planId, title,
                                category.getCategoryName(), date, selectedTime, reminderMinutes);
                    }
                }
            }
            long firstId = firstPlanId;
            int savedCount = saved;
            runOnUiThread(() -> {
                if (savedCount > 0) {
                    Toast.makeText(this, R.string.task_added, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    setSaving(false);
                    Toast.makeText(this,
                            firstId == -1 ? R.string.schedule_conflict_warning : R.string.task_add_failed,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private ArrayList<String> buildRepeatDates(String startDate, String repeatUntil, String repeatRule) {
        ArrayList<String> dates = new ArrayList<>();
        dates.add(startDate);
        if ("NONE".equals(repeatRule) || repeatUntil.trim().isEmpty()) {
            return dates;
        }
        try {
            Calendar current = Calendar.getInstance();
            current.setTime(dateFormat.parse(startDate));
            Calendar until = Calendar.getInstance();
            until.setTime(dateFormat.parse(repeatUntil));
            int step = "DAILY".equals(repeatRule) ? Calendar.DAY_OF_MONTH : Calendar.WEEK_OF_YEAR;
            for (int count = 0; count < 90; count++) {
                current.add(step, 1);
                if (current.after(until)) {
                    break;
                }
                dates.add(dateFormat.format(current.getTime()));
            }
        } catch (ParseException ignored) {
            return dates;
        }
        return dates;
    }

    private int parseInt(EditText editText, int fallback) {
        try {
            return Integer.parseInt(editText.getText().toString().trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private double parseDouble(EditText editText) {
        try {
            String value = editText.getText().toString().trim();
            return value.isEmpty() ? 0 : Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String valueFromArray(int arrayRes, int position) {
        String[] values = getResources().getStringArray(arrayRes);
        return position >= 0 && position < values.length ? values[position] : values[0];
    }

    private void updateDateTimeLabels() {
        btnChooseDate.setText(getString(R.string.date_value, selectedDate));
        btnChooseTime.setText(getString(R.string.time_value, selectedTime));
        btnChooseEndTime.setText(getString(R.string.end_time_value, selectedEndTime));
    }

    private void setSaving(boolean saving) {
        btnSaveTask.setEnabled(!saving);
        btnSaveTask.setText(saving ? R.string.saving : R.string.save_study_plan);
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }
}
