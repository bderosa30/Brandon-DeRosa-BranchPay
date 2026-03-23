package org.example.action;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.example.model.api.Repo;
import org.example.model.api.User;
import org.example.retrofit.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsActionTest {
    private final UserService userService = Mockito.mock(UserService.class, RETURNS_DEEP_STUBS);

    private UserDetailsAction action;

    @BeforeEach
    public void setup() {
        action = new UserDetailsAction(userService);
    }

    @Test
    public void aggregatedData() throws Exception {
        User expectedUser = User.builder()
                .username("test")
                .build();
        when(userService.getUser(eq("test")).execute()).thenReturn(Response.success(expectedUser));

        List<Repo> expectedRepos = List.of(Repo.builder()
                .url("url.com")
                .name("test")
                .build());
        when(userService.getUserRepos(eq("test")).execute()).thenReturn(Response.success(expectedRepos));

        User actual = action.getAggregatedUserDetails("test");
        assertThat(actual.getUsername()).isEqualTo(expectedUser.getUsername());
        assertThat(actual.getRepos().size()).isEqualTo(expectedRepos.size());
    }

    @Test
    public void userDetails_success() throws Exception {
        User expected = User.builder()
                .username("test")
                .build();
        when(userService.getUser(eq("test")).execute()).thenReturn(Response.success(expected));

        User actual = action.getUserDetails("test");
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
    }

    @Test
    public void userDetails_ioexception() throws Exception {
        when(userService.getUser(anyString()).execute()).thenThrow(new IOException("test"));

        assertThatThrownBy(() -> action.getUserDetails(anyString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch user data");
    }

    @Test
    public void userDetails_nullBody() throws Exception {
        when(userService.getUser(anyString()).execute()).thenReturn(Response.success(null));

        assertThatThrownBy(() -> action.getUserDetails(anyString()))
                .isInstanceOf(UserDetailsAction.ResourceNotFoundException.class)
                .hasMessageContaining("was not found in github service");
    }

    @Test
    public void userDetails_failure() throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        when(userService.getUser(anyString()).execute())
                .thenReturn(Response.error(500, ResponseBody.create(JSON, "failure")));

        assertThatThrownBy(() -> action.getUserDetails(anyString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error:");
    }

    @Test
    public void userRepos_success() throws Exception {
        List<Repo> expected = List.of(Repo.builder()
                .url("url.com")
                .name("test")
                .build());
        when(userService.getUserRepos(eq("test")).execute()).thenReturn(Response.success(expected));

        List<Repo> actual = action.getUserRepos("test");
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.getFirst().getName()).isEqualTo(expected.getFirst().getName());
        assertThat(actual.getFirst().getUrl()).isEqualTo(expected.getFirst().getUrl());
    }

    @Test
    public void userRepos_ioexception() throws Exception {
        when(userService.getUserRepos(anyString()).execute()).thenThrow(new IOException("test"));

        assertThatThrownBy(() -> action.getUserRepos(anyString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch user repo data");
    }

    @Test
    public void userRepos_nullBody() throws Exception {
        when(userService.getUser(anyString()).execute()).thenReturn(Response.success(null));

        assertThatThrownBy(() -> action.getUserDetails(anyString()))
                .isInstanceOf(UserDetailsAction.ResourceNotFoundException.class)
                .hasMessageContaining("was not found in github service");
    }

    @Test
    public void userRepos_failure() throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        when(userService.getUserRepos(anyString()).execute())
                .thenReturn(Response.error(500, ResponseBody.create(JSON, "failure")));

        assertThatThrownBy(() -> action.getUserRepos(anyString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error:");
    }
}
