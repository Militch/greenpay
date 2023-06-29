package com.esiran.greenpay.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.esiran.greenpay.system.entity.RoleMenu;
import com.esiran.greenpay.system.mapper.RoleMenuMapper;
import com.esiran.greenpay.system.service.IRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Override
    public List<RoleMenu> selectRleMenusByRoleId(Integer roleId) {
//        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(RoleMenu::getRoleId, roleId);
//        LambdaQueryWrapper<RoleMenu> qw = Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getRoleId, roleId).and(u -> u.lt(RoleMenu::get, 40).or().isNotNull(RoleMenu::getEmail));
        List<RoleMenu> list = this.baseMapper.selectRleMenusByRoleId(roleId);

        return list;
    }

    @Override
    public List<RoleMenu> getRoleMenusByRoleId(Integer roleId) {

        List<RoleMenu> roleMenus = this.baseMapper.getRoleMenusByRoleId(roleId);
        return roleMenus;
    }
}
