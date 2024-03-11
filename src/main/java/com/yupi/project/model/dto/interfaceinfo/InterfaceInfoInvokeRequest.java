package com.yupi.project.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @author Han
 * @data 2024/3/10
 * @apiNode
 */
@Data
public class InterfaceInfoInvokeRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 请求参数
     */
    private String userRequestParams;


}
