package com.sheep.mianshiy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sheep.mianshiy.annotation.AuthCheck;
import com.sheep.mianshiy.common.BaseResponse;
import com.sheep.mianshiy.common.DeleteRequest;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.common.ResultUtils;
import com.sheep.mianshiy.constant.RedisConstant;
import com.sheep.mianshiy.constant.UserConstant;
import com.sheep.mianshiy.exception.BusinessException;
import com.sheep.mianshiy.exception.ThrowUtils;
import com.sheep.mianshiy.mapper.UserMapper;
import com.sheep.mianshiy.model.dto.user.UserAddRequest;
import com.sheep.mianshiy.model.dto.user.UserLoginRequest;
import com.sheep.mianshiy.model.dto.user.UserQueryRequest;
import com.sheep.mianshiy.model.dto.user.UserRegisterRequest;
import com.sheep.mianshiy.model.dto.user.UserUpdateMyRequest;
import com.sheep.mianshiy.model.dto.user.UserUpdateRequest;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.model.entity.UserSignIn;
import com.sheep.mianshiy.model.vo.LoginUserVO;
import com.sheep.mianshiy.model.vo.UserVO;
import com.sheep.mianshiy.service.UserService;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.sheep.mianshiy.service.UserSignInService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sheep.mianshiy.service.impl.UserServiceImpl.SALT;
import static org.springframework.data.elasticsearch.annotations.DateFormat.year;

/**
 * 用户接口
 *

 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

//    @Resource
//    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private UserSignInService userSignInService;

    @Resource
    private UserMapper userMapper;



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
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
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
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
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
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
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
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
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

    /**
     * 用户签到
     * @param request
     * @return
     */
    @GetMapping("/sign_in/db")
    BaseResponse<Boolean> userSignInByDB(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        Date signDate = Date.valueOf(LocalDate.now());

        // 不能重复签到
        QueryWrapper<UserSignIn> userSignInQueryWrapper = new QueryWrapper<>();
        userSignInQueryWrapper.eq("userId",userId);
        userSignInQueryWrapper.eq("signDate",signDate);
        UserSignIn userSignIn = userSignInService.getOne(userSignInQueryWrapper);
        ThrowUtils.throwIf(userSignIn != null,ErrorCode.OPERATION_ERROR,"不能重复签到");
        userSignIn = UserSignIn.builder()
                .signDate(signDate).
                userId(userId).build();
        boolean result = userSignInService.save(userSignIn);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"签到失败");
        return ResultUtils.success(result);
    }


    /**
     * 查看用户某年某月的签到情况
     * @param request
     * @return
     */
    @GetMapping("/sign_in_record/db")
    public BaseResponse< List<String>> getUserSignInRecordByDB(HttpServletRequest request, Integer year, Integer month) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        // 如果时间日期为null，获取当前月份
        year = Optional.ofNullable(year).orElse(LocalDate.now().getYear());
        month = Optional.ofNullable(month).orElse(LocalDate.now().getMonthValue());
        YearMonth yearMonth = YearMonth.of(year, month);
        // 获取该月的第一天 将 yearMonth.atDay(1) 的LocalDate 转成 Date
        Date startOfMonth = Date.valueOf(yearMonth.atDay(1));
        // 获取该月的最后一天
        Date endOfMonth = Date.valueOf(yearMonth.atEndOfMonth());
        // 查询数据库 select * from user_sign_in where userId = ? and signDate between ? and ?
        List<UserSignIn> userSignInList = userMapper.listUserSignInRecordOfMonth(userId,startOfMonth,endOfMonth);
        List<String> dateList = userSignInList.stream()
                .map(UserSignIn::getSignDate)
                // 转换日期格式
                .map(date -> formatDate(date))
                .collect(Collectors.toList());

        return ResultUtils.success(dateList);
    }


    /**
     * 时间格式解析
     * @param date
     * @return
     */
    private static String formatDate(java.util.Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }




}
