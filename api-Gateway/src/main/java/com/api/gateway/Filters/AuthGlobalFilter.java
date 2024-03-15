package com.api.gateway.Filters;

import cn.hutool.core.util.StrUtil;
import com.api.gateway.client.MyFeignClient;
import com.api.gateway.mapper.InterfaceInfoMapper;
import com.api.gateway.mapper.UserMapper;
import com.api.sdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author Han
 * @data 2024/3/14
 * @apiNode
 */
@Component
@Slf4j
public class AuthGlobalFilter implements WebFilter, Ordered {

    private static final String API_HOST = "http://localhost:8082";
    @Resource
    private UserMapper userMapper;
    @Resource
    private MyFeignClient myFeignClient;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 在这里进行用户的权限校验
        ServerHttpRequest requestInfo = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 请求路径
        String path = API_HOST + requestInfo.getPath();
        // 请求头信息
        HttpHeaders headers = requestInfo.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String body = headers.getFirst("body");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // accessKye和sign一定不能为null
        if (StrUtil.isBlank(accessKey) || StrUtil.isBlank(sign)) {
            return handleNoAuth(response);
        }
        // 1. 请求日志
        log.info("请求路径：{}", path);
        // 请求头信息校验
        // 2. 用户请求参数校验 todo secretKey和accessKey从数据库中取(使用OpenFeign)
        Long userId = userMapper.getUserIdByAk(accessKey);
        if (userId <= 0) {
            return handleNoAuth(response);
        }
        // 根据userId将accessKey查询出来
        String akByToDB = userMapper.getAkByUserId(userId);
        if (StrUtil.isBlank(akByToDB)) {
            return handleNoAuth(response);
        }
        // 根据ak查出来sk
        String skByDB = userMapper.getSkByAk(akByToDB);
        if (StrUtil.isBlank(skByDB)) {
            return handleNoAuth(response);
        }
        String signCheck = SignUtils.genSign(body, skByDB);
        // 检查两次加密是否相同
        if (!signCheck.equals(sign) || !accessKey.equals(akByToDB)) {
            return handleNoAuth(response);
        }
        // 随机数校验nonce
        if (nonce != null) {
            if (nonce.length() < 4) {
                return handleNoAuth(response);
            }
        }
        // 是否过期
        long currentTime = System.currentTimeMillis() / 1000;
        if (timestamp != null) {
            final long FIVE_MINUTES = 60 * 5;
            if (currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
                return handleNoAuth(response);
            }
        }
        // 3. 用户权限
        // 4. 接口是否存在 todo 从数据库中查出url与path对比
        Long interfaceId = interfaceInfoMapper.getInterfaceIdByUrl(path);
        if (interfaceId == null || interfaceId <= 0) {
            return handleError(response);
        }
        // 转发请求
        Mono<Void> filter = chain.filter(exchange);
        // 异步调用
        // 5. 接口调用成功调用次数+1 todo 使用OpenFeign远程调用
        if (response.getStatusCode() == HttpStatus.OK) {
            boolean isSuccess = myFeignClient.invokeCount(interfaceId, userId);
            log.info("减少接口调用次数{}", isSuccess);
        }
        // 6. 返回响应信息
        log.info("响应信息：{}", response.getStatusCode());
        // 接口调用失败，返回错误码
        if (response.getStatusCode() != HttpStatus.OK) {
            return handleError(response);
        }
        // 7. 响应日志
        return filter;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        // 无权限
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();

    }

    private Mono<Void> handleError(ServerHttpResponse response) {

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }


}
