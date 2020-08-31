package com.esiran.greenadmin.admin.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenadmin.system.entity.Menu;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.service.IMenuService;
import com.esiran.greenadmin.system.service.IRoleService;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Component("authorizer")
public class AdminAuthRealm extends AuthorizingRealm {
    private final IUserService userService;
    private final IRoleService roleService;
    private final IMenuService menuService;
    public AdminAuthRealm(IUserService userService, IRoleService roleService, IMenuService menuService) {
        this.userService = userService;
        this.roleService = roleService;
        this.menuService = menuService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principalCollection.getPrimaryPrincipal();
        List<Menu> menus = menuService.getMenusByUserId(user.getId());
        List<Role> roles = roleService.listByUserId(user.getId());
        simpleAuthorizationInfo.setStringPermissions(
                menus.stream().map(Menu::getMark)
                        .collect(Collectors.toSet()));
        simpleAuthorizationInfo.setRoles(
                roles.stream().map(Role::getRoleCode)
                        .collect(Collectors.toSet()));
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userService.getOne(queryWrapper);
        if (user == null) throw new UnknownAccountException();
        return new SimpleAuthenticationInfo(user,user.getPassword(),null,getName());
    }
}
