package com.esiran.greenadmin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.RoleMenu;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserRoleInputDto;
import com.esiran.greenadmin.system.entity.vo.MenuTreeVo;
import com.esiran.greenadmin.system.mapper.RoleMapper;
import com.esiran.greenadmin.system.service.IMenuService;
import com.esiran.greenadmin.system.service.IRoleMenuService;
import com.esiran.greenadmin.system.service.IRoleService;
import com.esiran.greenadmin.system.service.IUserRoleService;
import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private final IUserRoleService userRoleService;

    private final IMenuService menuService;


    public RoleServiceImpl(IRoleMenuService iRoleMenuService, IUserRoleService userRoleService, IMenuService menuService) {
        this.iRoleMenuService = iRoleMenuService;
        this.userRoleService = userRoleService;
        this.menuService = menuService;
    }


    @Override
    public Role selectById(Integer id) throws PostResourceException {
        if (id <= 0) {
            throw new PostResourceException("角色ID不正确");
        }
        return this.baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, APIException.class})
    public boolean save(UserRoleInputDto roleDto) throws APIException {
        if (StringUtils.isBlank(roleDto.getName())) {
            throw new APIException("角色名称不能为空", String.valueOf(HttpStatus.BAD_REQUEST));
        }
        if (StringUtils.isBlank(roleDto.getRoleCode())) {
            throw new APIException("角色代码不能为空", String.valueOf(HttpStatus.BAD_REQUEST));
        }
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getName, roleDto.getName());
        Role odl = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (odl != null) {
            throw new APIException("角色已经存在", String.valueOf(HttpStatus.BAD_REQUEST));
        }
        //更新角色信息
        Role role = modelMapper.map(roleDto, Role.class);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(role.getCreatedAt());
        boolean save = save(role);
        if (roleDto.getPermIds() != null && roleDto.getPermIds().length() > 0) {

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
    public void updateRoleById(UserRoleInputDto roleDto) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Role role = modelMapper.map(roleDto,Role.class);
        role.setUpdatedAt(LocalDateTime.now());
        this.updateById(role);
        String pIds = roleDto.getPermissionIds();
        if (!StringUtils.isBlank(pIds)){
            List<RoleMenu> rms = Arrays.stream(pIds.split(","))
                    .map(item->{
                        RoleMenu rm = new RoleMenu();
                        rm.setRoleId(role.getId());
                        rm.setMenuId(Integer.parseInt(item));
                        rm.setCreatedAt(LocalDateTime.now());
                        rm.setUpdatedAt(LocalDateTime.now());
                        return rm;
                    })
                    .collect(Collectors.toList());
            iRoleMenuService.remove(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId,role.getId()));
            iRoleMenuService.saveBatch(rms);
        }else {
            iRoleMenuService.remove(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId,role.getId()));
        }
    }

    @Override
    public List<Role> selectByIds(List<Integer> ids) {
        List<Role> roles = this.listByIds(ids);
        return roles;
    }

    @Override
    public List<MenuTreeVo> getMenuListByUser(Integer userId) {
        List<UserRole> userRoles = userRoleService.selectUserRoleById(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return null;
        }

        //确定用户菜单
        userRoles.stream().distinct();
        List<MenuTreeVo> treeVos = new ArrayList<>();
        userRoles.forEach(item -> {
            List<MenuTreeVo> menuTreeByRoleId = menuService.getMenuTreeByRoleId(item.getRoleId());
            if (!CollectionUtils.isEmpty(menuTreeByRoleId)) {
                treeVos.addAll(menuTreeByRoleId);
            }
        });
        //防止用户多角色菜单重复问题
        List<MenuTreeVo> collect = treeVos.stream().distinct().collect(Collectors.toList());
        collect.sort(Comparator.comparing(MenuTreeVo::getSorts).reversed());
        //对子菜单进行排序
        collect.forEach(menuTreeVo -> {
            List<MenuTreeVo> childrens = menuTreeVo.getChildren();
            childrens.sort(Comparator.comparing(MenuTreeVo::getSorts).reversed());
        });
        return collect;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class, ApiException.class})
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
