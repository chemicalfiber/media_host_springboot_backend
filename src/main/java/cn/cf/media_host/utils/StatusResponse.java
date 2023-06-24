package cn.cf.media_host.utils;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义响应状态，将JSON交给前端处理，同时设置响应码
 */
public class StatusResponse {
    /**
     * 200 OK，请求成功响应
     * @param data 响应的数据
     * @return ResponseEntity，最终会被转换为JSON
     */
    public static ResponseEntity<Map<String,Object>> ok200(Object data){
        HashMap<String, Object> map = new HashMap<>();
        map.put("message","OK");
        map.put("data",data);
        return ResponseEntity.ok(map);
    }

    /**
     * 400 BadRequest，错误的请求
     * @param message 要传达给前端的错误信息文本
     * @param data 错误信息数据，可以是null
     * @return ResponseEntity，最终会被转换为JSON
     */
    public static ResponseEntity<Map<String,Object>> badRequest400(String message,Object data){
        HashMap<String, Object> map = new HashMap<>();
        map.put("message",message);
        map.put("data",data);
        return ResponseEntity.badRequest().body(map);
    }

    /**
     * 404 NotFound，找不到资源
     * @return ResponseEntity，最终会被转换为JSON
     */
    public static ResponseEntity<Map<String,Object>> notFound404(){
        return ResponseEntity.notFound().build();
    }

    /**
     * 500 InternalServerError，服务器内部错误
     * @param message 要传达给前端的错误信息文本
     * @param data 错误信息数据，可以是null
     * @return ResponseEntity，最终会被转换为JSON
     */
    public static ResponseEntity<Map<String,Object>> internalServerError500(String message,Object data){
        HashMap<String, Object> map = new HashMap<>();
        map.put("message",message);
        map.put("data",data);
        return ResponseEntity.internalServerError().body(map);
    }
}
