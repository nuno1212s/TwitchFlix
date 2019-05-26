package com.twitchflix.applicationclient.activities;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.VideoStream;

public class StreamVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_video);
    }


    private static class RequestStreamLink extends AsyncTask<String, Void, VideoStream> {

        @Override
        protected VideoStream doInBackground(String... args) {
            return ClientApp.getIns().getServerRequests().requestVideoStream(args[0],
                    args[1],
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());
        }

        @Override
        protected void onPostExecute(VideoStream videoStream) {

            //TODO: Stream video

        }
    }

}
