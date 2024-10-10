package com.sheep.mianshiy.service;

import com.sheep.mianshiy.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sheep.mianshiy.model.dto.question.QuestionQueryRequest;

import java.util.List;

/**

* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-10-01 10:53:09
*/
public interface QuestionService extends IService<Question> {

    List<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);
}
