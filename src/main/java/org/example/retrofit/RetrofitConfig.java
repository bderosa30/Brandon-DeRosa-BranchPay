package org.example.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import okhttp3.OkHttpClient;
import org.example.retrofit.adapters.UtcTimeAdapter;
import org.example.retrofit.interceptors.RateLimitInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.okhttp")
public class RetrofitConfig {

    private RetrofitService userService;

    // TODO(bderosa): we can have multiple instances of gson depending on different API integrations. Consider
    //  splitting "named" gson config(s) into their own class.
    @Bean
    public Gson userDetailsGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .registerTypeAdapter(ZonedDateTime.class, new UtcTimeAdapter())
                .create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(userDetailsGson());
        return converter;
    }

    @Bean
    public UserService userServiceRetrofit() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(userService.connTimeout, TimeUnit.MILLISECONDS);
        httpClientBuilder.readTimeout(userService.readTimeout, TimeUnit.MILLISECONDS);
        httpClientBuilder.addInterceptor(new RateLimitInterceptor());

        return new Retrofit.Builder()
                .baseUrl(userService.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(userDetailsGson()))
                .client(httpClientBuilder.build())
                .build()
                .create(UserService.class);
    }

    @Data
    static class RetrofitService {
        private String baseUrl;
        private Integer connTimeout;
        private Integer readTimeout;
    }
}
