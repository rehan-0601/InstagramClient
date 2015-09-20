package com.codepath.rmulla.instagramclient;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rmulla on 9/15/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    //This class will be used to cache the view lookups #ViewHolder Pattern
    private static class ViewHolder {
        TextView tvCaption;
        TextView tvUsername;
        TextView tvLocation;
        TextView tvTime;
        TextView tvLikes;
        TextView tvComment1;
        TextView tvComment2;
        TextView tvViewAll;
        ImageView ivPhoto;
        ImageView ivProfPic;
    }

    //Constructor is instantiated by activity. Contructor args is the information u need from the activity.
    //you will need to pass resource if you want activity to tell the adapter which resource to use
    //here we already know the resource, its fixed. so not required in constructor
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        //second argument is how to want to map each input object item on the output screen.
        super(context, android.R.layout.simple_list_item_1, objects);
    }
    //What our item looks like

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get data item for this position
        InstagramPhoto photo = getItem(position);

        //check if we are using a recycled view, if not inflate
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if(convertView == null){
            //create a new view from template, you will need the expensive findViewbyID calls here
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_photo, parent, false);
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.ivProfPic = (ImageView) convertView.findViewById(R.id.ivProfPic);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvLikes = (TextView)convertView.findViewById(R.id.tvLikes);
            viewHolder.tvComment1 = (TextView) convertView.findViewById(R.id.tvComment1);
            viewHolder.tvComment2 = (TextView) convertView.findViewById(R.id.tvComment2);
            viewHolder.tvViewAll = (TextView) convertView.findViewById(R.id.tvViewAll);
            convertView.setTag(viewHolder);
        }
        else{
            //if convertView !=null means this is a recycled view. Prevent the findViewbyId calls. use getTag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //look up the views we need to populate, image, caption here
        /*TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ImageView ivProfPic = (ImageView) convertView.findViewById(R.id.ivProfPic);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvLikes = (TextView)convertView.findViewById(R.id.tvLikes);
        TextView tvComment1 = (TextView) convertView.findViewById(R.id.tvComment1);
        TextView tvComment2 = (TextView) convertView.findViewById(R.id.tvComment2);*/
        //insert model data into each of the view items

        viewHolder.tvUsername.setText(photo.username);
        //setting image height to actual image height. adjustviewbounds will then resize width to maintain aspect ratio
        //viewHolder.ivPhoto.getLayoutParams().height = photo.imageHeight;
        //trying to set the height of the drawable to match the height/width of the textview.
        //display icon only if there is a location
        viewHolder.tvLocation.setAlpha(1);
        Drawable img = convertView.getResources().getDrawable(R.drawable.location);
        img.setBounds(0, 0, 20, viewHolder.tvLocation.getMeasuredHeight());
        viewHolder.tvLocation.setText(photo.location);
        if (photo.location==null){
            viewHolder.tvLocation.setAlpha(0);
        }
        String epochTime= photo.postTime;
        CharSequence relativeTime =DateUtils.getRelativeTimeSpanString(Long.parseLong(epochTime)*1000,System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS);
        //trucating '45 seconds ago to 45s, 45 minutes ago to 45m etc'
        String timestamp = relativeTime.toString();
        String[] timestampBits = timestamp.split("\\s+");
        String truncatedTimestamp;
        if(timestampBits[1].equals("seconds") || timestampBits[1].equals("second")){
            truncatedTimestamp=timestampBits[0]+"s";
        }
        else if(timestampBits[1].equals("minute") || timestampBits[1].equals("minutes")){
            truncatedTimestamp=timestampBits[0]+"m";
        }
        else if(timestampBits[1].equals("hours") || timestampBits[1].equals("hour")){
            truncatedTimestamp=timestampBits[0]+"h";
        }
        else if(timestampBits[1].equals("days") || timestampBits[1].equals("day")){
            truncatedTimestamp=timestampBits[0]+"d";
        }
        else if(timestampBits[1].equals("weeks") || timestampBits[1].equals("week")){
            truncatedTimestamp=timestampBits[0]+"w";
        }
        else truncatedTimestamp = timestamp;

        viewHolder.tvTime.setText(truncatedTimestamp);
        viewHolder.tvLikes.setText(Integer.toString(photo.likesCount)+" likes");

        //caption has the username followed by the caption
        //int[] colors = convertView.getResources().getIntArray(R.array.androidcolors);
        int usernamebluecolor = convertView.getResources().getColor(R.color.usernameblue);
        //int usernameblue = colors[0];
        ForegroundColorSpan blueForegroundColorSpan = new ForegroundColorSpan(usernamebluecolor);
        StyleSpan boldSpan= new StyleSpan(Typeface.BOLD);
        SpannableStringBuilder ssb = new SpannableStringBuilder(photo.username);
        ssb.setSpan(
                blueForegroundColorSpan,            // the span to add
                0,                                 // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder
        // SPAN_EXCLUSIVE_EXCLUSIVE means to not extend the span when additional
        // text is added in later
        ssb.setSpan(
                boldSpan,            // the span to add
                0,                                 // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(" ");
        ForegroundColorSpan blackForegroundColorSpan = new ForegroundColorSpan(convertView.getResources().getColor(android.R.color.black));
        ssb.append(photo.caption);
        ssb.setSpan(
                blackForegroundColorSpan,
                ssb.length() - photo.caption.length(),
                ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.tvCaption.setText(ssb, TextView.BufferType.EDITABLE);
        //tvCaption.setText(photo.username+" "+photo.caption);
        //Clear out the image-since the download of image using picasso might take time and u might be using a recycled view
        viewHolder.ivProfPic.setImageResource(0);
        viewHolder.ivPhoto.setImageResource(0);
        //Insert image using picasso
        if (photo.imageUrl!="" && photo.profilePicUrl!="") {
            viewHolder.ivPhoto.getLayoutParams().height = photo.imageHeight;
            Picasso.with(getContext()).load(photo.imageUrl).into(viewHolder.ivPhoto);
            //Picasso.with(getContext()).load(photo.imageUrl).resize(photo.imageWidth,photo.imageHeight).centerInside().into(viewHolder.ivPhoto);
            //Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.mipmap.insta_launcher).into(viewHolder.ivPhoto);

            Picasso.with(getContext()).load(photo.profilePicUrl).placeholder(R.mipmap.insta_launcher).into(viewHolder.ivProfPic);
        }

        //fill out the comment views if there are comments.//first word is the username//2nd word is the entire comment


        if (photo.comments.size()>0){
            String[] words = photo.comments.get(0).split("\\s+",2);
            //tvComment1.setText(photo.comments.get(0));
            blueForegroundColorSpan = new ForegroundColorSpan(usernamebluecolor);
            boldSpan= new StyleSpan(Typeface.BOLD);
            ssb = new SpannableStringBuilder(words[0]);
            ssb.setSpan(
                    blueForegroundColorSpan,            // the span to add
                    0,                                 // the start of the span (inclusive)
                    ssb.length(),                      // the end of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder
            // SPAN_EXCLUSIVE_EXCLUSIVE means to not extend the span when additional
            // text is added in later
            ssb.setSpan(
                    boldSpan,            // the span to add
                    0,                                 // the start of the span (inclusive)
                    ssb.length(),                      // the end of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
            ssb.append(words[1]);
            viewHolder.tvComment1.setText(ssb, TextView.BufferType.EDITABLE);
        }

        if (photo.comments.size()>1){
            //tvComment2.setText(photo.comments.get(1));
            String[] words = photo.comments.get(1).split("\\s+",2);
            //tvComment1.setText(photo.comments.get(0));
            blueForegroundColorSpan = new ForegroundColorSpan(usernamebluecolor);
            boldSpan= new StyleSpan(Typeface.BOLD);
            ssb = new SpannableStringBuilder(words[0]);
            ssb.setSpan(
                    blueForegroundColorSpan,            // the span to add
                    0,                                 // the start of the span (inclusive)
                    ssb.length(),                      // the end of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder
            // SPAN_EXCLUSIVE_EXCLUSIVE means to not extend the span when additional
            // text is added in later
            ssb.setSpan(
                    boldSpan,            // the span to add
                    0,                                 // the start of the span (inclusive)
                    ssb.length(),                      // the end of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
            ssb.append(words[1]);
            viewHolder.tvComment2.setText(ssb, TextView.BufferType.EDITABLE);
        }
        if(photo.comments.size()>2){
            viewHolder.tvViewAll.setText("View all "+photo.commentsCount+" comments");
        }
        //return the created item as a view
        return convertView;
    }
}
