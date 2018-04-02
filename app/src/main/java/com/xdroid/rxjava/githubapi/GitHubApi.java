package com.xdroid.rxjava.githubapi;


import com.xdroid.rxjava.model.Contributor;
import com.xdroid.rxjava.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 类描述:GitHubApi
 * 创建人:launcher.myemail@gmail.com
 * 创建时间:16-1-24
 * 备注:https://github.com/basil2style
 */
public interface GitHubApi {

    /**
     * See https://developer.github.com/v3/users/
     */

    @GET("/users/{username}")
    Call<User> getUser(@Path("username") String user);

    @GET("/users/{username}")
    Observable<User> getUserObservable(@Path("username") String username);


    /**
     * See https://developer.github.com/v3/repos/#list-contributors
     */
    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> getContributorsObservable(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/contributors")
    List<Contributor> getContributors(@Path("owner") String owner, @Path("repo") String repo);
}
