package com.api.sdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 * @author Han
 * @data 2024/2/28
 * @apiNode
 */
public class SignUtils {
    /**
     * 生成签名
     * @param body 请求头拼接的body
     * @param secretKey 秘钥密码
     * @return
     */
    public static String genSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return md5.digestHex(content);
    }
}
