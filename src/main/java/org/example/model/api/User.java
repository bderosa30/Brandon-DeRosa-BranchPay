package org.example.model.api;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@ToString
@Builder(toBuilder = true)
public class User {

    // alternate is used for deserialization
    // This approach can be brittle in certain scenarios so prefer using a
    // custom TypeAdapter implementation if time permits

    @SerializedName(value = "user_name", alternate = {"login"})
    private String username;

    @SerializedName(value = "display_name", alternate = {"name"})
    private String displayName;

    @SerializedName(value = "avatar", alternate = {"avatar_url"})
    private String avatar;

    @SerializedName(value = "geo_location", alternate = {"location"})
    private String geoLocation;

    @SerializedName(value = "email")
    private String email;

    @SerializedName(value = "url")
    private String url;

    @SerializedName(value = "created_at")
    private ZonedDateTime createAt;

    private List<Repo> repos;
}
