package com.esiran.greenpay.system.service;

import com.esiran.greenpay.system.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IRoleMenuService extends IService<RoleMenu> {


    public List<RoleMenu> selectRleMenusByRoleId(Integer roleId);

    List<RoleMenu> getRoleMenusByRoleId(Integer roleId);
}
