package cn.cf.media_host.config;

import cn.cf.media_host.interceptor.JWT_Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWT_Interceptor())
                .addPathPatterns("/user/**")    // 拦截路径
                .excludePathPatterns("/user/login") // 放行路径
                .excludePathPatterns("/user/register");
    }
}
