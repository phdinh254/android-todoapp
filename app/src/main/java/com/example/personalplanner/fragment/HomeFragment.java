package com.example.personalplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.R;
import com.example.personalplanner.activity.PlanCategoryListActivity;
import com.example.personalplanner.activity.TaskDetailActivity;
import com.example.personalplanner.data.local.DatabaseHelper;
import com.example.personalplanner.data.model.StudyPlan;
import com.example.personalplanner.data.model.StudyStatistics;
import com.example.personalplanner.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private TextView txtWelcome;
    private TextView txtTodaySummary;
    private TextView txtCompletion;
    private TextView txtHeroDescription;
    private LinearProgressIndicator progressCompletion;
    private TextView txtTotalPlans;
    private TextView txtAssignments;
    private TextView txtClassHours;
    private TextView txtWorkHours;
    private TextView txtOverdue;
    private TextView txtEmptyUpcoming;
    private TextView txtInsight1;
    private TextView txtInsight2;
    private TextView txtInsight3;

    private View[] planRows;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        bindViews(view);
        setupActions(view);
        renderDashboard();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            renderDashboard();
        }
    }

    private void bindViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtTodaySummary = view.findViewById(R.id.txtTodaySummary);
        txtCompletion = view.findViewById(R.id.txtCompletion);
        txtHeroDescription = view.findViewById(R.id.txtHeroDescription);
        progressCompletion = view.findViewById(R.id.progressCompletion);
        txtTotalPlans = view.findViewById(R.id.txtTotalPlans);
        txtAssignments = view.findViewById(R.id.txtAssignments);
        txtClassHours = view.findViewById(R.id.txtClassHours);
        txtWorkHours = view.findViewById(R.id.txtWorkHours);
        txtOverdue = view.findViewById(R.id.txtOverdue);
        txtEmptyUpcoming = view.findViewById(R.id.txtEmptyUpcoming);
        txtInsight1 = view.findViewById(R.id.txtInsight1);
        txtInsight2 = view.findViewById(R.id.txtInsight2);
        txtInsight3 = view.findViewById(R.id.txtInsight3);
        planRows = new View[]{
                view.findViewById(R.id.rowPlan1),
                view.findViewById(R.id.rowPlan2),
                view.findViewById(R.id.rowPlan3)
        };
    }

    private void setupActions(View view) {
        view.findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PlanCategoryListActivity.class)));
        view.findViewById(R.id.btnSearch).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new TaskFragment())
                        .commit());
        MaterialButton btnViewPlans = view.findViewById(R.id.btnViewPlans);
        btnViewPlans.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new TaskFragment())
                        .commit());
    }

    private void renderDashboard() {
        int userId = sessionManager.getUserId();
        String username = sessionManager.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = "bạn";
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        StudyStatistics statistics = databaseHelper.getStudyStatistics(userId);
        ArrayList<StudyPlan> allPlans = databaseHelper.getStudyPlans(
                userId,
                "",
                DatabaseHelper.FILTER_ALL,
                0
        );
        ArrayList<StudyPlan> upcomingPlans = databaseHelper.getUpcomingPlans(userId, today, 3);

        int classMinutes = sumMinutesByType(allPlans, StudyPlan.TYPE_CLASS);
        int workMinutes = sumMinutesByType(allPlans, StudyPlan.TYPE_PART_TIME);

        txtWelcome.setText("Chào mừng trở lại, " + username);
        txtTodaySummary.setText("Theo dõi bài tập, buổi học, ca làm thêm và kế hoạch cá nhân trong một nơi.");

        int percent = statistics.getCompletionPercent();
        txtCompletion.setText(percent + "% hoàn thành");
        txtHeroDescription.setText(statistics.getCompletedPlans() + " / "
                + statistics.getTotalPlans() + " kế hoạch đã xong. Còn "
                + statistics.getPendingPlans() + " việc cần theo dõi.");
        progressCompletion.setProgressCompat(percent, true);

        txtTotalPlans.setText(String.valueOf(statistics.getTotalPlans()));
        txtAssignments.setText(String.valueOf(statistics.getAssignmentCount()));
        txtClassHours.setText(formatHours(classMinutes));
        txtWorkHours.setText(formatHours(workMinutes));
        txtOverdue.setText(statistics.getOverduePlans() + " quá hạn");

        bindUpcomingPlans(upcomingPlans);

        txtInsight1.setText("• " + statistics.getClassCount() + " buổi học, "
                + statistics.getAssignmentCount() + " bài tập và "
                + statistics.getPartTimeCount() + " ca làm thêm đã được ghi nhận.");
        txtInsight2.setText("• Tổng thời lượng dự kiến: "
                + formatHours(statistics.getPlannedMinutes()) + " giờ.");
        txtInsight3.setText("• Bạn có " + statistics.getCourseCount()
                + " nhóm kế hoạch để phân loại học tập, công việc và cá nhân.");
    }

    private void bindUpcomingPlans(ArrayList<StudyPlan> plans) {
        txtEmptyUpcoming.setVisibility(plans.isEmpty() ? View.VISIBLE : View.GONE);
        for (int i = 0; i < planRows.length; i++) {
            View row = planRows[i];
            if (i >= plans.size()) {
                row.setVisibility(View.GONE);
                continue;
            }
            StudyPlan plan = plans.get(i);
            row.setVisibility(View.VISIBLE);
            TextView title = row.findViewById(R.id.txtPlanTitle);
            TextView meta = row.findViewById(R.id.txtPlanMeta);
            TextView type = row.findViewById(R.id.txtPlanType);
            title.setText(plan.getTitle());
            meta.setText(buildPlanMeta(plan));
            type.setText(labelForType(plan.getPlanType()));
            row.setOnClickListener(v -> openPlanDetail(plan));
        }
    }

    private String buildPlanMeta(StudyPlan plan) {
        String category = plan.getCategoryName();
        if (category == null || category.trim().isEmpty()) {
            category = "Chưa phân nhóm";
        }
        return plan.getDate() + " • " + plan.getTime() + " • " + category;
    }

    private int sumMinutesByType(ArrayList<StudyPlan> plans, String type) {
        int minutes = 0;
        for (StudyPlan plan : plans) {
            if (type.equals(plan.getPlanType())
                    && plan.getStatus() != StudyPlan.STATUS_CANCELLED) {
                minutes += plan.getDurationMinutes();
            }
        }
        return minutes;
    }

    private String formatHours(int minutes) {
        if (minutes <= 0) {
            return "0";
        }
        if (minutes % 60 == 0) {
            return String.valueOf(minutes / 60);
        }
        return String.format(Locale.US, "%.1f", minutes / 60f);
    }

    private String labelForType(String type) {
        if (StudyPlan.TYPE_ASSIGNMENT.equals(type)) {
            return "Bài tập";
        }
        if (StudyPlan.TYPE_CLASS.equals(type)) {
            return "Đi học";
        }
        if (StudyPlan.TYPE_PART_TIME.equals(type)) {
            return "Làm thêm";
        }
        if (StudyPlan.TYPE_EXAM.equals(type)) {
            return "Thi";
        }
        if (StudyPlan.TYPE_PROJECT.equals(type)) {
            return "Dự án";
        }
        return "Cá nhân";
    }

    private void openPlanDetail(StudyPlan plan) {
        Intent intent = new Intent(requireContext(), TaskDetailActivity.class);
        intent.putExtra("plan_id", plan.getPlanId());
        intent.putExtra("title", plan.getTitle());
        intent.putExtra("description", plan.getDescription());
        intent.putExtra("date", plan.getDate());
        intent.putExtra("time", plan.getTime());
        intent.putExtra("status", plan.getStatus());
        intent.putExtra("category_id", plan.getCategoryId());
        intent.putExtra("plan_type", plan.getPlanType());
        intent.putExtra("end_time", plan.getEndTime());
        intent.putExtra("location", plan.getLocation());
        intent.putExtra("room", plan.getRoom());
        intent.putExtra("subject", plan.getSubject());
        intent.putExtra("repeat_rule", plan.getRepeatRule());
        intent.putExtra("repeat_until", plan.getRepeatUntil());
        intent.putExtra("reminder_minutes", plan.getReminderMinutes());
        intent.putExtra("wage", plan.getWage());
        intent.putExtra("submitted", plan.isSubmitted());
        intent.putExtra("priority", plan.getPriority());
        intent.putExtra("duration", plan.getDurationMinutes());
        intent.putExtra("reminder_enabled", plan.isReminderEnabled());
        startActivity(intent);
    }
}
