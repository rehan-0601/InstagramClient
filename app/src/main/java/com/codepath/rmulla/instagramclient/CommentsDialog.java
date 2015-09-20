package com.codepath.rmulla.instagramclient;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by rmulla on 9/19/15.
 */
public class CommentsDialog extends DialogFragment {
    public CommentsDialog() {
        // Empty constructor required for DialogFragment
    }

    public static CommentsDialog newInstance(String title, InstagramPhoto selectedPhoto) {
        CommentsDialog frag = new CommentsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("comments",selectedPhoto.comments);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container);
        TextView tvComments = (TextView) view.findViewById(R.id.tvComments);
        ArrayList<String> commentsList = getArguments().getStringArrayList("comments");
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        for(int i=0;i<commentsList.size();i++){
            String formattedText = "<b><font color='#0C3F7D'>"+commentsList.get(i).split("\\s+",2)[0]+"</font></b> "+commentsList.get(i).split("\\s+",2)[1];
            tvComments.append(Html.fromHtml(formattedText));
            // tvComments.append(commentsList.get(i));
            tvComments.append("\n");
        }
        // Show soft keyboard automatically
        /*mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);*/
        return view;
    }

}
