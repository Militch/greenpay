package com.esiran.greenadmin.admin.controller.system.user;

/**
 * @author han
 * @Package com.esiran.greenpay.admin.controller.system.user
 * @date 2020/5/18 12:30
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.esiran.greenadmin.admin.controller.CURDBaseController;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.framework.annotation.PageViewHandleError;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserUpdateDto;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/system/user")
public class AdminSystemUserController extends CURDBaseController {
    private final IUserRoleService userRoleService;

    private final IUserService userService;

    public AdminSystemUserController(IUserRoleService userRoleService, IUserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    @GetMapping("/useropent")
    public String userOpent(){
        return render("system/user/useropent");
    }

    @GetMapping("/list")
    public String index() {
        return render("system/user/listCopy");
    }


    @GetMapping("/list/{userId}/edit")
    @PageViewHandleError
    public String edit( ModelMap modelMap, @PathVariable Integer userId) throws PostResourceException {
        UserDTO user = userService.selectUserById(userId);
        List<UserRole> userRoles = userRoleService.selectUserRoleById(userId);
        List<Integer> collect = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("userRoles", collect);
        return render("system/user/edit");
    }


    @PostMapping("/list/{userId}/edit")
    @PageViewHandleError
    public String edit(@PathVariable Integer userId,@Valid UserUpdateDto userUpdateDto) throws APIException {
        userUpdateDto.setId(userId);
        return redirect("/system/user/list");
    }


    @GetMapping("/list/add")
    public String add() {
        return render("system/user/add");
    }


}
