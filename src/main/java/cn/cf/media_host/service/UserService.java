package cn.cf.media_host.service;

import cn.cf.media_host.pojo.User;
import cn.cf.media_host.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    /**
     * 用户登录
     * @param user 使用前端参数封装生成的User对象
     * @return 存放在数据库中的User记录，如果数据库中没有对应记录，则返回null
     */
    public User userLogin(User user) {
        return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 当用户名可用时（数据库中没有使用这个用户名的用户时），返回true，反之则返回false
     */
    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username) == null;
    }

    /**
     * 用户注册
     * @param user 要写入数据库的User对象
     * @return 是否成功将用户信息写入数据库
     */
    public boolean userRegister(User user){
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 更新用户信息
     * @param user 要更新信息的用户实体类
     * @return 是否成功更新数据库中的信息
     */
    public boolean userUpdate(User user){
        try {
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据ID查询用户
     * @param id 用户在user集合中的_id
     * @return 包含用户信息的User实体类
     */
    public User findUserById(String id) {
        Optional<User> optionalUser = userRepository.findById(new ObjectId(id));
        if (optionalUser.isEmpty()){
            return null;
        }
        return optionalUser.get();
    }
}
