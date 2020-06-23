package com.esiran.greenpay.admin.controller.system.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.entity.UserRole;
import com.esiran.greenpay.system.entity.dot.UserDTO;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.service.IMenuService;
import com.esiran.greenpay.system.service.IRoleService;
import com.esiran.greenpay.system.service.IUserRoleService;
import com.esiran.greenpay.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/v1/system/users")
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


    @ApiOperation("查询所有用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页个数", defaultValue = "10")
    })

    @GetMapping
    public IPage<UserDTO> list(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size ,
            UserDTO userDTO) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isEmpty(userDTO.getUsername())) {
            userLambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());
        }
        Page<User> page = userService.page(new Page<>(current, size),userLambdaQueryWrapper);

        List<User> records = page.getRecords();
        User loginUser = theUser();
        if (records.contains(loginUser)) {
            records.remove(loginUser);
        }

        IPage<UserDTO> convert = page.convert(item -> modelMapper.map(item, UserDTO.class));

        for (UserDTO user : convert.getRecords()) {
            List<UserRole> userRoles = iUserRoleService.selectUserRoleById(user.getId());
            List<Integer> collect = userRoles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                List<Role> roles = iRoleService.selectByIds(collect);
                String roleNames ="";
                for (int i = 0; i < roles.size(); i++) {
                    if (i != 0) {
                        roleNames += ",";
                    }
                    roleNames += roles.get(i).getName();
                }
                user.setRoleNames(roleNames);
            }

        }

        return convert;
    }


    @DeleteMapping("/del")
    public boolean del(@RequestParam Integer id) throws PostResourceException {
        if (id <= 0) {
            throw new PostResourceException("用户ID不正确");
        }
        UserDTO userDTO = userService.selectUserById(id);
        if (userDTO == null) {
            throw new PostResourceException("用户不存在");
        }

        iUserRoleService.removeUserRoleYyUserId(id);
        return true;
    }


    @PostMapping
    public ResponseEntity add(@Valid UserInputDto userInputDto) throws APIException {

        iUserRoleService.addUserAndRole(userInputDto);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }


}
