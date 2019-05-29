package com.twitchflix.applicationclient.landingpage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;

public class LandingPageDrawer extends NetworkUser<Void, Void, Boolean> {

    private Map<UUID, List<Video>> videos;

    private Map<UUID, String> channelNames;

    private Map<UUID, Bitmap> thumbnails;

    LandingPageDrawer(Activity landingPage) {
        super(landingPage);

        this.videos = new LinkedHashMap<>();
        this.channelNames = new HashMap<>();
        this.thumbnails = new HashMap<>();
    }

    @Override
    protected Boolean doInBackground(Void... strings) {

        if (!isInternetConnectionAvailable()) {
            return false;
        }

        List<UserVideo> videos = ClientApp.getIns().getServerRequests()
                .getLandingPage(ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

        for (Video video : videos) {

            List<Video> orDefault = this.videos.get(video.getUploader());

            if (orDefault == null) {
                orDefault = new ArrayList<>();
            }

            orDefault.add(video);

            this.videos.put(video.getUploader(), orDefault);
        }

        for (UUID uuid : this.videos.keySet()) {

            UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(uuid);

            this.channelNames.put(uuid, userData.getFirstName() + " " + userData.getLastName());

        }

        for (List<Video> allVideos : this.videos.values()) {

            for (Video video : allVideos) {

                try {
                    InputStream inputStream = new URL(video.getThumbnailLink()).openStream();

                    this.thumbnails.put(video.getVideoID(), BitmapFactory.decodeStream(inputStream));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean successful) {

        if (isContextPresent()) {

            Activity activity = getContextIfPresent();

            SwipeRefreshLayout refreshLayout = activity.findViewById(R.id.main_landing_page_refresh);

            ScrollView layout = refreshLayout.findViewById(R.id.main_landing_page_scroll);

            LinearLayout landing_page_layout = layout.findViewById(R.id.main_landing_page_layout);

            for (Map.Entry<UUID, List<Video>> videos : videos.entrySet()) {

                TextView textView = new TextView(activity);

                textView.setText(this.channelNames.get(videos.getKey()));

                textView.setTextSize(25);

                textView.setPadding(10, 40, 0, 15);

                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                landing_page_layout.addView(textView);

                LinearLayout videoLayout = new LinearLayout(activity);

                videoLayout.setOrientation(LinearLayout.HORIZONTAL);

                videoLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                for (Video video : videos.getValue()) {
                    ImageView view = new ImageView(activity);

                    view.setLayoutParams(new ViewGroup.MarginLayoutParams(320, 240));

                    view.setClickable(true);

                    view.setPadding(15, 0, 0, 0);

                    view.setOnClickListener(new OnClickVideoListener(activity, video.getVideoID()));

                    view.setAdjustViewBounds(true);

                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    view.setImageBitmap(this.thumbnails.get(video.getVideoID()));

                    videoLayout.addView(view);
                }

                landing_page_layout.addView(videoLayout);

            }

            refreshLayout.setRefreshing(false);

        }

    }
}
