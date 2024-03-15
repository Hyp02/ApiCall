package com.api.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Han
 * @description 针对表【interface_info(接口信息)】的数据库操作Mapper
 * @createDate 2024-03-14 20:58:38
 * @Entity com.api.gateway.model.pojo.InterfaceInfo
 */
@Repository
public interface InterfaceInfoMapper {
    @Select("select id from interface_info where url = #{url}")
    Long getInterfaceIdByUrl(@Param("url") String url);
}




