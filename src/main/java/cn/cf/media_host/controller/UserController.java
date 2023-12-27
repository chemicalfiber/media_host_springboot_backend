package cn.cf.media_host.controller;

import cn.cf.media_host.pojo.Media;
import cn.cf.media_host.pojo.User;
import cn.cf.media_host.service.MediaService;
import cn.cf.media_host.service.UserService;
import cn.cf.media_host.utils.JWTUtil;
import cn.cf.media_host.utils.StatusResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private MediaService mediaService;

    @Value("${media-host.self-domain}")
    private String selfDomain;
    @Value("${server.port}")
    private String port;

    /**
     * 用户登录
     *
     * @param user 由前端提交的信息封装而成的User实体类对象
     * @return 封装的响应状态
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(User user) {
//        System.out.println(user);
        User loginUser = userService.userLogin(user);
        // 是否登录成功？
        if (loginUser == null) {
            return StatusResponse.badRequest400("用户名或密码错误", null);
        }
//        System.out.println(loginUser);
        // 生成JWT_token
        String tokenString = JWTUtil.generateToken(loginUser);
        // 生成响应数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", tokenString);

        return StatusResponse.ok200(map);
    }

    /**
     * 用户注册
     *
     * @param user            由前端提交的信息封装而成的User实体类对象
     * @param confirmPassword 由前端提交的"确认密码"
     * @return 封装的响应状态
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> userRegister(User user, String confirmPassword) {
//        public ResponseEntity<Map<String, Object>> userRegister(String username, String nickname, String password, String confirmPassword) {
//        System.out.println(username);
//        System.out.println(nickname);
//        System.out.println(password);
//        System.out.println(confirmPassword);
        String username = user.getUsername();
        String nickname = user.getNickname();
        String password = user.getPassword();
        HashMap<String, Object> map = new HashMap<>();
        // 比对两次输入的密码
        if (!password.equals(confirmPassword)) {
            map.put("username", username);
            map.put("nickname", nickname);
            return StatusResponse.badRequest400("两次输入的密码不一致！", map);
        }

        // 检查用户名可用性
        boolean usernameAvailable = userService.isUsernameAvailable(username);
        if (!usernameAvailable) {    // 由于返回值是true代表的是"可用"，所以在这里取反
            map.put("username", username);
            map.put("nickname", nickname);
            map.put("usernameIsBeingUsed", true);    // 用户名被占用了！
            return StatusResponse.badRequest400("用户名被占用！", map);
        }

        // 校验通过
        boolean isRegisterSuccess = userService.userRegister(user);
        if (isRegisterSuccess) {
            return StatusResponse.ok200(null);
        } else {
            return StatusResponse.internalServerError500("服务器错误，请稍后重试。", null);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user            由前端提交的信息封装而成的User实体类对象
     * @param user_id         由前端提交的用户ID
     * @param confirmPassword 由前端提交的"确认密码"
     * @return 封装的响应状态
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> userUpdate(User user, String user_id, String confirmPassword) {
        user.set_id(new ObjectId(user_id)); // 前端传递的参数名为user_id
//        System.out.println(user);
        HashMap<String, Object> map = new HashMap<>();
        // 比对两次输入的密码
        if (!user.getPassword().equals(confirmPassword)) {
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            return StatusResponse.badRequest400("两次输入的密码不一致！", map);
        }

        // 将更新信息写入数据库
        boolean isUpdateSuccess = userService.userUpdate(user);
        if (isUpdateSuccess) {
            return StatusResponse.ok200(null);
        } else {
            return StatusResponse.internalServerError500("服务器错误，请稍后重试。", null);
        }
    }


    /**
     * 根据用户ID查询视频
     *
     * @param id 前端提交的用户ID
     * @return 封装的响应状态，包含了查询到的List<Media>
     */
    @GetMapping("/video/{id}")
    public ResponseEntity<Map<String, Object>> getVideosByUserId(@PathVariable String id) {
//        mediaService.getMediaByUserId(id,"video");
        return StatusResponse.ok200(mediaService.getMediaByUserId(id, "video"));
    }

    /**
     * 根据用户ID查询图片
     *
     * @param id 前端提交的用户ID
     * @return 封装的响应状态，包含了查询到的List<Media>
     */
    @GetMapping("/img/{id}")
    public ResponseEntity<Map<String, Object>> getImagesByUserId(@PathVariable String id) {
        List<Media> images = mediaService.getMediaByUserId(id, "image");
        // 敏感信息保护
        for (Media image : images) {
            image.setUploader_id("");
            image.setGrid_fs_key(new ObjectId());
            image.setLink(selfDomain + ":" + port + "/f/" + image.get_id());
        }
        return StatusResponse.ok200(images);
    }
}
