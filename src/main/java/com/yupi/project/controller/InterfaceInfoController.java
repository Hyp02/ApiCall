package com.yupi.project.controller;

import cn.hutool.json.JSONUtil;
import com.api.sdk.client.ApiClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.project.annotation.AuthCheck;
import com.yupi.project.common.BaseResponse;
import com.yupi.project.common.DeleteRequest;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.common.ResultUtils;
import com.yupi.project.constant.CommonConstant;
import com.yupi.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.model.dto.interfaceinfo.*;
import com.yupi.project.model.enums.InterfaceState;
import com.yupi.project.model.pojo.InterfaceInfo;
import com.yupi.project.model.pojo.User;
import com.yupi.project.service.InterfaceInfoService;
import com.yupi.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 接口管理
 *
 * @author yupi
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceinfoService;

    @Resource
    private UserService userService;

    @Resource
    private ApiClient apiClient;
    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceinfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceinfoAddRequest,
                                               HttpServletRequest request) {
        if (interfaceinfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoAddRequest, interfaceinfo);
        // 校验
        interfaceinfoService.validInterfaceInfo(interfaceinfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceinfo.setUserId(loginUser.getId());
        boolean result = interfaceinfoService.save(interfaceinfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceinfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest,
                                                     HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceinfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceinfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceinfoUpdateRequest == null || interfaceinfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoUpdateRequest, interfaceinfo);
        // 参数校验
        interfaceinfoService.validInterfaceInfo(interfaceinfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceinfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceinfoService.updateById(interfaceinfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = interfaceinfoService.getById(id);
        return ResultUtils.success(interfaceinfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceinfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceinfoQueryRequest) {
        InterfaceInfo interfaceinfoQuery = new InterfaceInfo();
        if (interfaceinfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceinfoQueryRequest, interfaceinfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceinfoQuery);
        List<InterfaceInfo> interfaceinfoList = interfaceinfoService.list(queryWrapper);
        return ResultUtils.success(interfaceinfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceinfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceinfoQueryRequest,
                                                                     HttpServletRequest request) {
        if (interfaceinfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoQueryRequest, interfaceinfoQuery);
        long current = interfaceinfoQueryRequest.getCurrent();
        long size = interfaceinfoQueryRequest.getPageSize();
        String sortField = interfaceinfoQueryRequest.getSortField();
        String sortOrder = interfaceinfoQueryRequest.getSortOrder();
        String content = interfaceinfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceinfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceinfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceinfoPage = interfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceinfoPage);
    }

    // endregion

    /**
     * 上线接口
     *
     * @param idRequest 前端封装的接口id
     * @param request
     * @return
     */
    // 管理员权限检查自定义注解
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterface(@RequestBody IdRequest idRequest,
                                                 HttpServletRequest request) {
        // 校验请求参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        // 校验接口是否存在
        InterfaceInfo byId = interfaceinfoService.getById(idRequest.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 校验接口是否可用 todo 修改为要线下的指定接口
        com.api.sdk.model.User user = new com.api.sdk.model.User();
        user.setName("testUserObj");
        String usernameByPost = apiClient.getUsernameByPost(user);
        if (usernameByPost == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口异常");
        }
        // 修改接口状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(idRequest.getId());
        interfaceInfo.setStatus(InterfaceState.ONLINE.getState());
        boolean result = interfaceinfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterface(@RequestBody IdRequest idRequest,
                                                  HttpServletRequest request) {
        // 校验请求参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        // 校验接口是否存在
        InterfaceInfo byId = interfaceinfoService.getById(idRequest.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 修改接口状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(idRequest.getId());
        interfaceInfo.setStatus(InterfaceState.OFFLINE.getState());
        boolean result = interfaceinfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * interfaceInfoInvokRequest
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> testInvokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                  HttpServletRequest request) {
        // 校验请求参数
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        // 校验接口是否存在
        InterfaceInfo interfaceById = interfaceinfoService.getById(interfaceInfoInvokeRequest.getId());
        if (interfaceById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 校验接口状态
        Integer status = interfaceById.getStatus();
        if (status == InterfaceState.OFFLINE.getState()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口已关闭");
        }
        // 调用接口
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        ApiClient apiClient = new ApiClient(accessKey, secretKey);
        // todo 优化为根据地址调用对应方法
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        String usernameByPost = apiClient.getUsernameByPost(JSONUtil.toBean(userRequestParams, com.api.sdk.model.User.class));
        return ResultUtils.success(usernameByPost);
    }


}
