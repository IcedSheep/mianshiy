package com.sheep.mianshiy.service;

import com.sheep.mianshiy.model.entity.QuestionFavour;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sheep.mianshiy.model.entity.User;

/**
*/
public interface QuestionFavourService extends IService<QuestionFavour> {

    /**
     * 收藏 / 取消收藏
     * @param questionId
     * @param loginUser
     * @return
     */
    Boolean doQuestionFavour(Long questionId, User loginUser);

    /**
     * 收藏 / 取消收藏 使用了事务
     * @param userId
     * @param questionId
     * @return
     */
   Boolean doQuestionFavourInner(long userId, long questionId);
}
