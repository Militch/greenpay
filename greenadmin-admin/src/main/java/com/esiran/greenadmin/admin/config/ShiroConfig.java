package com.esiran.greenadmin.admin.config;

import com.esiran.greenadmin.admin.shiro.AdminAuthRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private final AdminAuthRealm realm;

    public ShiroConfig(AdminAuthRealm realm) {
        this.realm = realm;
    }


    /**
     * 记住我的配置
     * @return
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie(
                ShiroHttpSession.DEFAULT_SESSION_ID_NAME.concat("_ADMIN"));
        /**通过js脚本将无法读取到cookie信息*/
        cookie.setHttpOnly(true);
        /**cookie保存一天*/
        cookie.setMaxAge(24 * 60 * 60);
        CookieRememberMeManager manager=new CookieRememberMeManager();
        manager.setCookie(cookie);
        return manager;
    }

    /**
     * @function 加密算法
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        /** 加密次数 */
        hashedCredentialsMatcher.setHashIterations(1);
        return  hashedCredentialsMatcher;
    }

    @Bean("sessionManager")
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(HashedCredentialsMatcher hashedCredentialsMatcher, CacheManager cacheManager, RememberMeManager meManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //校验密码用到的算法
        //        realm.setCredentialsMatcher(hashedCredentialsMatcher);
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager);
        securityManager.setRememberMeManager(meManager);
        return securityManager;
    }



    /**
     * 缓存配置
     * @return
     */
    @Bean
    public MemoryConstrainedCacheManager cacheManager() {
        //使用内存缓存
        MemoryConstrainedCacheManager cacheManager=new MemoryConstrainedCacheManager();
        return cacheManager;
    }

    /**
     * @funtion Filter工厂，设置对应的过滤条件和跳转条件
     * @param securityManager
     * @return
     */
    @Bean( name="shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        /**
         * 过滤链定义，从上向下顺序执行，一般将/**放在最为下
         * 所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
         */
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/font/**", "anon");
        filterChainDefinitionMap.put("/layui/**", "anon");
        filterChainDefinitionMap.put("/simditor/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");

        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "anon");

        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuth");
        shiroFilterFactoryBean.setSuccessUrl("/home");
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     *
     * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


}
