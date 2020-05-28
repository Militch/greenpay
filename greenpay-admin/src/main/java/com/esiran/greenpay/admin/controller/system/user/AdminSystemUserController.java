package com.esiran.greenpay.admin.controller.system.user;

/**
 * @author han
 * @Package com.esiran.greenpay.admin.controller.system.user
 * @date 2020/5/18 12:30
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.system.entity.dot.UserDTO;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.service.IUserRoleService;
import com.esiran.greenpay.system.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/system/user")
public class AdminSystemUserController {
    private final IUserRoleService userRoleService;

    private final IUserService userService;

    public AdminSystemUserController(IUserRoleService userRoleService, IUserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public ModelAndView index() {
        return new ModelAndView("admin/system/user/list");
    }


    @GetMapping("/list/{userId}/edit")
    @PageViewHandleError
    public String edit( ModelMap modelMap, @PathVariable Integer userId) throws PostResourceException {
        UserDTO user = userService.selectUserById(userId);
        modelMap.addAttribute("user", user);
        return "admin/system/user/edit";
    }


    @PostMapping("/list/{userId}/edit")
    public String edit(@PathVariable Integer userId,@Valid UserInputDto userInputDto) throws PostResourceException {

        if (StringUtils.isBlank(userInputDto.getUsername()) ||
                userInputDto.getUsername().length()<2) {

            throw new PostResourceException("用户名格式不正确");
        }
        if (StringUtils.isBlank(userInputDto.getEmail())) {
            throw new PostResourceException("用户名或Email为空");
        }
        if (StringUtils.isBlank(userInputDto.getPassword()) || userInputDto.getPassword().length()<6) {
            throw new PostResourceException("用户名密码至少6位");
        }

        if (StringUtils.isBlank(userInputDto.getRoleIds())) {
            throw new PostResourceException("未选择角色权限");
        }

        userRoleService.updateUserAndRoles(userId,userInputDto);

        return "redirect:/admin/system/user/list";
    }


    @GetMapping("/add")
    @PageViewHandleError
    public String add() {
        return "admin/system/user/add";
    }


}
