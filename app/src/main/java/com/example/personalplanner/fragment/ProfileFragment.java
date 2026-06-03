package com.example.personalplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.R;
import com.example.personalplanner.activity.LoginActivity;
import com.example.personalplanner.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView txtProfileUsername;
    private Button btnLogout;
    private SessionManager sessionManager;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtProfileUsername = view.findViewById(R.id.txtProfileUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
        sessionManager = new SessionManager(requireContext());

        txtProfileUsername.setText("Tài khoản: " + sessionManager.getUsername());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
