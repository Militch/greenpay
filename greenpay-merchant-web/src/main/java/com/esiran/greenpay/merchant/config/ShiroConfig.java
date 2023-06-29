package com.esiran.greenpay.merchant.config;

import com.esiran.greenpay.merchant.shiro.MerchantAuthRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    private final MerchantAuthRealm realm;

    public ShiroConfig(MerchantAuthRealm realm) {
        this.realm = realm;
    }


    @Bean("simpleCookie")
    public SimpleCookie cookie(){
        SimpleCookie cookie = new SimpleCookie(
                ShiroHttpSession.DEFAULT_SESSION_ID_NAME.concat("_MERCHANT"));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }
    @Bean("sessionManager")
    public SessionManager sessionManager(SimpleCookie cookie){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager  securityManager(SessionManager sessionManager){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/css/**","anon");
        chainDefinition.addPathDefinition("/font/**","anon");
        chainDefinition.addPathDefinition("/layui/**","anon");
        chainDefinition.addPathDefinition("/simditor/**","anon");
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        chainDefinition.addPathDefinition("/**", "authc");


        return chainDefinition;
    }
}
