package com.tom.npm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class BlogContentFragment extends Fragment {

    private TextView titleView,contentView;


    private static final String TAG = "BlogContentFragment";

    private ImageView returnTo;



    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_content, container, false);

        titleView = view.findViewById(R.id.BlogTitle);
        contentView = view.findViewById(R.id.BlogContent); // Make sure this ID is an EditText in your layout

        returnTo = view.findViewById(R.id.return_content);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BlogActivity.class);
                getActivity().overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });



        Bundle args = getArguments();
        if (args != null) {
            titleView.setText(args.getString("title"));
            contentView.setText(args.getString("content"));
            contentView.setTextIsSelectable(true);
            contentView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.text_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (item.getItemId() == R.id.chat) {
                        int min = 0;
                        int max = contentView.getText().length();
                        if (contentView.isFocused()) {
                            final int selStart = contentView.getSelectionStart();
                            final int selEnd = contentView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        final CharSequence selectedText = contentView.getText().subSequence(min, max);


                        mode.finish();

                        final ChatDialogFragment chatDialog = new ChatDialogFragment();
                        chatDialog.setSelectedText(selectedText);
                        chatDialog.setFullText(contentView.getText().toString());
                        chatDialog.show(getFragmentManager(), "chatDialog");

                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }



        return view;
    }



}