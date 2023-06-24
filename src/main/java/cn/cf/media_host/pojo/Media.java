package cn.cf.media_host.pojo;

import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@ToString
@Document(collection = "media") // 声明这个类所对应的数据库中的collection
public class Media {
    private String _id;
    private String name;
    private Date upload_date;
    private String uploader_id;
    private String type;
    private ObjectId grid_fs_key;
}
