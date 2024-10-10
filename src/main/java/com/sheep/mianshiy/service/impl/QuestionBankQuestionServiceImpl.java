package com.sheep.mianshiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.exception.ThrowUtils;
import com.sheep.mianshiy.mapper.QuestionMapper;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionBank;
import com.sheep.mianshiy.model.entity.QuestionBankQuestion;
import com.sheep.mianshiy.service.QuestionBankQuestionService;
import com.sheep.mianshiy.mapper.QuestionBankQuestionMapper;
import com.sheep.mianshiy.service.QuestionBankService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**

* @description 针对表【question_bank_question(题库题目)】的数据库操作Service实现
* @createDate 2024-10-01 10:53:09
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionBankQuestionMapper questionBankQuestionMapper;
    /**
     * 分页查询单个题库下的关联的题目
     * @param questionBankId
     * @return
     */
    @Override
    public List<Question> listQuestionByPage(Long questionBankId) {
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR);
        // 查看题库是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null,ErrorCode.PARAMS_ERROR,"题库不存在");
        //
        List<Long> questionIdList = questionBankQuestionMapper.listQuestionIds(questionBankId);
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new ArrayList<>();
        }
        // 查询题目
        return questionMapper.listQuestionByQuestionBankId(questionIdList);
    }
}




