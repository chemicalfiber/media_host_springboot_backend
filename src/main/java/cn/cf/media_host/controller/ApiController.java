package cn.cf.media_host.controller;

import cn.cf.media_host.pojo.User;
import cn.cf.media_host.repository.UserRepository;
import cn.cf.media_host.utils.StatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Resource
    private UserRepository userRepository;

    @GetMapping("/verifyCode.png")
    public void generateVerifyCode(){

    }

    @GetMapping("/checkToken")
    public ResponseEntity<Map<String,Object>> checkToken(HttpServletRequest request){
        String tokenString = request.getParameter("x-token");
        if (tokenString==null || tokenString.isEmpty()){
            return StatusResponse.badRequest400("未传递token",null);
        }
        return StatusResponse.ok200(null);
    }

    @GetMapping("/checkName")
    public ResponseEntity<Map<String,Object>> checkUserName(String username){
        User byUsername = userRepository.findByUsername(username);
        HashMap<String, Object> map = new HashMap<>();
        if (byUsername == null){
            map.put("canUse",true);
        }else {
            map.put("canUse",false);
        }
        return StatusResponse.ok200(map);
    }
}
