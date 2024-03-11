package com.yupi.project.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Han
 * @data 2024/3/11
 * @apiNode
 */
@SpringBootTest
public class UserInterfaceInfo {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Test
    void testInvokeCount() {
        boolean count = userInterfaceInfoService.invokeCount(1L, 1L);
        Assertions.assertTrue(count);
    }
}
