package com.twitchflix.applicationclient.channelview;

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
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ChannelDrawer extends NetworkUser<UUID, Void, Boolean> {

    public ChannelDrawer(Activity context) {
        super(context);
    }

    private List<Video> videos;

    private Map<UUID, Bitmap> thumbnails;

    private String channelName;

    @Override
    protected Boolean doInBackground(UUID... ids) {

        if (!isInternetConnectionAvailable()) {
            return false;
        }

        UserData user = ClientApp.getIns().getUserDataRequests().requestUserData(ids[0]);

        channelName = user.getFirstName() + " " + user.getLastName();

        videos = new ArrayList<>();
        thumbnails = new HashMap<>();

        List<UserVideo> videosByUser = ClientApp.getIns().getServerRequests().getVideosByUser(user.getUuid());

        for (UserVideo userVideo : videosByUser) {

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
        if (isContextPresent() && successful) {

            Activity activity = getContextIfPresent();

            SwipeRefreshLayout refreshLayout = activity.findViewById(R.id.channel_activity);

            LinearLayout mainChannelActivity = refreshLayout.findViewById(R.id.main_channel_activity);

            TextView channelName = mainChannelActivity.findViewById(R.id.channel_name);

            channelName.setText(this.channelName);

            ScrollView view = mainChannelActivity.findViewById(R.id.channel_scroll_view);

            LinearLayout videoLayout = view.findViewById(R.id.channel_main_layout);

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

                videoLayout.addView(eachVideo);
            }

            refreshLayout.setRefreshing(false);
        }
    }
}
