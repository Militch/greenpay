package com.esiran.greenpay.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.system.entity.UserRole;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统用户角色 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    IPage<UserRoleInputDto> selectRole(IPage<UserRole> userVoPage, @Param(Constants.WRAPPER) Wrapper<UserRole> wrapper);

    UserRoleInputDto selectRolesById(@Param(Constants.WRAPPER) Wrapper<UserRole> wrapper);
}
