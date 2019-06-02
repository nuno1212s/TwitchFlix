package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.VideoStream;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.lang.ref.WeakReference;

public class StreamOptions extends AppCompatActivity {

    private EditText title, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_options);

        title = findViewById(R.id.stream_title);
        desc = findViewById(R.id.stream_desc);
    }


    public void onClickStartStream(View view) {

        String stream_title = title.getText().toString(),
                stream_desc = desc.getText().toString();

        new RequestStreamLink(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                stream_title,
                stream_desc);
    }

    private static class RequestStreamLink extends NetworkUser<String, Void, VideoStream> {

        RequestStreamLink(StreamOptions options) {
            super(options);
        }

        @Override
        protected VideoStream doInBackground(String... args) {
            if (!isInternetConnectionAvailable()) {
                return null;
            }

            return ClientApp.getIns().getServerRequests().requestVideoStream(args[0],
                    args[1],
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());
        }

        @Override
        protected void onPostExecute(VideoStream videoStream) {

            if (isContextPresent() && videoStream != null) {

                Intent intent = new Intent(getContextIfPresent(), StreamVideo.class);

                intent.putExtra("streamLink", videoStream.getStreamLink());

                getContextIfPresent().startActivity(intent);

                finishActivity();
            }
        }
    }
}
