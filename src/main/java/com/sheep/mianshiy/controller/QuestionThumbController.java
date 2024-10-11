package com.sheep.mianshiy.controller;

import com.sheep.mianshiy.common.BaseResponse;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.common.ResultUtils;
import com.sheep.mianshiy.exception.ThrowUtils;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.service.QuestionThumbService;
import com.sheep.mianshiy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目点赞接口
 *
 */
@RestController
@RequestMapping("/questionThumb")
@Slf4j
public class QuestionThumbController {

    @Resource
    private QuestionThumbService questionThumbService;

    @Resource
    private UserService userService;


    /**
     * 点赞 / 取消点赞
     * @param questionId
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> doQuestionThumbByRedis(Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Boolean result = questionThumbService.doQuestionThumbImporve(questionId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 查看题目的点赞数
     * @param questionId
     * @param request
     * @return
     */
    @GetMapping("/get/questionThumbNum")
    public BaseResponse<Long> getQuestionThumbNumByRedis(Long questionId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long questionThumbNum = questionThumbService.getQuestionThumbNum(questionId);
        return ResultUtils.success(questionThumbNum);
    }


    /**
     * 查看用户点了哪些题目的赞
     * @param request
     * @return
     */
    @GetMapping("/get/userThumbQuestion")
    public BaseResponse<List<Question>> getUserThumbQuestionByRedis(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Question> questionList = questionThumbService.getUserThumbQuestion(loginUser);
        return ResultUtils.success(questionList);
    }










}
