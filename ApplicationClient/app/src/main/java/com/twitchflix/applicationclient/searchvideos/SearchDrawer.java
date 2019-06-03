package com.twitchflix.applicationclient.searchvideos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class SearchDrawer extends NetworkUser<String, Void, Boolean> {

    public SearchDrawer(Activity context) {
        super(context);
    }

    private List<Video> videos;

    private Map<UUID, Bitmap> thumbnails;

    @Override
    protected Boolean doInBackground(String... strings) {

        if (!isInternetConnectionAvailable()) {
            return false;
        }

        List<UserVideo> searchVideos = ClientApp.getIns().getServerRequests().searchVideo(strings[0], ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

        videos = new ArrayList<>();
        thumbnails = new HashMap<>();

        for (UserVideo userVideo : searchVideos) {

            videos.add(userVideo);

            try {
                InputStream inputStream = new URL(userVideo.getThumbnailLink()).openStream();

                this.thumbnails.put(userVideo.getVideoID(), BitmapFactory.decodeStream(inputStream));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean successful) {
        if (isContextPresent()) {
            if (successful) {

                Activity activity = getContextIfPresent();

                SwipeRefreshLayout refreshLayout = activity.findViewById(R.id.search_refresh);

                ScrollView scrollView = refreshLayout.findViewById(R.id.scroll_search);

                LinearLayout displayResults = scrollView.findViewById(R.id.display_results);

                displayResults.removeAllViews();

                for (Video video : videos) {

                    LinearLayout eachVideo = new LinearLayout(getContextIfPresent());

                    eachVideo.setClickable(true);

                    eachVideo.setOnClickListener(new OnClickVideoListener(activity, video.getVideoID()));

                    eachVideo.setPadding(0, 35, 0, 0);

                    eachVideo.setOrientation(LinearLayout.HORIZONTAL);

                    ImageView thumbnail = new ImageView(getContextIfPresent());

                    thumbnail.setLayoutParams(new ViewGroup.MarginLayoutParams(320, 240));

                    thumbnail.setImageBitmap(this.thumbnails.get(video.getVideoID()));

                    LinearLayout textAndDesc = new LinearLayout(getContextIfPresent());

                    textAndDesc.setOrientation(LinearLayout.VERTICAL);

                    TextView videoName = new TextView(getContextIfPresent());

                    videoName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40));

                    videoName.setText(video.getTitle());

                    videoName.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    TextView videoDesc = new TextView(getContextIfPresent());

                    videoDesc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200));

                    videoDesc.setText(video.getDescription());

                    textAndDesc.addView(videoName);
                    textAndDesc.addView(videoDesc);

                    eachVideo.addView(thumbnail);
                    eachVideo.addView(textAndDesc);

                    displayResults.addView(eachVideo);
                }

                refreshLayout.setRefreshing(false);

            }
        }
    }
}
