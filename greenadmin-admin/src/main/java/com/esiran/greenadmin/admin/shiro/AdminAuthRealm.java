package com.esiran.greenadmin.admin.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.service.IRoleService;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("authorizer")
public class AdminAuthRealm extends AuthorizingRealm {
    private final IUserService userService;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;


    public AdminAuthRealm(IUserService userService, IUserRoleService userRoleService, IRoleService roleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principalCollection.getPrimaryPrincipal();
        List<UserRole> userRoles = userRoleService.selectUserRoleById(user.getId());
        List<Role> roles = roleService.listByIds(userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(item ->{
                simpleAuthorizationInfo.addRole(item.getName());
                simpleAuthorizationInfo.addStringPermission(item.getRoleCode());
            });
        }
        return simpleAuthorizationInfo;
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(user,user.getPassword(),null,getName());
    }
}
