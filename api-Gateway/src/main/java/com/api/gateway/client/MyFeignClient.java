package com.api.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Han
 * @data 2024/3/15
 * @apiNode
 */
@FeignClient(name = "apiCallBackEnd",url = "localhost:7529/api/userUserInterfaceInfo")
public interface MyFeignClient {
    @GetMapping("/invokeCount")
    boolean invokeCount(@RequestParam("interfaceInfoId") long interfaceInfoId,
                        @RequestParam("userId") long userId);
}
