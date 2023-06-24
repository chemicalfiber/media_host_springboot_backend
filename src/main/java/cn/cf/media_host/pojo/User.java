package cn.cf.media_host.pojo;

import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document(collection = "user")  // 声明这个类所对应的数据库中的collection
public class User {
    @Id
    private ObjectId _id;
    private String username;
    private String nickname;
    private String password;
    private String usertype;

}
