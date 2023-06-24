package cn.cf.media_host.repository;

import cn.cf.media_host.pojo.Media;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends MongoRepository<Media, ObjectId> {
    // 根据上传者ID查询指定类型的文件信息
    @Query("{'uploader_id':?0,'type':?1}")
    List<Media> findByUploader_id(String uploader_id,String fileType);

    // 重载findById，返回Optional以避免NullPointerException
    @Query("{'_id':?0}")
    Optional<Media> findById(String fileId);

    // 重载deleteById，返回的是被删除文件对应的media集合中的记录
    @DeleteQuery("{'_id':?0}")
    Media deleteById(String id);
}
