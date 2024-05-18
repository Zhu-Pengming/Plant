package com.example.npm;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BlogContentFragment extends Fragment {

    private TextView titleView;
    private TextView contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_content, container, false);

        titleView = view.findViewById(R.id.BlogTitle);
        contentView = view.findViewById(R.id.BlogContent);

        // Assuming data is passed via arguments
        Bundle args = getArguments();
        if (args != null) {
            titleView.setText(args.getString("title"));
            contentView.setText(args.getString("content"));
        }

        return view;
    }
}