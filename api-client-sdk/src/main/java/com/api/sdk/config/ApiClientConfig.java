package com.api.sdk.config;

import com.api.sdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author Han
 * @data 2024/2/28
 * @apiNode
 */
@Component
@ConfigurationProperties("api.client")
@Data
@ComponentScan
public class ApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public ApiClient apiClient() {
        return new ApiClient(accessKey, secretKey);
    }
}
