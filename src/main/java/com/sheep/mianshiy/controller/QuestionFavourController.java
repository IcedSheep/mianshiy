package com.sheep.mianshiy.controller;

import com.sheep.mianshiy.common.BaseResponse;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.common.ResultUtils;
import com.sheep.mianshiy.exception.ThrowUtils;
import com.sheep.mianshiy.model.dto.questionfavour.QuestionFavourAddRequest;
import com.sheep.mianshiy.model.entity.QuestionFavour;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.service.QuestionFavourService;
import com.sheep.mianshiy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目收藏接口
 *

 */
@RestController
@RequestMapping("/questionFavour")
@Slf4j
public class QuestionFavourController {

    @Resource
    private QuestionFavourService questionFavourService;

    @Resource
    private UserService userService;


    /**
     * 收藏 / 取消收藏
     * @param questionFavourAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> doQuestionFavour(@RequestBody QuestionFavourAddRequest questionFavourAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionFavourAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long questionId = questionFavourAddRequest.getQuestionId();
        Boolean result = questionFavourService.doQuestionFavour(questionId, loginUser);
        return ResultUtils.success(result);
    }







}
