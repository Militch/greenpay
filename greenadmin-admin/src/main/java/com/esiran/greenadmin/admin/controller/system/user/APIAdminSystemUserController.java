package com.esiran.greenadmin.admin.controller.system.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.admin.controller.CURDBaseController;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserUpdateDto;
import com.esiran.greenadmin.system.service.IMenuService;
import com.esiran.greenadmin.system.service.IRoleService;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system/users")
@Api(tags = "用户管理")
public class APIAdminSystemUserController extends CURDBaseController {

    private static ModelMapper modelMapper = new ModelMapper();
    private final IUserService userService;
    private final IUserRoleService iUserRoleService;
    private final IMenuService iMenuService;
    private final IRoleService iRoleService;

    public APIAdminSystemUserController(IUserService userService, IUserRoleService iUserRoleService, IMenuService iMenuService, IRoleService iRoleService) {
        this.userService = userService;
        this.iUserRoleService = iUserRoleService;
        this.iMenuService = iMenuService;
        this.iRoleService = iRoleService;
    }


    @PostMapping("/updateUserAndRoles")
    public boolean updateUserAndRoles(@PathParam("userId") Integer userId,@Valid UserInputDto userInputDto) throws APIException {
        if (StringUtils.isBlank(userInputDto.getUsername()) ||
                userInputDto.getUsername().length()<2) {

            throw new APIException("用户名格式不正确","400");
        }
        if (StringUtils.isBlank(userInputDto.getEmail())) {
            throw new APIException("用户名或Email为空","400");
        }
        return true;
    }

    @ApiOperation("查询所有用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页个数", defaultValue = "10")
    })
    @GetMapping
    @RequiresPermissions("system_user_view")
    public IPage<UserDTO> list(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size ,
            UserDTO userDTO) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isEmpty(userDTO.getUsername())) {
            userLambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());
        }
        Page<User> page = userService.page(new Page<>(current, size),userLambdaQueryWrapper);
        IPage<UserDTO> convert = page.convert(item -> modelMapper.map(item, UserDTO.class));
        for (UserDTO user : convert.getRecords()) {
            List<UserRole> userRoles = iUserRoleService.selectUserRoleById(user.getId());
            List<Integer> collect = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                List<Role> roles = iRoleService.selectByIds(collect);
                StringBuilder roleNames = new StringBuilder();
                for (int i = 0; i < roles.size(); i++) {
                    if (i != 0) {
                        roleNames.append(",");
                    }
                    roleNames.append(roles.get(i).getName());
                }
                user.setRoleNames(roleNames.toString());
            }

        }

        return convert;
    }


    @DeleteMapping
    @RequiresPermissions("system_user_del")
    public void del(@RequestParam Integer id) throws PostResourceException {
        userService.removeUserById(id);
    }

    @PostMapping
    @RequiresPermissions("system_user_add")
    public ResponseEntity<UserDTO> add(@Valid UserInputDto userInputDto) throws APIException {
        UserDTO userDTO = userService.addUser(userInputDto);
        return ResponseEntity.ok(userDTO);
    }
    @PostMapping("/{userId}")
    @RequiresPermissions("system_user_update")
    public ResponseEntity<UserDTO> update(@Valid UserUpdateDto userUpdateDto, @PathVariable Integer userId) throws APIException, PostResourceException {
        userUpdateDto.setId(userId);
        UserDTO userDTO = userService.updateUserById(userUpdateDto);
        return ResponseEntity.ok(userDTO);
    }

}
