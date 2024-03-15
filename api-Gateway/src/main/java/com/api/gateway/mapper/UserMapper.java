package com.api.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Han
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2024-03-14 20:36:27
 * @Entity generator.domain.User
 */
@Repository
public interface UserMapper {

    @Select("select id from user where accessKey = #{accessKey}")
    long getUserIdByAk(@Param("accessKey") String accessKey);

    @Select("select accessKey from user where id = #{userId}")
    String getAkByUserId(@Param("userId") long userId);

    @Select("select secretKey from user where accessKey = #{accessKey}")
    String getSkByAk(@Param("accessKey") String accessKey);
}




