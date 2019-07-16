package com.twitchflix.applicationclient.landingpage;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.navigation.NavigationView;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.MainActivity;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.activities.AccountSettings;
import com.twitchflix.applicationclient.activities.Stream;
import com.twitchflix.applicationclient.channelview.ChannelView;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.landingpage.drawers.LandingPageDrawer;
import com.twitchflix.applicationclient.landingpage.drawers.SearchPageDrawer;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.utils.NetworkUser;
import com.twitchflix.applicationclient.viewmodels.LandingPageViewModel;
import com.twitchflix.applicationclient.utils.UserDataLoader;

public class LandingPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserDataLoader {

    private LandingPageViewModel pageViewModel;

    private Drawer searchPageDrawer, landingPageDrawer;

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        this.pageViewModel = ViewModelProviders.of(this).get(LandingPageViewModel.class);

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

        initUserInformationDisplay();
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView view = (SearchView) item.getActionView();

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                pageViewModel.getSearchQueryVideos().observe(LandingPage.this, (videos) -> {

                    searchPageDrawer.draw(videos);

                    refreshLayout.setRefreshing(false);

                });

                pageViewModel.setQueryString("");

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                refreshLayout.setRefreshing(true);

                pageViewModel.getSearchQueryVideos().removeObservers(LandingPage.this);

                pageViewModel.requestRefresh();

                return true;
            }
        });

        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                pageViewModel.setQueryString(s);

                return true;
            }
        });

        return true;
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

    /**
     * Initializes and displays the users information
     */
    private void initUserInformationDisplay() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navView = drawerLayout.findViewById(R.id.nav_view);

        View headerView = navView.getHeaderView(0);

        TextView userEmail = headerView.findViewById(R.id.display_userEmail),
                userName = headerView.findViewById(R.id.display_userName);

        ImageView userPhoto = headerView.findViewById(R.id.userPhoto);

        UserData userData = ClientApp.getIns().getLoginHandler().getCurrentUserData();

        userEmail.setText(userData.getEmail());

        String userFullName = userData.getFirstName() + " " + userData.getLastName();

        userName.setText(userFullName);

        this.pageViewModel.requestLoadUserPhoto();

        this.pageViewModel.getUserPhoto().observe(this, userPhoto::setImageBitmap);
    }

    /**
     * Initializes and displays the landing page videos
     */
    private void initAndDraw() {
        this.refreshLayout = findViewById(R.id.main_landing_page_refresh);

        ScrollView layout = refreshLayout.findViewById(R.id.main_landing_page_scroll);

        LinearLayout videoLayout = layout.findViewById(R.id.main_landing_page_layout);

        refreshLayout.setOnRefreshListener(() -> this.pageViewModel.requestRefresh());

        this.searchPageDrawer = new SearchPageDrawer(this, videoLayout);

        this.landingPageDrawer = new LandingPageDrawer(this, videoLayout);

        refreshLayout.setRefreshing(true);

        this.pageViewModel.getLandingPageVideos().observe(this, (videos) -> {

            landingPageDrawer.draw(videos);

            refreshLayout.setRefreshing(false);

        });

        this.pageViewModel.requestRefresh();

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

                    ((Activity) getContextIfPresent()).finish();
                }
            }

        }
    }
}
