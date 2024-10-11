package com.sheep.mianshiy.service;

import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sheep.mianshiy.model.entity.User;

import java.util.List;

/**
*/
public interface QuestionThumbService extends IService<QuestionThumb> {

    /**
     * 点赞 / 取消点赞
     * @param questionId
     * @param loginUser
     * @return
     */
    Boolean doQuestionThumb(Long questionId, User loginUser);

    /**
     * 获取点赞数
     * @param questionId
     * @return
     */
    Long getQuestionThumbNum(Long questionId);

    Boolean doQuestionThumbImporve(Long questionId, User loginUser);

    List<Question> getUserThumbQuestion(User loginUser);
}
