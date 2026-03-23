package org.example.integration;

import org.example.action.UserDetailsAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.retrofit.adapters.UtcTimeAdapter.CUSTOM_TIME_FORMATTER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubUserDetailsIntegrationTest {

    @Autowired
    private UserDetailsAction action;

    /**
     * Integration test is likely better suited at the REST controller level but ran out of time
     * since its a little more involved to setup.
     */
    @Test
    public void testEndToEnd() {
        var resp = action.getAggregatedUserDetails("octocat");

        assertThat(resp.getUsername()).isEqualTo("octocat");
        assertThat(resp.getDisplayName()).isEqualTo("The Octocat");
        assertThat(resp.getAvatar()).isEqualTo("https://avatars.githubusercontent.com/u/583231?v=4");
        assertThat(resp.getGeoLocation()).isEqualTo("San Francisco");
        assertThat(resp.getEmail()).isNull();
        assertThat(resp.getUrl()).isEqualTo("https://api.github.com/users/octocat");
        // ZonedDateTimes dont know formatting until we tell it what to do. Serializer does this for us for REST calls
        // but we need to explicitly format time here for unit testing the action.
        assertThat(resp.getCreateAt().format(CUSTOM_TIME_FORMATTER)).isEqualTo("Tue, 25 Jan 2011 18:44:36 GMT");

        assertThat(resp.getRepos().getFirst().getUrl())
                .isEqualTo("https://api.github.com/repos/octocat/boysenberry-repo-1");
        assertThat(resp.getRepos().getFirst().getName()).isEqualTo("boysenberry-repo-1");
    }
}
