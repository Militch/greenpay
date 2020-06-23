package com.esiran.greenpay.admin.controller.system.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.RoleMenu;
import com.esiran.greenpay.system.entity.dot.MenuDTO;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import com.esiran.greenpay.system.entity.vo.MenuTreeVo;
import com.esiran.greenpay.system.entity.vo.RoleVo;
import com.esiran.greenpay.system.service.IMenuService;
import com.esiran.greenpay.system.service.IRoleMenuService;
import com.esiran.greenpay.system.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author han
 */

@RestController
@RequestMapping("/admin/api/v1/system/roles")
@Api(tags = "角色管理")
public class APIAdminSystemRoleController extends CURDBaseController {
    private static ModelMapper modelMapper = new ModelMapper();

    private final IRoleService roleService;

    private final IRoleMenuService roleMenuService;

    private final IMenuService menuService;

    public APIAdminSystemRoleController(IRoleService roleService, IRoleMenuService roleMenuService, IMenuService menuService) {
        this.roleService = roleService;
        this.roleMenuService = roleMenuService;
        this.menuService = menuService;
    }

    @ApiOperation("查询所有的用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页码",defaultValue = "1"),
            @ApiImplicitParam(name = "size",value = "每页个数",defaultValue = "10")
    })

    @GetMapping
    public IPage<Role> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false,defaultValue = "10") Integer size, RoleVo roleVo){
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isEmpty(roleVo.getName())) {
            lambdaQueryWrapper.eq(Role::getName,roleVo.getName());
        }
        return roleService.page(new Page<>(current,size),lambdaQueryWrapper);
    }

    @ApiOperation("更新用户角色")
    @PutMapping

    public boolean upRole(@Valid UserRoleInputDto userRoleDto) throws ApiException {

        roleService.updateUserRole(userRoleDto);

        return true;
    }


    @PostMapping("/add")
    @PageViewHandleError
    public boolean add(@Valid UserRoleInputDto userRoleDto) throws APIException {
        roleService.save(userRoleDto);
        return true;
    }


    @ApiOperation("获取指定ID用户角色")
    @GetMapping("/{id}")
    public UserRoleInputDto get(@PathVariable("id") Integer userId) throws Exception{
        Role role = roleService.selectById(userId);
        UserRoleInputDto roleDto = modelMapper.map(role, UserRoleInputDto.class);
        return roleDto;
    }

    @ApiOperation("删除指定ID用户角色")
    @DeleteMapping("/del")

    public boolean del(@RequestParam Integer id) throws PostResourceException {
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

    @GetMapping("/ofUser")
    public ResponseEntity<List<MenuTreeVo>> getUserMenus() {

        List<MenuTreeVo> menuTreeVos = roleService.getMenuListByUser(theUser().getId());

        return ResponseEntity.ok(menuTreeVos);
    }

}
