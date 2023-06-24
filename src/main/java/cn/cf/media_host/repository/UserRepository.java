package cn.cf.media_host.repository;

import cn.cf.media_host.pojo.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    // 根据username查询用户
    User findByUsername(String username);

    // 根据username和password查找用户
    User findByUsernameAndPassword(String username, String password);

}
