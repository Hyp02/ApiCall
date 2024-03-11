package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.project.model.pojo.UserInterfaceInfo;
import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Han
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2024-03-11 16:38:41
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b) {
        // 校验前端传来的参数
        if (userInterfaceInfo == null || userInterfaceInfo.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        if (b) {
            Long id = userInterfaceInfo.getId();
            Long userId = userInterfaceInfo.getUserId();
            Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
            Integer totalNum = userInterfaceInfo.getTotalNum();
            Integer leftNum = userInterfaceInfo.getLeftNum();
            Integer status = userInterfaceInfo.getStatus();
            Date createTime = userInterfaceInfo.getCreateTime();
            Date updateTime = userInterfaceInfo.getUpdateTime();
            Integer isDelete = userInterfaceInfo.getIsDelete();
            // 创建时，所有参数必须非空
            if (ObjectUtils.anyNull(id, status, userId, createTime, updateTime, isDelete, interfaceInfoId,
                    totalNum, leftNum)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // TODO


    }

    /**
     * 接口调用次数统计
     * 在某个接口被调用后执行
     * @param interfaceInfoId 接口id
     * @param userId          用户id
     * @return
     */
    @Override
    // 考虑并发问题，多线程下可能会导致接口剩余调用次数小于零的情况 加锁
    public synchronized boolean invokeCount(long interfaceInfoId, long userId) {
        // 校验
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 统计次数
        //   1.减少剩余量
        //   2.增加调用次数
        UpdateWrapper<UserInterfaceInfo> wrapper = new UpdateWrapper<>();
        // 定位接口
        wrapper.eq("interfaceInfoId", interfaceInfoId);
        wrapper.eq("userId", userId);
        // 只修改leftNum大于零的记录
        wrapper.gt("leftNum", 0);
        // 修改次数（totalNum + 1 ，leftNum -1）
        wrapper.setSql("totalNum = totalNum + 1,leftNum = leftNum -1");
        return this.update(wrapper);

    }
}




