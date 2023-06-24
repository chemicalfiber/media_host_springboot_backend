package cn.cf.media_host;

import cn.cf.media_host.pojo.User;
import cn.cf.media_host.repository.MediaRepository;
import cn.cf.media_host.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Optional;

@SpringBootTest
class MediaHostApplicationTests {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MediaRepository mediaRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testFindByUsername(){
        System.out.println(userRepository.findByUsername("chemicalfiber"));
        Optional<User> user = userRepository.findById(new ObjectId("645ce2682ce6792e3b553e07"));
        if (user.isPresent()){
            System.out.println(user.get());
        }else {
            System.out.println("找不到_id为[645ce2682ce6792e3b553e07]的用户");
        }
    }

    @Test
    void testFindByUploaderId(){
        System.out.println(mediaRepository.findByUploader_id("645ce2682ce6792e3b553e07","video"));
    }
}
