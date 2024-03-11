package com.api.interfaces;


import com.api.sdk.client.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest

class ApiInterfaceApplicationTests {

    @Resource
    private ApiClient apiClient;
    @Test
    void contextLoads() {
        String usernameByPost = apiClient.getNameByPost("nihao");
        System.out.println("测试里的结果："+usernameByPost);
    }

}
