package com.twitchflix.applicationclient.landingpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.MainActivity;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.activities.AccountSettings;
import com.twitchflix.applicationclient.activities.Stream;
import com.twitchflix.applicationclient.channelview.ChannelView;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.searchvideos.SearchActivity;
import com.twitchflix.applicationclient.utils.NetworkUser;
import com.twitchflix.applicationclient.viewmodels.LandingPageViewModel;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LandingPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LandingPageViewModel pageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        this.pageViewModel = ViewModelProviders.of(this).get(LandingPageViewModel.class);

        this.pageViewModel.getLandingPageVideos().observe(this, (videos) -> {

        });

        setTitle("TwitchFlix");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAndDraw();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView view = (SearchView) item.getActionView();

        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                SearchActivity.start(LandingPage.this, s);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.own_channel) {

            ChannelView.start(this, ClientApp.getIns().getLoginHandler().getCurrentActiveConnection().getOwner());

        } else if (id == R.id.start_stream) {

            Intent intent = new Intent(this, Stream.class);

            startActivity(intent);

        } else if (id == R.id.account_settings) {

            Intent intent = new Intent(this, AccountSettings.class);

            startActivity(intent);

        } else if (id == R.id.logout) {
            new LogOut(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initAndDraw() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navView = drawerLayout.findViewById(R.id.nav_view);

        View headerView = navView.getHeaderView(0);

        TextView userEmail = headerView.findViewById(R.id.display_userEmail),
                userName = headerView.findViewById(R.id.display_userName);

        UserData userData = ClientApp.getIns().getLoginHandler().getCurrentUserData();

        userEmail.setText(userData.getEmail());

        String userFullName = userData.getFirstName() + " " + userData.getLastName();

        userName.setText(userFullName);
    }

    private static class LogOut extends NetworkUser<Void, Void, Boolean> {

        public LogOut(LandingPage page) {
            super(page);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (!isInternetConnectionAvailable()) {
                return false;
            }

            ClientApp.getIns().getLoginHandler().logOut();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean successfull) {

            if (isContextPresent()) {
                if (successfull) {
                    sendToActivity(MainActivity.class);

                    finishActivity();
                }
            }

        }
    }
}
