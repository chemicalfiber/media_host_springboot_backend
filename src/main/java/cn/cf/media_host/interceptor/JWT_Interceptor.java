package cn.cf.media_host.interceptor;

import cn.cf.media_host.utils.JWTUtil;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class JWT_Interceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        HashMap<String, Object> map = new HashMap<>();

        String tokenString = request.getHeader("x-token");
//        System.out.println(tokenString);
        try {
            JWTUtil.verify(tokenString);
            return true;
        } catch (SignatureVerificationException e) {
            map.put("message", "签名校验失败");
        } catch (TokenExpiredException e) {
            map.put("message", "token过期");
        } catch (AlgorithmMismatchException e) {
            map.put("message", "算法不匹配");
        } catch (InvalidClaimException e) {
            map.put("message", "失效的payload");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("message", "无效的token");
        }

        map.put("status", false);
        // 将校验的异常结果返回给前端
        String responseJSON = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.BAD_REQUEST.value()); // 400响应状态码
        response.getWriter().println(responseJSON);
        return false;
    }
}
