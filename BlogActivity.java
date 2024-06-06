package com.example.npm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BlogActivity extends AppCompatActivity {
    private static final String TAG = "Gallery";

    private int mCurrentIndex = 0;

    private ViewPager mGallery;


    private TextView blogTips;
    private View blogLine;
    private ImageView blogLogo,retrunTo;
    private ImageView blogTipsImage;


    private List<Integer> mFruitList = new ArrayList<>();

    private List<BlogPost> mBlogPosts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        blogTips = findViewById(R.id.blog_tips);
        blogLine = findViewById(R.id.blog_line);
        blogLogo = findViewById(R.id.blog_logo);
        blogTipsImage = findViewById(R.id.blogTips);
        retrunTo = findViewById(R.id.blog_return);

        retrunTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlogActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });

        initData();

        initWidgets();

        addListeners();
    }
    public void showBlogContentFragment(BlogPost post) {
        BlogContentFragment fragment = new BlogContentFragment();
        blogTips.setVisibility(View.GONE);
        blogLine.setVisibility(View.GONE);
        blogLogo.setVisibility(View.GONE);
        blogTipsImage.setVisibility(View.GONE);
        mGallery.setVisibility(View.GONE);
        retrunTo.setVisibility(View.GONE);
        Bundle args = new Bundle();
        args.putString("title", post.getTitle());
        args.putString("content", post.getContent());
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_blog, fragment) // Change this line
                .addToBackStack(null) // Optional, to add the transaction to the back stack
                .commit();
    }

    private void initData() {
        mBlogPosts = DataRepository.getBlogPosts();
    }


    private void addListeners() {
        mGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, ">>> " + position);
                mCurrentIndex = position;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initWidgets() {
        mGallery = findViewById(R.id.gallery);
        GalleryAdapter adapter = new GalleryAdapter(this, mBlogPosts);
        mGallery.setOffscreenPageLimit(mBlogPosts.size());
        mGallery.setAdapter(adapter);
        mGallery.setCurrentItem(mBlogPosts.size() - 1);
        mGallery.setPageTransformer(false, new GalleryTransformer());

    }



}