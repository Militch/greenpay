package com.esiran.greenadmin.admin.controller.system.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esiran.greenadmin.admin.controller.CURDBaseController;
import com.esiran.greenadmin.common.entity.APIError;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.framework.annotation.PageViewHandleError;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.RoleMenu;
import com.esiran.greenadmin.system.entity.dot.UserRoleInputDto;
import com.esiran.greenadmin.system.service.IRoleMenuService;
import com.esiran.greenadmin.system.service.IRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/system/role")
public class AdminSystemRoleController extends CURDBaseController {
    private final IRoleService roleService;
    private final IRoleMenuService iRoleMenuService;

    public AdminSystemRoleController(IRoleService roleService, IRoleMenuService iRoleMenuService) {
        this.roleService = roleService;
        this.iRoleMenuService = iRoleMenuService;
    }

    @GetMapping("/list")
    @RequiresPermissions("system_role_view")
    public String toPage() {
        return render("system/role/list");
    }


    @GetMapping("/list/add")
    @RequiresPermissions("system_role_add")
    public String add(HttpSession httpSession, ModelMap modelMap) {
        return render("system/role/add");
    }


    @GetMapping("/list/edit/{id}")
    @PageViewHandleError
    @RequiresPermissions("system_role_update")
    public String edit(ModelMap modelMap, @PathVariable Integer id) throws PostResourceException {
        Role role = roleService.selectById(id);
        if (role == null) throw new PostResourceException("未找到角色");
        LambdaQueryWrapper<RoleMenu> qw = new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId,id);
        List<RoleMenu> roleMenus = iRoleMenuService.list(qw);
        List<Integer> permissionIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        modelMap.addAttribute("role", role);
        modelMap.addAttribute("permissionIds", permissionIds);
        return render("system/role/edit");
    }

    @PostMapping("/list/edit/{id}")
    @RequiresPermissions("system_role_update")
    @PageViewHandleError
    public String update(@PathVariable Integer id, @Valid UserRoleInputDto roleDto) throws PostResourceException {
        roleDto.setId(id);
        roleService.updateRoleById(roleDto);
        return redirect("/system/role/list");
    }



}
