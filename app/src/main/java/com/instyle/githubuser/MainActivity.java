package com.instyle.githubuser;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.instyle.githubuser.adapter.UsersAdapter;
import com.instyle.githubuser.apiutils.BaseApiService;
import com.instyle.githubuser.apiutils.UtilsApi;
import com.instyle.githubuser.model.Users;
import com.instyle.githubuser.model.displayUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id._loading)
    ProgressBar _loading;
    @BindView(R.id._userList)
    RecyclerView _userList;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    private SearchView searchView;
    BaseApiService callApiService;
    List<displayUser> _displayUser = new ArrayList<>();
    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callApiService = UtilsApi.getAPIService();

        //checking for network connectivity
        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(coordinator, "No Network connection", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            requestUserList();
                        }
                    });

            snackbar.show();
        } else {
            requestUserList();
        }


    }

    private void requestUserList() {

        _loading.setVisibility(View.VISIBLE);

        callApiService.getUserList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Users>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Users> responseRepos) {
                        Log.i("result", responseRepos.toString());


                        for (int i = 0; i < responseRepos.size(); i++) {
                            String name = responseRepos.get(i).getLogin();
                            String userProfile = responseRepos.get(i).getAvatar_url();
                            String html_url = responseRepos.get(i).getHtml_url();


                            Log.i("name", name);
                            Log.i("userProfile", userProfile);

                            _displayUser.add(new displayUser(name, userProfile, html_url));

                        }
                        usersAdapter = new UsersAdapter(getApplicationContext(), _displayUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        _loading.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                                "Loading List data", Toast.LENGTH_SHORT).show();


                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);

                        _userList.setLayoutManager(layoutManager);

                        _userList.setItemAnimator(new DefaultItemAnimator());
                        _userList.setHasFixedSize(true);
                        _userList.setAdapter(usersAdapter);

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i("query", query);
                // filter recycler view when query submitted
                usersAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                usersAdapter.getFilter().filter(query);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
