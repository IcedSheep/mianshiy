package com.sheep.mianshiy.mapper;

import com.sheep.mianshiy.model.entity.QuestionBankQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**


*/
public interface QuestionBankQuestionMapper extends BaseMapper<QuestionBankQuestion> {

    /**
     * 查询题库下的题库
     * @param questionBankId
     * @return
     */
    List<Long> listQuestionIds(@Param("questionBankId") Long questionBankId);
}




