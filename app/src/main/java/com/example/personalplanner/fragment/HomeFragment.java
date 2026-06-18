package com.example.personalplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.R;
import com.example.personalplanner.activity.CourseListActivity;
import com.example.personalplanner.data.local.DatabaseHelper;
import com.example.personalplanner.data.model.StudyStatistics;
import com.example.personalplanner.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private TextView txtTotalTasks;
    private TextView txtCompletedTasks;
    private TextView txtPendingTasks;
    private TextView txtCourseCount;
    private TextView txtStudyHours;
    private TextView txtAssignmentCount;
    private TextView txtClassCount;
    private TextView txtPartTimeCount;
    private TextView txtOverdueTasks;
    private TextView txtProgressSummary;
    private ProgressBar progressCompletion;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView txtWelcome = view.findViewById(R.id.txtWelcome);
        txtTotalTasks = view.findViewById(R.id.txtTotalTasks);
        txtCompletedTasks = view.findViewById(R.id.txtCompletedTasks);
        txtPendingTasks = view.findViewById(R.id.txtPendingTasks);
        txtCourseCount = view.findViewById(R.id.txtCourseCount);
        txtStudyHours = view.findViewById(R.id.txtStudyHours);
        txtAssignmentCount = view.findViewById(R.id.txtAssignmentCount);
        txtClassCount = view.findViewById(R.id.txtClassCount);
        txtPartTimeCount = view.findViewById(R.id.txtPartTimeCount);
        txtOverdueTasks = view.findViewById(R.id.txtOverdueTasks);
        txtProgressSummary = view.findViewById(R.id.txtProgressSummary);
        progressCompletion = view.findViewById(R.id.progressCompletion);
        MaterialButton btnManageCourses = view.findViewById(R.id.btnManageCourses);
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        txtWelcome.setText(getString(R.string.welcome_student,
                sessionManager.getUsername().isEmpty()
                        ? getString(R.string.default_user) : sessionManager.getUsername()));
        btnManageCourses.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CourseListActivity.class)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (executorService != null && !executorService.isShutdown()) loadStatistics();
    }

    private void loadStatistics() {
        executorService.execute(() -> {
            StudyStatistics stats = databaseHelper.getStudyStatistics(sessionManager.getUserId());
            mainHandler.post(() -> {
                if (!isAdded() || getView() == null) return;
                txtTotalTasks.setText(String.valueOf(stats.getTotalPlans()));
                txtCompletedTasks.setText(String.valueOf(stats.getCompletedPlans()));
                txtPendingTasks.setText(String.valueOf(stats.getPendingPlans()));
                txtCourseCount.setText(String.valueOf(stats.getCourseCount()));
                txtStudyHours.setText(String.format(Locale.getDefault(), "%.1f",
                        stats.getPlannedMinutes() / 60f));
                txtAssignmentCount.setText(String.valueOf(stats.getAssignmentCount()));
                txtClassCount.setText(String.valueOf(stats.getClassCount()));
                txtPartTimeCount.setText(String.valueOf(stats.getPartTimeCount()));
                txtOverdueTasks.setText(String.valueOf(stats.getOverduePlans()));
                progressCompletion.setProgress(stats.getCompletionPercent());
                txtProgressSummary.setText(getString(
                        R.string.study_progress_summary, stats.getCompletionPercent()));
            });
        });
    }

    @Override
    public void onDestroyView() {
        if (executorService != null) executorService.shutdownNow();
        super.onDestroyView();
    }
}
