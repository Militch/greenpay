package com.esiran.greenpay.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.RoleMenu;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import com.esiran.greenpay.system.mapper.RoleMapper;
import com.esiran.greenpay.system.service.IRoleMenuService;
import com.esiran.greenpay.system.service.IRoleService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 系统角色 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private static final ModelMapper modelMapper = new ModelMapper();

    private IRoleMenuService iRoleMenuService;

    public RoleServiceImpl(IRoleMenuService iRoleMenuService) {
        this.iRoleMenuService = iRoleMenuService;
    }


    @Override
    public Role selectById(Long id) throws PostResourceException {
        if (id <= 0) {
            throw new PostResourceException("角色ID不正确");
        }
       return this.baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean save(UserRoleInputDto roleDto) throws PostResourceException {
        if (StringUtils.isBlank(roleDto.getName())) {
            throw new PostResourceException("角色名称不能为空");
        }
        if (StringUtils.isBlank(roleDto.getRoleCode())) {
            throw new PostResourceException("角色代码不能为空");
        }
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getName,roleDto.getName());
        Role odl = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (odl != null) {
            throw new PostResourceException("角色已经存在");
        }
        //更新角色信息
        Role role = modelMapper.map(roleDto, Role.class);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(role.getCreatedAt());
        boolean save = save(role);
        String[] split = roleDto.getPermIds().split(",");
        //插入角色权限菜单
        RoleMenu roleMenu = new RoleMenu();
        for (String s : split) {
            Integer value = Integer.valueOf(s);
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(value);
            roleMenu.setCreatedAt(LocalDateTime.now());
            roleMenu.setUpdatedAt(roleMenu.getCreatedAt());
            iRoleMenuService.save(roleMenu);
        }

        return true;
    }

    @Override
    public boolean edit(UserRoleInputDto roleDto) throws PostResourceException {
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(Role::getName, roleDto.getName());
        Role role = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (role == null) {
            throw new PostResourceException("未查询到信息");
        }
        //更新角色信息
        role.setRoleCode(roleDto.getRoleCode());
        role.setUpdatedAt(LocalDateTime.now());

        return updateById(role);
    }

    @Override
    public List<Role>  selectByIds(List<Integer> ids) {
        List<Role> roles = this.listByIds(ids);
        return roles;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class,ApiException.class})
    public boolean updateUserRole(UserRoleInputDto userRoleDto) throws ApiException {

        Role newRole = modelMapper.map(userRoleDto, Role.class);

        //得到新的权限
        String permIds = userRoleDto.getPermIds();
        String[] split = permIds.split(",");
        //删除已有的权限
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", newRole.getId());
        iRoleMenuService.remove(queryWrapper);
        //插入新的权限
        RoleMenu roleMenu = new RoleMenu();
        for (String s : split) {
            Integer id = Integer.valueOf(s);
            roleMenu.setRoleId(newRole.getId());
            roleMenu.setMenuId(id);
            iRoleMenuService.save(roleMenu);
        }
        //更新角色
        this.updateById(newRole);
        return false;
    }
}
