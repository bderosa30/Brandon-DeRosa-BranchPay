package org.example.action;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import org.example.model.api.Repo;
import org.example.model.api.User;
import org.example.retrofit.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.example.config.SimpleCacheManager.USER_DETAILS_CACHE;

/**
 * Action delegate for retrieving github user details
 */
@Service
public class UserDetailsAction {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsAction.class);
    private final UserService userService;

    @Autowired
    public UserDetailsAction(UserService userService) {
        this.userService = userService;
    }

    @Cacheable(USER_DETAILS_CACHE)
    public User getAggregatedUserDetails(String user) {
        // TODO(bderosa): for some reason logger isn't letting me pass in string format args. investigate.

        User details = getUserDetails(user);
        List<Repo> repos = getUserRepos(user);

        return details.toBuilder()
                .repos(repos)
                .build();
    }

    @VisibleForTesting
    User getUserDetails(String user) {
        try {
            Response<User> resp = userService.getUser(user).execute();
            if (!resp.isSuccessful()) {
                String errorMsg = "";
                // errorBody.string() does close the resource
                if (resp.errorBody() != null) {
                    errorMsg = resp.errorBody().string();
                }
                throw new RuntimeException(String.format("Failed to fetch user details. Error: %s", errorMsg));
            }

            if (resp.body() == null) {
                String msg = String.format("User: %s was not found in github service", user);
                logger.warn(msg);
                throw new ResourceNotFoundException(msg);
            }

            return resp.body();
        } catch (IOException io) {
            String msg = String.format("Failed to fetch user data: %s", io.getMessage());
            logger.error(msg);
            throw new RuntimeException(msg, io.getCause());
        }
    }

    @VisibleForTesting
    List<Repo> getUserRepos(String user) {
        try {
            Response<List<Repo>> resp = userService.getUserRepos(user).execute();
            if (!resp.isSuccessful()) {
                String errorMsg = "";
                // errorBody.string() does close the resource
                if (resp.errorBody() != null) {
                    errorMsg = resp.errorBody().string();
                }
                throw new RuntimeException(String.format("Failed to fetch repo details for user: %s. Error: %s", user, errorMsg));
            }

            if (resp.body() == null) {
                String msg = String.format("User repo details for user: %s was not found in github service", user);
                logger.warn(msg);
                throw new ResourceNotFoundException(msg);
            }

            return resp.body();
        } catch (IOException io) {
            String msg = String.format("Failed to fetch user repo data: %s", io.getCause());
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String msg) {
            super(msg);
        }
    }
}
