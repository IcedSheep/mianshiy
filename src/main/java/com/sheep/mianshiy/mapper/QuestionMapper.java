package com.sheep.mianshiy.mapper;

import com.sheep.mianshiy.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sheep.mianshiy.model.dto.question.QuestionQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**

*/
public interface QuestionMapper extends BaseMapper<Question> {

    List<Question> listQuestionByQuestionBankId(@Param("questionIdList") List<Long> questionIdList);


    /**
     * 题目模糊查询
     * @param questionQueryRequest
     * @return
     */
    List<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);
}




