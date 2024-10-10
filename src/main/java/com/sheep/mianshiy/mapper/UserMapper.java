package com.sheep.mianshiy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.model.entity.UserSignIn;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户数据库操作
 *

 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户某年某月的签到情况
     * @param startOfMonth 月的第一天
     * @param endOfMonth  月最后一天
     * @return
     */

    List<UserSignIn> listUserSignInRecordOfMonth(
            @Param("userId") Long userId, @Param("startOfMonth") Date startOfMonth,
            @Param("endOfMonth") Date endOfMonth);

}




