package com.esiran.greenpay.admin.config;

import com.esiran.greenpay.admin.shiro.AdminAuthRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private final AdminAuthRealm realm;

    public ShiroConfig(AdminAuthRealm realm) {
        this.realm = realm;
    }


    @Bean("simpleCookie")
    public SimpleCookie cookie() {
        SimpleCookie cookie = new SimpleCookie(
                ShiroHttpSession.DEFAULT_SESSION_ID_NAME.concat("_ADMIN"));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }

    @Bean("sessionManager")
    public SessionManager sessionManager(SimpleCookie cookie) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

//    @Bean
//    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
//        chainDefinition.addPathDefinition("/css/**", "anon");
//        chainDefinition.addPathDefinition("/font/**", "anon");
//        chainDefinition.addPathDefinition("/layui/**", "anon");
//        chainDefinition.addPathDefinition("/simditor/**", "anon");
//        chainDefinition.addPathDefinition("/favicon.ico", "anon");
//        chainDefinition.addPathDefinition("/**", "authc");
//        return chainDefinition;
//    }

//    @Bean( name="shiroFilterFactoryBean")
//    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        //拦截器.
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
//        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
//        filterChainDefinitionMap.put("/logout", "anon");
////        filterChainDefinitionMap.put("/afterlogout", "anon");
//        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
//        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
//        filterChainDefinitionMap.put("/css/**", "anon");
//        filterChainDefinitionMap.put("/font/**", "anon");
//        filterChainDefinitionMap.put("/layui/**", "anon");
//        filterChainDefinitionMap.put("/simditor/**", "anon");
//        filterChainDefinitionMap.put("/favicon.ico", "anon");
//        // filterChainDefinitionMap.put("/html/**","anon");
//        filterChainDefinitionMap.put("/**", "authc");
//        //未授权界面;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
////        shiroFilterFactoryBean.setLoginUrl("/unauth");
//        // 登录成功后要跳转的链接
//        //shiroFilterFactoryBean.setSuccessUrl("/index");
//        shiroFilterFactoryBean.setLoginUrl("/login");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//        return shiroFilterFactoryBean;
//    }
//
//
//    /**
//     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
//     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
//     *
//     * @return
//     */
//    @Bean
//    @DependsOn({"lifecycleBeanPostProcessor"})
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(true);
//        return advisorAutoProxyCreator;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager(sessionManager(cookie())));
//        return authorizationAttributeSourceAdvisor;
//    }
}
