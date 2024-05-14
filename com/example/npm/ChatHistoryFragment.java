package com.example.npm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatHistoryFragment extends Fragment {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatSessionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewChatSessions);
        adapter = new ChatSessionAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        viewModel.getChatSessions().observe(getViewLifecycleOwner(), chatSessions -> {
            adapter.setChatSessions(chatSessions);
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}
