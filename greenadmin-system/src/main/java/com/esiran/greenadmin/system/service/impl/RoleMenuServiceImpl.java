package com.esiran.greenadmin.system.service.impl;

import com.esiran.greenadmin.system.entity.RoleMenu;
import com.esiran.greenadmin.system.mapper.RoleMenuMapper;
import com.esiran.greenadmin.system.service.IRoleMenuService;
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
