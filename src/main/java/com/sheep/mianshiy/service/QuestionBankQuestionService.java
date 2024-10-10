package com.sheep.mianshiy.service;

import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionBankQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sheep.mianshiy.model.vo.QuestionVO;

import java.util.List;

/**

* @description 针对表【question_bank_question(题库题目)】的数据库操作Service
* @createDate 2024-10-01 10:53:09
*/
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    List<Question> listQuestionByPage(Long questionBankId);

}
