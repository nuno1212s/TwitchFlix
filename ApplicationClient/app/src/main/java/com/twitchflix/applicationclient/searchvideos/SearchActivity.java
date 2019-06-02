package com.twitchflix.applicationclient.searchvideos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
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

        SearchView viewById = findViewById(R.id.search_view);

        viewById.setQuery(search, false);

        viewById.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                search(s);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

    }

    public void search(String search) {
        this.search = search;

        new SearchDrawer(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                search);

    }


}
