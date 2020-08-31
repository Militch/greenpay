package com.esiran.greenadmin.system.mapper;

import com.esiran.greenadmin.system.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 系统角色 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> selectRolesByUserId(Integer userId);
}
