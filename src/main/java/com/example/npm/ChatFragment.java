package com.example.npm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the appropriate layout based on whether it's the first time launch or not
        if (isFirstTimeLaunch()) {
            // If it's the first time launch, show the welcome layout
            return inflater.inflate(R.layout.fragment_welcome, container, false);
        } else {
            // If it's not the first time launch, show the chat history layout
            return inflater.inflate(R.layout.fragment_chat_history, container, false);
        }
    }

    // Method to check if it's the first time launch
    private boolean isFirstTimeLaunch() {
        // Get SharedPreferences instance
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Read the value of IS_FIRST_TIME_LAUNCH from SharedPreferences
        // If the value is true, it means it's the first time launch, otherwise it's not
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
