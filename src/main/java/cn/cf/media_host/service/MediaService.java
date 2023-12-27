package cn.cf.media_host.service;

import cn.cf.media_host.pojo.Media;
import cn.cf.media_host.repository.MediaRepository;
import cn.cf.media_host.utils.ConversionUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class MediaService {

    @Resource
    private MediaRepository mediaRepository;
    @Resource
    private GridFsOperations gridFsOperations;

    /**
     * 根据用户ID查找指定类型的文件
     *
     * @param userId   用户id
     * @param fileType 文件类型
     * @return 一个符合条件的Media的集合
     */
    public List<Media> getMediaByUserId(String userId, String fileType) {
        return mediaRepository.findByUploader_id(userId, fileType);
    }

    /**
     * 下载文件
     *
     * @param fileId 在media集合中的文件主键_id
     * @return 在GridFS中的文件对象
     */
    public GridFsResource downloadFile(String fileId) {
        // 在数据库中根据文件ID查询GridFS_key，再根据GridFS_key得到文件
        Optional<Media> media = mediaRepository.findById(fileId);
        if (media.isEmpty()) {
            return null;
        }
        // 得到GridFS_key
        ObjectId grid_fs_key = media.get().getGrid_fs_key();
        // 根据GridFS_key查询GridFS中存储的文件
        GridFSFile result = gridFsOperations.findOne(query(where("_id").is(grid_fs_key)));
        if (result != null) {
            return gridFsOperations.getResource(result);
        } else {
            return null;
        }
    }

    /**
     * 根据文件访问ID查找文件信息
     *
     * @param id 在media集合中的文件主键_id
     * @return 包含文件信息的实体类
     */
    public Media findById(String id) {
        Optional<Media> optionalMedia = mediaRepository.findById(id);
        if (optionalMedia.isEmpty()) {
            return null;
        }
        return optionalMedia.get();
    }

    /**
     * 根据ID删除文件，包括删除media集合中的数据和GridFS中的文件
     *
     * @param id 在media集合中的文件主键_id
     * @return 是否成功删除
     */
    public boolean deleteById(String id) {
        Media media = findById(id);
        try {
            // 删除GridFS中的数据
            gridFsOperations.delete(query(where("_id").is(media.getGrid_fs_key())));
            // 删除media集合中的数据
            System.out.println(mediaRepository.deleteById(id));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 接收上传文件
     *
     * @param file       要存储到数据库中的文件
     * @param fileType   文件类型，为"image"或"video"其中之一
     * @param uploaderId 上传者ID，对应user集合中的某个用户的ID
     * @return 成功插入到media集合中的一条记录
     */
    public Media storeInGridFS(MultipartFile file, String fileType, String uploaderId) {
        try {
            InputStream inputStream = file.getInputStream();
            // 存储文件到GridFS中
            ObjectId grid_fs_key = gridFsOperations.store(inputStream, file.getOriginalFilename(), file.getContentType());
            // 将文件信息写入到media集合中
            Media media = new Media();
            media.set_id(ConversionUtil.encode(System.currentTimeMillis()));
            media.setName(file.getOriginalFilename());
            media.setUpload_date(new Date());
            media.setUploader_id(uploaderId);
            media.setType(fileType);
            media.setGrid_fs_key(grid_fs_key);

            // 检查是否插入成功
            return mediaRepository.insert(media);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
