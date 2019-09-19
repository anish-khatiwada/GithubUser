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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.instyle.githubuser.adapter.ReposAdapter;
import com.instyle.githubuser.apiutils.BaseApiService;
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

    int totalFollower;
    int totalFollowing;
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
        if (getIntent().getStringExtra("user").isEmpty()) {

            Toast.makeText(DetailActivity.this, "Receiving  data failed", Toast.LENGTH_SHORT).show();

        } else {
            user = getIntent().getStringExtra("user");
            _userProfileName.setText(user);

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
                    public void onNext(List<RepoResponse> RepoResponse) {
                        tvPosts.setText(RepoResponse.size());
                        for (int i = 0; i < RepoResponse.size(); i++) {
                            String name = RepoResponse.get(i).getName();
                            String description = RepoResponse.get(i).getDescription();

                            repoList.add(new Repo(name, description));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        _loading.setVisibility(View.GONE);
                        Toast.makeText(DetailActivity.this, "Reciving repos data", Toast.LENGTH_SHORT).show();

                        mRepoAdapter = new ReposAdapter(DetailActivity.this, repoList, null);
                        _userRepos.setAdapter(mRepoAdapter);
                        mRepoAdapter.notifyDataSetChanged();
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
                        totalFollower = repoResponse.size();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        tvFollowers.setText(totalFollower);
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

                        totalFollowing=RepoResponse.size();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        tvFollowing.setText(totalFollowing);
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
