package com.sheep.mianshiy.mapper;

import com.sheep.mianshiy.model.entity.QuestionFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
*/
public interface QuestionFavourMapper extends BaseMapper<QuestionFavour> {

    /**
     * 查询收藏表
     * @param userId
     * @param questionId
     * @return
     */
    QuestionFavour getQuestionFavour(@Param("userId") long userId, @Param("questionId") long questionId);

    /**
     * 删除点赞信息
     * @param userId
     * @param questionId
     */
    void remove(@Param("userId") long userId, @Param("questionId") long questionId);
}




