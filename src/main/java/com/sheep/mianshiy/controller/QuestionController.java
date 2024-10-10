package com.sheep.mianshiy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sheep.mianshiy.annotation.AuthCheck;
import com.sheep.mianshiy.common.BaseResponse;
import com.sheep.mianshiy.common.DeleteRequest;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.common.ResultUtils;
import com.sheep.mianshiy.constant.UserConstant;
import com.sheep.mianshiy.exception.BusinessException;
import com.sheep.mianshiy.exception.ThrowUtils;

import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.model.dto.question.QuestionAddRequest;
import com.sheep.mianshiy.model.dto.question.QuestionQueryRequest;
import com.sheep.mianshiy.model.dto.question.QuestionUpdateRequest;
import com.sheep.mianshiy.model.vo.QuestionVO;
import com.sheep.mianshiy.model.vo.UserVO;
import com.sheep.mianshiy.service.QuestionService;
import com.sheep.mianshiy.service.UserService;
import com.sheep.mianshiy.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 *

 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;



    /**
     * 创建题目
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest,question);
        question.setTags(JsonUtils.toJson(questionAddRequest.getTags()));
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        question.setTags(JsonUtils.toJson(questionUpdateRequest.getTags()));
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取题目（封装类）
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 查询关联的用户
        User user = userService.getById(loginUser.getId());
        UserVO userVO = userService.getUserVO(user);
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question,questionVO);
        questionVO.setUser(userVO);
        // 获取封装类
        return ResultUtils.success(questionVO);
    }

    /**
     * 分页获取题库下的题目
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null,ErrorCode.PARAMS_ERROR);
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        List<Question> questionList = questionService.listQuestionByPage(questionQueryRequest);
        Page<Question> page = new Page<>(current,size);
        page.setRecords(questionList);
        return ResultUtils.success(page);
    }

}
