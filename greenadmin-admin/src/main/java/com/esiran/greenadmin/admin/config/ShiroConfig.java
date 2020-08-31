package com.esiran.greenadmin.admin.config;

import com.esiran.greenadmin.admin.shiro.AdminAuthRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShiroConfig {

    private final AdminAuthRealm realm;

    public ShiroConfig(AdminAuthRealm realm) {
        this.realm = realm;
    }


    @Bean("simpleCookie")
    public SimpleCookie cookie(){
        SimpleCookie cookie = new SimpleCookie(
                ShiroHttpSession.DEFAULT_SESSION_ID_NAME.concat("_ADMIN"));
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
    public DefaultWebSecurityManager securityManager(SessionManager sessionManager){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }


    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/js/**", "anon");
        chainDefinition.addPathDefinition("/font/**", "anon");
        chainDefinition.addPathDefinition("/layui/**", "anon");
        chainDefinition.addPathDefinition("/simditor/**", "anon");
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
}
