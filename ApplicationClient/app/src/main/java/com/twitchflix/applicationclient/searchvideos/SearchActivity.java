package com.twitchflix.applicationclient.searchvideos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.R;

public class SearchActivity extends AppCompatActivity {

    private static final String SEARCH = "search";

    public static void start(Context context, String search) {
        Intent intent = new Intent(context, SearchActivity.class);

        intent.putExtra(SEARCH, search);

        context.startActivity(intent);
    }

    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = getIntent().getExtras().getString(SEARCH);

        search(search);

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.search_refresh);

        swipeRefresh.setOnRefreshListener(() -> search(SearchActivity.this.search));

        TextView text = findViewById(R.id.search_view);

        text.setText(search);

    }

    public void search(String search) {
        this.search = search;

        new SearchDrawer(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                search);

    }


}
