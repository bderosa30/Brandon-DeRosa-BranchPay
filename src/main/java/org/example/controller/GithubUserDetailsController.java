package org.example.controller;

import org.example.action.UserDetailsAction;
import org.example.model.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest endpoint(s) related to GitHub user details
 */
@RestController
@RequestMapping("/github")
public class GithubUserDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsAction.class);

    private final UserDetailsAction userDetailsAction;

    @Autowired
    public GithubUserDetailsController(UserDetailsAction userDetailsAction) {
        this.userDetailsAction = userDetailsAction;
    }

    @GetMapping("/user/{user}")
    public User getUserDetails(@PathVariable String user) {
        logger.info(String.format("Fetching github user details for user: %s", user));

        return userDetailsAction.getAggregatedUserDetails(user);
    }
}
