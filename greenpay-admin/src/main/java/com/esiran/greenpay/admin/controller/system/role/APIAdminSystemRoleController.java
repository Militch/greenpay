package com.esiran.greenpay.admin.controller.system.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.RoleMenu;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import com.esiran.greenpay.system.service.IRoleMenuService;
import com.esiran.greenpay.system.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author han
 */

@RestController
@RequestMapping("/admin/api/v1/system/roles")
@Api(tags = "角色管理")
public class APIAdminSystemRoleController {
    private static ModelMapper modelMapper = new ModelMapper();

    private final IRoleService roleService;

    private final IRoleMenuService roleMenuService;

    public APIAdminSystemRoleController(IRoleService roleService, IRoleMenuService roleMenuService) {
        this.roleService = roleService;
        this.roleMenuService = roleMenuService;
    }

    @ApiOperation("查询所有的用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页码",defaultValue = "1"),
            @ApiImplicitParam(name = "size",value = "每页个数",defaultValue = "10")
    })

    @GetMapping
    public IPage<Role> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false,defaultValue = "10") Integer size){

        return roleService.page(new Page<>(current,size));
    }

    @ApiOperation("更新用户角色")
    @PutMapping
//    @Transactional
    public boolean upRole(@Valid UserRoleInputDto userRoleDto) throws Exception{
        Role newRole = modelMapper.map(userRoleDto, Role.class);
        newRole.setRoleCode(userRoleDto.getPermIds());
        //得到新的权限
        String permIds = userRoleDto.getPermIds();
        String[] split = permIds.split(",");
        //删除已有的权限
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", newRole.getId());
        roleMenuService.remove(queryWrapper);
        //插入新的权限
        RoleMenu roleMenu = new RoleMenu();
        for (String s : split) {
            Integer id = Integer.valueOf(s);
            roleMenu.setRoleId(newRole.getId());
            roleMenu.setMenuId(id);
            roleMenuService.save(roleMenu);
        }
        //更新角色
        roleService.updateById(newRole);
        return true;
    }


    @PostMapping("/add")
    public boolean add(@Valid UserRoleInputDto userRoleDto) throws PostResourceException {
        roleService.save(userRoleDto);
        return true;
    }


    @ApiOperation("获取指定ID用户角色")
    @GetMapping("/{id}")
    public UserRoleInputDto get(@PathVariable("id") Long userId) throws Exception{
        Role role = roleService.selectById(userId);
        UserRoleInputDto roleDto = modelMapper.map(role, UserRoleInputDto.class);
        return roleDto;
    }

    @ApiOperation("删除指定ID用户角色")
    @DeleteMapping("/del")
//    @Transactional
    public boolean del(@RequestParam Long id) throws PostResourceException {
        if (id==null ||id <= 0) {
            throw new PostResourceException("角色ID不正确");
        }
        Role role = roleService.selectById(id);
        if (role == null) {
            throw new PostResourceException("角色不存在");
        }
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", role.getId());
        roleMenuService.remove(queryWrapper);

        return roleService.removeById(id);
    }

}
