package com.tang.admin.config.security;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tang.admin.config.ClassPathTldsLoader;
import com.tang.admin.filters.CaptchaCodeFilter01;
import com.tang.admin.filters.CaptchaCodeFilter02;
import com.tang.admin.pojo.User;
import com.tang.admin.service.IRbacService;
import com.tang.admin.service.IUserService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JxcAuthenticationSuccessHandler jxcAuthenticationSuccessHandler;

    @Resource
    private JxcAuthenticationFailedHandler jxcAuthenticationFailedHandler;

    @Resource
    private JxcLogoutSuccessHandler jxcLogoutSuccessHandler;

    @Resource
    private IUserService userService;

    @Resource
    private CaptchaCodeFilter02 captchaCodeFilter02;

    @Resource
    private CaptchaCodeFilter01 captchaCodeFilter01;

    @Resource
    private DataSource dataSource;

    @Resource
    private IRbacService rbacService;

    /**
     * 放行静态资源
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/css/**",
                "/error/**",
                "/images/**",
                "/js/**",
                "/lib/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterBefore(captchaCodeFilter01, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().disable(); // 任何跨域
//        http.headers().frameOptions().sameOrigin(); // 同源跨域

        http.formLogin()
                .loginPage("/index")
                .loginProcessingUrl("/login")
                .successHandler(jxcAuthenticationSuccessHandler)
                .failureHandler(jxcAuthenticationFailedHandler);
        http.logout()
                .logoutUrl("/signout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(jxcLogoutSuccessHandler);

        http.authorizeRequests()
                .antMatchers("/index").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/image").permitAll()
                .anyRequest().authenticated();

        http.rememberMe()
                .rememberMeParameter("rememberMe")
                //设置token的有效期，即多长时间内可以免除重复登录，单位是秒。
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .tokenRepository(persistentTokenRepository());

    }

    /**
     * 自定义登录逻辑
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                System.out.println("loading User...");
                User user = userService.findByUsername(username);
                if (null == user) {
                    throw new UsernameNotFoundException("用户不存在");
                }
                System.out.println("getting authorities...");
                List<String> roleNames = rbacService.findRolesByUserName(username);
                if(CollectionUtils.isNotEmpty(roleNames)){
                    List<String> authorities = rbacService.findAuthoritiesByRoleName(roleNames);
                    roleNames = roleNames.stream().map(role -> "ROLE_" + role).collect(Collectors.toList());
                    authorities.addAll(roleNames);
                    user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", authorities)));
                }
                return user;
            }
        };
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置从数据库中获取token
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    /**
     * 加载 ClassPathTldsLoader
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ClassPathTldsLoader.class)
    public ClassPathTldsLoader classPathTldsLoader(){
        return new ClassPathTldsLoader();
    }

//    告知框架，userDetailsService的实现类为...，和密码解析器为...。
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService()).passwordEncoder(encoder());
//    }
//
////    手动生成asdf的密文，并加入数据库
//    public static void main(String[] args) {
//        BCryptPasswordEncoder pw = new BCryptPasswordEncoder();
//        String psw = pw.encode("asdf");
//        System.out.println(psw);
//        System.out.println(pw.matches("asdf", "$2a$10$uomzRb1GdFM.4rgV/nGNW.nwCOJP9NYQJDL2TvGR2O8q.NvD4tQjW"));
//    }



}
