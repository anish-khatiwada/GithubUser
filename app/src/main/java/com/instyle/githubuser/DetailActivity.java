package com.instyle.githubuser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.instyle.githubuser.adapter.ReposAdapter;
import com.instyle.githubuser.apiutils.BaseApiService;
import com.instyle.githubuser.apiutils.UtilsApi;
import com.instyle.githubuser.model.Repo;
import com.instyle.githubuser.model.RepoResponse;
import com.instyle.githubuser.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {


    @BindView(R.id.profileProgressBar)
    ProgressBar _loading;

    @BindView(R.id.username)
    TextView _userProfileName;

    @BindView(R.id.userLogin)
    TextView userLogin;

    @BindView(R.id.website)
    TextView _userWebsite;

    @BindView(R.id.tvPosts)
    TextView tvPosts;

    @BindView(R.id.tvFollowers)
    TextView tvFollowers;

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @BindView(R.id.profile_photo)
    CircleImageView profile_photo;

    @BindView(R.id._userRepos)
    RecyclerView _userRepos;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    BaseApiService mApiService;
    ReposAdapter mRepoAdapter;
    List<Repo> repoList = new ArrayList<>();

    String user = "";
    String userProfile = "";
    String htmlUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.profileToobar);
        setSupportActionBar(toolbar);
        mApiService = UtilsApi.getAPIService();
        if (getIntent().getStringExtra("user").isEmpty()) {

            Toast.makeText(DetailActivity.this, "Receiving  data failed", Toast.LENGTH_SHORT).show();

        } else {
            user = getIntent().getStringExtra("user");
            _userProfileName.setText(user);
            userLogin.setText(user);

        }

        userProfile = getIntent().getStringExtra("userProfile");
        htmlUrl = getIntent().getStringExtra("html_url");


        Log.i("result",user);
        Log.i("result",userProfile);
        Log.i("result",htmlUrl);


        //checking for network connectivity
        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(coordinator, "No Network connection", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            requestRepos(user);
                            requestTotalFollower(user);
                            requestTotalFollowing(user);
                        }
                    });

            snackbar.show();
        } else {

            requestRepos(user);
            requestTotalFollower(user);
            requestTotalFollowing(user);
        }

        _userWebsite.setText(htmlUrl);

        Picasso.get()
                .load(userProfile)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(profile_photo);


    }
    private void requestRepos(String username) {
        _loading.setVisibility(View.VISIBLE);

        mApiService.requestRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RepoResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<RepoResponse> responseRepos) {
                        tvPosts.setText(Integer.toString(responseRepos.size()));
                        Log.i("repoResponse", responseRepos.toString());

                        for (int i = 0; i < responseRepos.size(); i++) {
                            String name = responseRepos.get(i).getName();
                            String description = responseRepos.get(i).getDescription();

                            repoList.add(new Repo(name, description));
                        }
                        mRepoAdapter = new ReposAdapter(DetailActivity.this, repoList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        _loading.setVisibility(View.GONE);
                        Toast.makeText(DetailActivity.this, "loading data.....", Toast.LENGTH_SHORT).show();


                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);

                        _userRepos.setLayoutManager(layoutManager);

                        _userRepos.setItemAnimator(new DefaultItemAnimator());
                        _userRepos.setHasFixedSize(true);
                        _userRepos.setAdapter(mRepoAdapter);
                    }
                });
    }
    private void requestTotalFollower(String username) {


        mApiService.requestFollower(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Users>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Users> repoResponse) {
                        Log.i("repoResponse", repoResponse.toString());

                        tvFollowers.setText(Integer.toString(repoResponse.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private void requestTotalFollowing(String username) {


        mApiService.requestFollowing(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Users>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Users> RepoResponse) {


                        tvFollowing.setText(Integer.toString(RepoResponse.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed

        super.onBackPressed();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
