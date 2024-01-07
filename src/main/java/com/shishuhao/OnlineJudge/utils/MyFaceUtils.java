package com.shishuhao.OnlineJudge.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import com.shishuhao.OnlineJudge.model.face.UserLoginAndRegister;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class MyFaceUtils {
    private static final String api_key = "cnLrQsZ2ApyIvzI7nnLNlNliElBUR_lt";

    private static final String api_secret = "RGwfPHvlgM90PLfisY36-wlm2ATEGYbs";

    /**
     * 获取人脸的token
     *
     * @param file
     * @return
     * @throws JSONException
     */
    public String GetFaceTokenDetect(File file) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("image_file", file);

        String jsonString = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = new JSONObject(jsonString);
        int faceNum = jsonObject.getInt("face_num");
        if (faceNum == 1) {
            JSONArray facesArray = jsonObject.getJSONArray("faces");
            if (facesArray.length() > 0) {
                JSONObject faceObject = facesArray.getJSONObject(0);
                String faceToken = faceObject.getString("face_token");
                return faceToken;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 使用outer_Id创建人脸集合
     */
    public String creatFaceSetByOutId(String outer_Id) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("outer_id", outer_Id);


        String jsonString = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = new JSONObject(jsonString);

        String id = jsonObject.getString("outer_id");
        if (id != null) {
            return id;
        }
        return null;
    }


    /**
     * 根据outId里面和face_token在FaceSet里面创建人脸face_token
     */
    public Boolean AddFaceTokenByOutId(String outer_id, String face_tokens) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("outer_id", outer_id);
        paramMap.put("face_tokens", face_tokens);


        String jsonString = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = new JSONObject(jsonString);
        String face_count = jsonObject.getString("face_count");

        if (face_count.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否存在这个人脸集合
     */
    public UserLoginAndRegister isExistFaceSet(String face_token, String outer_id) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/search";

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("outer_id", outer_id);
        paramMap.put("face_token", face_token);


        String jsonString = HttpUtil.post(url, paramMap);

        JSONObject jsonObj = new JSONObject(jsonString);
        JSONArray results = jsonObj.getJSONArray("results");
        if (results == null) {
            return null;
        }

        UserLoginAndRegister userLoginAndRegister = new UserLoginAndRegister();
        double confidence = 0.0;
        String faceToken = "";
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            confidence = result.getDouble("confidence");
            faceToken = result.getString("face_token");
        }
        userLoginAndRegister.setConfidence(confidence);
        userLoginAndRegister.setFaceToken(faceToken);

        return userLoginAndRegister;

    }

    /**
     * 根据outer_id 查询人脸库是否存在
     *
     * @param outer_id
     * @return
     */
    public Boolean FaceSetIsExist(String outer_id) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";


        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("outer_id", outer_id);


        String jsonString = HttpUtil.post(url, paramMap);

        JSONObject jsonObject = new JSONObject(jsonString);
        String results = jsonObject.getString("outer_id");

        if (results != null) {
            return true;
        }
        return false;
    }


    /**
     * 向人脸集中添加人脸token
     */
    public boolean AddFaceTokenToFaceSet(String face_tokens, String outer_id) throws JSONException {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("api_key", api_key);
        paramMap.put("api_secret", api_secret);
        paramMap.put("face_tokens", face_tokens);
        paramMap.put("outer_id", outer_id);


        String jsonString = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = new JSONObject(jsonString);
        String results = jsonObject.getString("outer_id");

        if (results.length() > 0) {
            return true;
        }
        return false;


    }


}
