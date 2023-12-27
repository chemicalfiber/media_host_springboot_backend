package cn.cf.media_host.controller;

import cn.cf.media_host.pojo.Media;
import cn.cf.media_host.pojo.User;
import cn.cf.media_host.service.MediaService;
import cn.cf.media_host.service.UserService;
import cn.cf.media_host.utils.JWTUtil;
import cn.cf.media_host.utils.StatusResponse;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/file")
public class MediaController {

    @Resource
    private MediaService mediaService;
    @Resource
    private UserService userService;

    @Value("${media-host.self-domain}")
    private String selfDomain;
    @Value("${server.port}")
    private String port;

    /**
     * 查询文件信息，在本系统内根据文件ID查找文件详细信息
     *
     * @param id 要查询的文件的ID
     * @return 封装的响应状态
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMediaDetail(@PathVariable String id) {
        // 查询文件信息
        Media media = mediaService.findById(id);
        if (media == null) {
            return StatusResponse.badRequest400("文件ID无效", null);
        }
        // 查询文件上传者
        User user = userService.findUserById(media.getUploader_id());
        if (user == null) {
            return StatusResponse.badRequest400("找不到对应用户", null);
        }

        // 构建返回数据
        HashMap<String, Object> map = new HashMap<>();
        // 敏感信息保护
        media.setUploader_id("");
        media.setGrid_fs_key(null);
        map.put("info", media);
        map.put("file_link", selfDomain + ":" + port + "/f/" + media.get_id());
        map.put("upload_user", user.getNickname());

        return StatusResponse.ok200(map);
    }

    /**
     * 接收上传文件
     *
     * @param file    从前端表单中提交的文件
     * @param request 封装请求信息的对象
     * @return 封装的响应状态
     * @throws Exception 当出现异常时抛出
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadMedia(@RequestPart MultipartFile file, HttpServletRequest request) throws Exception {
        // 尝试从请求参数和header中获取token
        String userTokenString = request.getHeader("x-token");
        if (userTokenString == null || userTokenString.isEmpty()) {
            // 当header中没有token时从请求参数获取
            userTokenString = request.getParameter("x-token");
        }
        // 如果没有token，就以传递过来的userId作为写入数据库的依据
        String userId = request.getParameter("userId");

        if ((userTokenString == null || userTokenString.isEmpty()) &&
                (userId == null || userId.isEmpty())) {
            return StatusResponse.badRequest400("未传递token或用户id，上传被拒绝", null);
        }

        // 以token中的id为准，忽略请求参数中的userId
        if (userTokenString != null && !userTokenString.isEmpty()) {
            DecodedJWT decodedJWT = JWTUtil.verify(userTokenString);
            userId = decodedJWT.getClaim("user_id").asString();
        }

        // 判断文件类型
        String contentType = file.getContentType();
        String fileType;
        if (contentType.contains("image")) {
            fileType = "image";
        } else if (contentType.contains("video")) {
            fileType = "video";
        } else {
            return StatusResponse.badRequest400("本图床系统仅接受图片/视频类型的文件", null);
        }

        // 存储文件到GridFS中
        Media insert = mediaService.storeInGridFS(file, fileType, userId);
        if (insert == null) {
            return StatusResponse.internalServerError500("服务器内部错误，请稍后重试", null);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("file_link", selfDomain + ":" + port + "/f/" + insert.get_id());
        return StatusResponse.ok200(map);
    }

    /**
     * 根据文件ID删除文件，包含media集合中的文件记录和GridFS中的文件
     *
     * @param id 要删除的文件在media集合中的_id
     * @return 封装的响应状态
     */
    @GetMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteMediaById(@PathVariable String id) {
        if (id.equals("") || id.isEmpty()) {
            return StatusResponse.badRequest400("未传递文件ID", null);
        }

        if (mediaService.deleteById(id)) {
            return StatusResponse.ok200(null);
        } else {
            return StatusResponse.internalServerError500("服务器内部错误，请稍后重试", null);
        }

    }
}
