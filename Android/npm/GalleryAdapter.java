package com.tom.npm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class GalleryAdapter extends PagerAdapter {
    private Context mContext;
    private List<BlogPost> mBlogPosts;

    public GalleryAdapter(Context context, List<BlogPost> blogPosts) {
        mContext = context;
        mBlogPosts = blogPosts;
    }

    @Override
    public int getCount() {
        return mBlogPosts.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BlogPost post = mBlogPosts.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        String title = post.getTitle();
        if (title == null || title.isEmpty()) {
            title = "默认标题"; // Replace with your default title
        }
        tvTitle.setText(title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BlogActivity)mContext).showBlogContentFragment(post);
            }
        });

        AppCompatImageView imgView = view.findViewById(R.id.img);
        imgView.setImageResource(post.getImageId());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}