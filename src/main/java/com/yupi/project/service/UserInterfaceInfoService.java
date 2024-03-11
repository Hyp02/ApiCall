package com.yupi.project.service;

import com.yupi.project.model.pojo.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Han
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-03-11 16:38:41
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 数据校验
     * @param userInterfaceInfo
     * @param b 是否需要保证每个参数都不为null
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);

    boolean invokeCount(long interfaceInfoId,long userId);
}
