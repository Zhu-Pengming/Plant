package com.example.npm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private List<News> newsList;
    private Context mContext;

    public BlogAdapter(List<News> newsList, Context mContext) {
        this.newsList = newsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        final BlogAdapter.ViewHolder holder = new BlogAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                News news = newsList.get(holder.getAdapterPosition());
                if (isTwoPane) {
                    // 如果是双页模式，则刷新NewsContentFragment中的内容
                    BlogFragment fragment = (BlogFragment) getActivity().
                            getSupportFragmentManager().
                            findFragmentById(R.id.newsContentFrag);
                    fragment.refresh(news.getTitle(), news.getContent());
                } else {
                    // 如果是单页模式，则直接启动NewsContentActivity
                    Intent intent = new Intent(getActivity(), NewsContentActivity.class);
                    intent.putExtra("news_title", news.getTitle());
                    intent.putExtra("news_content", news.getContent());
                    startActivity(intent);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlogFragment.NewsAdapter.ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.newsTitle.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView newsTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
        }
    }
}