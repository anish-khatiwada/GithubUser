package com.instyle.githubuser.apiutils;


import com.instyle.githubuser.model.RepoResponse;
import com.instyle.githubuser.model.Users;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface BaseApiService {

    @GET("users/{username}")
   Observable<List<RepoResponse>> requestUserDetails(@Path("username") String username);

    @GET("users/{username}/repos")
    Observable<List<RepoResponse>> requestRepos(@Path("username") String username);

    @GET("/users")
    Observable<List<Users>> getUserList();

    @GET("/users/{username}/followers")
    Observable<List<Users>> requestFollower(@Path("username") String username);

    @GET("/users/{username}/following")
    Observable<List<Users>> requestFollowing(@Path("username") String username);
}