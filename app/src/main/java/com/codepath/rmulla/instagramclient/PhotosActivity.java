package com.codepath.rmulla.instagramclient;

import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {
    public static final String CLIENT_ID="bc925d8df6ff474380962bb580ecc0c6";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    private ListView lvPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //SEND OUT API request to fetch photos
        //good to initilaise in onCreate
        photos = new ArrayList<>();
        aPhotos = new InstagramPhotosAdapter(this, photos);
        //Find the listview from the layout
        lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        //Bind adapter to listview to populate it
        lvPhotos.setAdapter(aPhotos);
        //Fetch the popular photos first time, without the refresh
        fetchPopularPhotos();
        // Setup refresh listener which triggers new data loading on swiping down
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupClickListener();
    }


    private void setupClickListener(){
        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstagramPhoto selectedPhoto = photos.get(position);
                String title = "Comments";
                showCommentsDialog(title, selectedPhoto);
            }
        });

    }
    public void showCommentsDialog(String title, InstagramPhoto selectedPhoto){
        FragmentManager fm = getSupportFragmentManager();
        CommentsDialog commentsDialog = CommentsDialog.newInstance(title, selectedPhoto);
        commentsDialog.show(fm, "fragment_comments");
    }

    public void fetchPopularPhotos(){
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        //Create the network client
        AsyncHttpClient client = new AsyncHttpClient();
        //Trigger GET request
        client.get(url, null, new JsonHttpResponseHandler(){
            //onSuccess(worked, 200)

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                photos.clear();
                //Expecting a JSONObject, since response is an Object not an Array ^^

                //Iterate each photo item and decode into java object
                JSONArray photosJSON = null;
                try{
                    photosJSON = response.getJSONArray("data");
                    for(int i=0;i<photosJSON.length();i++){
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        //handling possibility of null index or null values
                        if (photoJSON.optJSONObject("user")!=null) {
                            photo.username = photoJSON.getJSONObject("user").optString("username");
                            photo.profilePicUrl = photoJSON.getJSONObject("user").optString("profile_picture");
                        }
                        else{
                            photo.username="";
                            photo.profilePicUrl="";
                        }
                        if(photoJSON.optJSONObject("caption")!=null){
                            photo.caption = photoJSON.getJSONObject("caption").optString("text");
                        }
                        else photo.caption="";

                        if(photoJSON.optJSONObject("images")!=null) {
                            if(photoJSON.getJSONObject("images").optJSONObject("standard_resolution")!=null){
                                photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
                            }
                            else photo.imageUrl ="";
                        }
                        else photo.imageUrl="";

                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.imageWidth = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
                        if(photoJSON.optJSONObject("likes")!=null){
                            //returns 0 if count is not present
                            photo.likesCount = photoJSON.getJSONObject("likes").optInt("count");
                        }
                        if(photoJSON.optJSONObject("location")!=null) {
                            photo.location = photoJSON.getJSONObject("location").optString("name");
                        }
                        photo.postTime = photoJSON.optString("created_time");

                        //get comments if they exist. get all and store in an array

                        if(photoJSON.optJSONObject("comments")!=null) {
                            photo.commentsCount = photoJSON.getJSONObject("comments").getInt("count");
                            JSONArray commentsJSON = photoJSON.getJSONObject("comments").getJSONArray("data");
                            for (int j = 0; j < commentsJSON.length(); j++) {
                                JSONObject commentJSON = commentsJSON.getJSONObject(j);
                                String username = commentJSON.getJSONObject("from").getString("username");
                                String comment = username + " " + commentJSON.getString("text");
                                //add this comment to the array list of comments in the photo object
                                photo.comments.add(comment);
                            }
                        }

                        //Add decoded object to photos array
                        photos.add(photo);



                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                //notify adapter that the list values have changed so it can re-render
                aPhotos.notifyDataSetChanged();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //DO SOMETHING ON FAILURE
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
