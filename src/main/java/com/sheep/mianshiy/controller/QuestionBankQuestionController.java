package com.sheep.mianshiy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sheep.mianshiy.annotation.AuthCheck;
import com.sheep.mianshiy.common.BaseResponse;
import com.sheep.mianshiy.common.DeleteRequest;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.common.ResultUtils;
import com.sheep.mianshiy.constant.UserConstant;
import com.sheep.mianshiy.exception.BusinessException;
import com.sheep.mianshiy.exception.ThrowUtils;

import com.sheep.mianshiy.model.dto.questionbankquestion.QuestionBankQuestionAddRequest;
import com.sheep.mianshiy.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.sheep.mianshiy.model.dto.questionbankquestion.QuestionBankQuestionRemoveRequest;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionBank;
import com.sheep.mianshiy.model.entity.QuestionBankQuestion;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.model.enums.UserRoleEnum;
import com.sheep.mianshiy.service.QuestionBankQuestionService;
import com.sheep.mianshiy.service.QuestionBankService;
import com.sheep.mianshiy.service.QuestionService;
import com.sheep.mianshiy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目接口
 *

 */
@RestController
@RequestMapping("/questionBankQuestion")
@Slf4j
public class QuestionBankQuestionController {

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionService questionService;


    // region 增删改查

    /**
     * 题目加入到某个题库
     * @param questionBankQuestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionBankQuestionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQuestionAddRequest == null, ErrorCode.PARAMS_ERROR);
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestionAddRequest, questionBankQuestion);
        // 查询题库是否存在
        Long questionBankId = questionBankQuestionAddRequest.getQuestionBankId();
        Long questionId = questionBankQuestionAddRequest.getQuestionId();
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR,"题目不存在");
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR,"题目不存在");
        User loginUser = userService.getLoginUser(request);
        questionBankQuestion.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankQuestionService.save(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankQuestionId = questionBankQuestion.getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }


    /**
     * 题目移除某个题库
     * @param questionBankQuestionRemoveRequest
     * @return
     */
    @PostMapping("/remove")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeQuestionBankQuestion(@RequestBody QuestionBankQuestionRemoveRequest questionBankQuestionRemoveRequest) {
        ThrowUtils.throwIf(questionBankQuestionRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestionRemoveRequest.getQuestionBankId();
        Long questionId = questionBankQuestionRemoveRequest.getQuestionId();
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId",questionId);
        queryWrapper.eq("questionBankId",questionBankId);
        // 操作数据库
        boolean result = questionBankQuestionService.remove(queryWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }



}
