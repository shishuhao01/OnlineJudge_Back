package com.shishuhao.OnlineJudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishuhao.OnlineJudge.annotation.AuthCheck;
import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.DeleteRequest;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.config.WxOpenConfig;
import com.shishuhao.OnlineJudge.constant.UserConstant;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.exception.ThrowUtils;
import com.shishuhao.OnlineJudge.model.dto.user.*;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.face.UserLoginAndRegister;
import com.shishuhao.OnlineJudge.model.vo.LoginUserVO;
import com.shishuhao.OnlineJudge.model.vo.UserVO;
import com.shishuhao.OnlineJudge.service.UserService;
import com.shishuhao.OnlineJudge.utils.MyFaceUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.UUID;

import static com.shishuhao.OnlineJudge.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;


    private final static String face_outer_id = "shishuhao";
    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userName = userRegisterRequest.getUserName();


        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userName)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userName, userPassword, checkPassword);

        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        long PageNum = userQueryRequest.getPageNum();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(PageNum, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long PageNum = userQueryRequest.getPageNum();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(PageNum, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(PageNum, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    @PostMapping("/faceIdentify")
    public BaseResponse<LoginUserVO> userLoginByFace (@RequestPart("file") MultipartFile multipartFile,HttpServletRequest
                                                      httpServletRequest) {
        MyFaceUtils myFaceUtils = new MyFaceUtils();
        // 获取登录图片
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        File file = new File("D:\\Grade3\\Myself\\java\\SpringCloudProject\\OnlineJudgeBack\\src\\UserPicture"+
                File.separator + newFilename);
        try {

            multipartFile.transferTo(file);

            // 使用detect获得face_token
            String face_token =  myFaceUtils.GetFaceTokenDetect(file);

            // 使用search方法，传递face_token参数，获得人脸登录信息;
            UserLoginAndRegister existFaceSet = myFaceUtils.isExistFaceSet(face_token, face_outer_id);

            if (existFaceSet == null) {
               throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"人脸识别信息不存在");
            }
            // 根据confidence 和 face_token查询信息，
            Double confidence = existFaceSet.getConfidence();
            String face_token1 = existFaceSet.getFaceToken();

            User user = null;
            //只有相似度大于80.0才能进入这个判定条件，
            if (confidence > 80.0) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("faceToken",face_token1);
                user = userService.getOne(userQueryWrapper);
                if (user == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"用户人脸注册到人脸集中，用户token未保存在数据库");
                }
                httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE, user);
            }
            if (user == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"用户不存在");
            }

            return ResultUtils.success(userService.getLoginUserVO(user));

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"无人脸信息");
        }finally {
            file.delete();
        }

    }


    @PostMapping("/createFaceSet")
    public BaseResponse<String> createFaceSet (String outer_id) throws JSONException {
        MyFaceUtils myFaceUtils = new MyFaceUtils();

        String s = myFaceUtils.creatFaceSetByOutId(outer_id);

        if (s != null) {
            return ResultUtils.success(s);
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
    }


    /**
     * 人脸识别注册功能
     */
    @PostMapping("/registerByFace")
    public BaseResponse<Boolean> registerByFace (@RequestPart("file") MultipartFile multipartFile,HttpServletRequest
                                                 httpServletRequest) {
        MyFaceUtils myFaceUtils = new MyFaceUtils();
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        File file = new File("D:\\Grade3\\Myself\\java\\SpringCloudProject\\OnlineJudgeBack\\src\\UserPicture" +
                File.separator + newFilename);
        try {
            //1.获取文件信息，
            multipartFile.transferTo(file);
            //2.判断用户登录信息
            User loginUser = userService.getLoginUser(httpServletRequest);

            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"人脸注册前必须登录");
            }

            if (loginUser.getFaceToken() != null) {
                throw new BusinessException(ErrorCode.HAVE_BEAN_REGISTER,"用户已经注册过人脸");
            }

            //3.使用人脸监测detect方法获取face_token;
            String face_token = myFaceUtils.GetFaceTokenDetect(file);
            if (face_token == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未识别到人脸");
            }

            //4.将face_token添加到人脸集face_set，获得结果，（成功和失败）
            boolean b = myFaceUtils.AddFaceTokenToFaceSet(face_token, face_outer_id);

            if (!b) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统异常");
            }

            //5.将face_token传递到search方法中，返回到face_token1
            UserLoginAndRegister existFaceSet = myFaceUtils.isExistFaceSet(face_token, face_outer_id);
            if (existFaceSet == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统错误");
            }
            Double confidence = existFaceSet.getConfidence();
            String face_token1 = existFaceSet.getFaceToken();


            //6.将face_token保存到这个用户信息表中
            boolean b1 = false;
            if (confidence > 90.0) {
                loginUser.setFaceToken(face_token1);
                b1 = userService.updateById(loginUser);
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统错误");
            }
            return ResultUtils.success(b1);
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        } finally {
            file.delete();
        }

    }










}
