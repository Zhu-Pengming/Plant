package com.example.npm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private List<BlogPost> blogPosts;
    private LayoutInflater inflater;
    private Context context;

    public BlogAdapter(Context context, List<BlogPost> blogPosts) {
        this.inflater = LayoutInflater.from(context);
        this.blogPosts = blogPosts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.blog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BlogPost post = blogPosts.get(position);
        holder.title.setText(post.getTitle());
        holder.itemView.setOnClickListener(v -> {
            ((MainActivity)context).showBlogContentFragment(post);
        });
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blog_title);
        }
    }
}
