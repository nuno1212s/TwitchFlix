package com.twitchflix.applicationclient.channelview;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.MainActivity;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.UserData;

import java.util.UUID;

public class ChannelView extends AppCompatActivity {

    private static final String CHANNEL_OWNER = "CHANNEL_OWNER";

    private UUID channelOwner;

    public static void start(Context previous, UUID channelOwner) {
        Intent intent = new Intent(previous, ChannelView.class);

        intent.putExtra(CHANNEL_OWNER, channelOwner);

        previous.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_view);

        channelOwner = (UUID) getIntent().getExtras().get(CHANNEL_OWNER);

        initAndDraw();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initAndDraw() {

        SwipeRefreshLayout refreshLayout = findViewById(R.id.channel_activity);

        LinearLayout mainLinear = refreshLayout.findViewById(R.id.main_channel_activity);

        ScrollView scrollView = mainLinear.findViewById(R.id.channel_scroll_view);

        LinearLayout clearable = scrollView.findViewById(R.id.channel_main_layout);

        refreshLayout.setOnRefreshListener(() -> {
            clearable.removeAllViews();

            new ChannelDrawer(ChannelView.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    channelOwner);
        });

        new ChannelDrawer(this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        channelOwner);

    }

}
