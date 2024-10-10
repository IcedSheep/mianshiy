package com.sheep.mianshiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.dto.question.QuestionQueryRequest;
import com.sheep.mianshiy.service.QuestionService;
import com.sheep.mianshiy.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**

* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-10-01 10:53:09
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Resource
    private QuestionMapper questionMapper;

    /**
     * 模糊查询题目
     * @param questionQueryRequest
     * @return
     */
    @Override
    public List<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        int current = questionQueryRequest.getCurrent();
        int pageSize = questionQueryRequest.getPageSize();
        int startIndex = (current - 1) * pageSize;
        List<Question> questionList = questionMapper.listQuestionByPage(questionQueryRequest,startIndex);
        if (questionList.isEmpty()) {
            return new ArrayList<>();
        }
        return questionList;
    }
}




