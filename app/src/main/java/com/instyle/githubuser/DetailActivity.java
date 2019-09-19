package com.instyle.githubuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.instyle.githubuser.adapter.ReposAdapter;
import com.instyle.githubuser.adapter.UsersAdapter;
import com.instyle.githubuser.apiutils.BaseApiService;
import com.instyle.githubuser.model.Repo;
import com.instyle.githubuser.model.RepoResponse;
import com.instyle.githubuser.model.Users;
import com.instyle.githubuser.model.displayUser;
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


    BaseApiService mApiService;
    ReposAdapter mRepoAdapter;
    List<Repo> repoList = new ArrayList<>();

    int totalFollower;
    int totalFollowing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.profileToobar);
        setSupportActionBar(toolbar);

        String user = getIntent().getStringExtra("user");
        String userProfile = getIntent().getStringExtra("userProfile");
        String htmlUrl = getIntent().getStringExtra("html_url");


        Log.i("result",user);
        Log.i("result",userProfile);
        Log.i("result",htmlUrl);

        Picasso.get()
                .load(userProfile)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(profile_photo);


        _userProfileName.setText(user);
      
        _userWebsite.setText(htmlUrl);
        requestTotalFollower(user);
        requestTotalFollowing(user);
        requestRepos( user);

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
                    public void onNext(List<Users> RepoResponse) {
                        totalFollower=RepoResponse.size();

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


        mApiService.requestRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RepoResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<RepoResponse> RepoResponse) {

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
}
