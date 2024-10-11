package com.sheep.mianshiy.constant;

/**
 * 文件常量
 *

 */
public interface RedisConstant {

    /**
     * 用户签到的前缀
     */
    String USER_SIGN_IN_KEY_PREFIX = "user:sign";

    /**
     * 这个题目哪些用户点过赞
     */
    String USER_THUMB_QUESTION = "user:thumb:question";

    /**
     * 用户对哪些题目点过赞
     */
    String USER_THUMB_USER = "user:thumb:userinfo";



    /**
     * 获取用户签到的key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignInKey(Integer year, Long userId) {
        return String.format("%s:%s:%s",USER_SIGN_IN_KEY_PREFIX,year,userId);
    }
}
