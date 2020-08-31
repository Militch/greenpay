package com.esiran.greenadmin.admin.controller.system.user;

/**
 * @author han
 * @Package com.esiran.greenpay.admin.controller.system.user
 * @date 2020/5/18 12:30
 */

import com.esiran.greenadmin.admin.controller.CURDBaseController;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.framework.annotation.PageViewHandleError;
import com.esiran.greenadmin.system.entity.RoleSelectVo;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserUpdateDto;
import com.esiran.greenadmin.system.service.IRoleService;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/system/user")
public class AdminSystemUserController extends CURDBaseController {
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;
    private final IUserService userService;

    public AdminSystemUserController(IUserRoleService userRoleService, IRoleService roleService, IUserService userService) {
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/useropent")
    public String userOpent(){
        return render("system/user/useropent");
    }

    @GetMapping("/list")
    @RequiresPermissions("system_user_view")
    public String index() {
        return render("system/user/list");
    }


    @GetMapping("/list/{userId}/edit")
    @PageViewHandleError
    @RequiresPermissions("system_user_update")
    public String edit( ModelMap modelMap, @PathVariable Integer userId) throws PostResourceException {
        UserDTO user = userService.selectUserById(userId);
        List<UserRole> userRoles = userRoleService.selectUserRoleById(userId);
        List<Integer> collect = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        modelMap.addAttribute("roles", roleService.listAll());
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("userRoles", collect);
        return render("system/user/edit");
    }


    @PostMapping("/list/{userId}/edit")
    @PageViewHandleError
    @RequiresPermissions("system_user_update")
    public String edit(@PathVariable Integer userId,@Valid UserUpdateDto userUpdateDto) throws APIException {
        userUpdateDto.setId(userId);
        return redirect("/system/user/list");
    }


    @GetMapping("/list/add")
    @RequiresPermissions("system_user_add")
    @PageViewHandleError
    public String add(ModelMap modelMap) {
        modelMap.addAttribute("roles", roleService.listAll());
        return render("system/user/add");
    }


}
