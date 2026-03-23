package org.example.retrofit;

import org.example.model.api.Repo;
import org.example.model.api.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface UserService {
    @GET("/users/{username}")
    public Call<User> getUser(@Path("username") String username);

    @GET("/users/{username}/repos")
    public Call<List<Repo>> getUserRepos(@Path("username") String username);
}
