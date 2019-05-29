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

    private static class RequestStreamLink extends AsyncTask<String, Void, VideoStream> {

        private WeakReference<StreamOptions> options;

        RequestStreamLink(StreamOptions options) {
            this.options = new WeakReference<>(options);
        }

        @Override
        protected VideoStream doInBackground(String... args) {
            return ClientApp.getIns().getServerRequests().requestVideoStream(args[0],
                    args[1],
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());
        }

        @Override
        protected void onPostExecute(VideoStream videoStream) {

            StreamOptions streamOptions = options.get();

            if (streamOptions != null && videoStream != null) {

                Intent intent = new Intent(streamOptions, StreamVideo.class);

                intent.putExtra("streamLink", videoStream.getStreamLink());

                streamOptions.startActivity(intent);

                streamOptions.finish();
            }
        }
    }
}
