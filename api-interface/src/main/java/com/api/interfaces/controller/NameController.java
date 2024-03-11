package com.api.interfaces.controller;

import cn.hutool.core.util.StrUtil;
import com.api.sdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Han
 * @data 2024/2/21
 * @apiNode
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(@RequestParam("name") String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam("name") String name, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        if (StrUtil.isBlank(accessKey) || !accessKey.equals("hyp")) {
            return "抱歉，您无权限访问";
        }
        return "POST 你的名字是" + name + " 请求头是" + accessKey;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        // todo 修改为从数据库中查出用户的ak sk来判断

        if (!request.getHeader("accessKey").equals("hyp")) {
            return "抱歉，您无权限访问";
        }
        return "POST 用户名称是" + user.getName();
    }
    // 解密
    //private String checkSign(HttpServletRequest request) {
    //    String accessKey = request.getHeader("accessKey");
    //    String body = request.getHeader("body");
    //    SignUtils.genSign(body,secretKey)
    //    Digester md5 = new Digester(DigestAlgorithm.SHA256);
    //    String content = body + "." + secretKey;
    //    return md5.digestHex(content);
    //}
}
