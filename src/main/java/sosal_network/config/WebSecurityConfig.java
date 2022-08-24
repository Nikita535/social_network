package sosal_network.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sosal_network.service.UserService;


/**
 * Class WebSecurityConfig - класс конфигурации WebSecurity
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * Метод создания бина кодировщика паролей
     **/
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserService userService;

    /**
     * Метод разрешения доступа к страницам
     **/
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests().antMatchers("/login",
                        "/logout", "/register", "/activate/*", "/recovery",
                        "/recoveryPage/*", "/recover/*", "/recoveryPage", "/invalidToken", "/register/info/*", "/registerContinue", "/registerContinue/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout().deleteCookies("JSESSIONID")
                .permitAll()
                .logoutSuccessUrl("/login")
                .and()
                .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400).userDetailsService(userService);
    }


    /**
     * метод разрешения подгрузки стилей
     **/
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/**/**/**.css", "/**/**/**.js", "/**/**/**.gif");
    }
}
