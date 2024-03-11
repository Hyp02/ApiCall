package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.InterfaceInfoMapper;
import com.yupi.project.model.pojo.InterfaceInfo;
import com.yupi.project.service.InterfaceInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Han
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-08-20 18:37:01
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceinfo, boolean add) {

        if (interfaceinfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口信息
        Long id = interfaceinfo.getId();
        String name = interfaceinfo.getName();
        String description = interfaceinfo.getDescription();
        String url = interfaceinfo.getUrl();
        String requestHeader = interfaceinfo.getRequestHeader();
        String responseHeader = interfaceinfo.getResponseHeader();
        Integer status = interfaceinfo.getStatus();
        String method = interfaceinfo.getMethod();
        Long userId = interfaceinfo.getUserId();
        Date createTime = interfaceinfo.getCreateTime();
        Date updateTime = interfaceinfo.getUpdateTime();
        Integer isDelete = interfaceinfo.getIsDelete();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, description, url, requestHeader, responseHeader,
                    method) || ObjectUtils.anyNull(id, status, userId, createTime, updateTime, isDelete)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // api名字过长
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数过长");
        }
        // TODO


    }
}




