package org.learn.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.learn.utils.JwtUtil;
import org.learn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

//拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception{
        String token = request.getHeader("Authorization");

        //验证token
        try {
            //打开redis后每次都需要重新登录获得token
            //从redis中获取相同的token
            //ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            //String redisToken = ops.get(token);

            //token已经失效
            //if (redisToken == null) {
                //throw new RuntimeException();
            //}
            Map<String, Object> claims = JwtUtil.parseToken(token);

            //将数据储存到线程
            ThreadLocalUtil.set(claims);
            return true;
        }
        catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }
}
