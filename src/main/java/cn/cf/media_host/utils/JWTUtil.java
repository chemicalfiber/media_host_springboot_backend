package cn.cf.media_host.utils;

import cn.cf.media_host.pojo.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;

public class JWTUtil {

    private static final String SECRET = "aklsghauioghsduikohsdfujk";  // 算法中的"盐"

    // 生成token
    public static String generateToken(User user) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 7);    // 默认7天过期

        // 可以省略的JWT头
//        HashMap<String, Object> map = new HashMap<>();

        JWTCreator.Builder builder = JWT.create();

        builder.withClaim("username", user.getUsername())
                .withClaim("nickname", user.getNickname())
                .withClaim("usertype", user.getUsertype())
                .withClaim("user_id", user.get_id().toHexString());

//        map.forEach((key,value)-> {
//            builder.withClaim(key,value);   // 添加payload
//        });

        String token = builder
                //.withHeader(map)  // header
                .withExpiresAt(instance.getTime())  // 设定过期时间，在上面指定了7天
                .sign(Algorithm.HMAC256(SECRET));   // 指定签名算法，并指定算法中的"盐"

//        System.out.println(token);
        return token;
    }

    // 验证token合法性，解析token
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    // 解析token
//    public static DecodedJWT getTokenInfo(String token){
//        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
//        return decodedJWT;
//    }
}
