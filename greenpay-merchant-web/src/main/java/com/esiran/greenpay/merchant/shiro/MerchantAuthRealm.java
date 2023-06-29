package com.esiran.greenpay.merchant.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.system.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component("authorizer")
public class MerchantAuthRealm extends AuthorizingRealm {
    private final IMerchantService merchantService;

    public MerchantAuthRealm(IMerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getUsername,username);
        Merchant merchant = merchantService.getOne(queryWrapper);
        if (merchant == null)
            throw new UnknownAccountException();
        return new SimpleAuthenticationInfo(merchant,merchant.getPassword(),null,getName());
    }
}
