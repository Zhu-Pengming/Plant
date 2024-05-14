package com.example.npm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;


public class BlogFragment extends Fragment {

    private boolean isTwoPane = false;
    private RecyclerView newsTitleRecyclerView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsTitleRecyclerView = getActivity().findViewById(R.id.newsTitleRecyclerView);
        if (getActivity().findViewById(R.id.newsContentLayout) != null) {
            isTwoPane = true;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsTitleRecyclerView.setLayoutManager(layoutManager);
        NewsAdapter adapter = new NewsAdapter(getNews(), getActivity());
        newsTitleRecyclerView.setAdapter(adapter);
    }
    private List<News> getNews() {
        List<News> newsList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            News news = new News("This is news title " + i, getRandomLengthString("This is news content" + i + "."));
            newsList.add(news);
        }
        return newsList;
    }

    private String getRandomLengthString(String str) {
        int n = new Random().nextInt(20) + 1;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

}