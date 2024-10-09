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

import com.sheep.mianshiy.model.dto.questionbank.QuestionBankAddRequest;
import com.sheep.mianshiy.model.dto.questionbank.QuestionBankQueryRequest;
import com.sheep.mianshiy.model.dto.questionbank.QuestionBankUpdateRequest;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionBank;
import com.sheep.mianshiy.model.entity.User;

import com.sheep.mianshiy.model.vo.QuestionBankVO;
import com.sheep.mianshiy.service.QuestionBankQuestionService;
import com.sheep.mianshiy.service.QuestionBankService;
import com.sheep.mianshiy.service.QuestionService;
import com.sheep.mianshiy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 题库接口
 *

 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;



    /**
     * 创建题库
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
        // 数据校验
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionBank.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankId = questionBank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * 删除题库
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 更新题库（仅管理员可用）
     *
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
        // 数据校验
        // 判断是否存在
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 获取所有的题库
     * @return
     */
    @GetMapping("/list/vo")
    public BaseResponse<List<QuestionBankVO>> listQuestionBankVO() {
        List<QuestionBank> questionBankList = questionBankService.list();
        if (CollectionUtils.isEmpty(questionBankList)) {
            return ResultUtils.success(null);
        }
        List<QuestionBankVO> questionBankVOList = new ArrayList<>();
        questionBankList.stream().forEach(
           questionBank -> {
               QuestionBankVO questionBankVO = new QuestionBankVO();
               BeanUtils.copyProperties(questionBank,questionBankVO);
               questionBankVOList.add(questionBankVO);
           }
        );
        // 获取封装类
        return ResultUtils.success(questionBankVOList);
    }

    /**
     * 分页获取题库下的题目
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 查询数据库
        Long questionBankId = questionBankQueryRequest.getId();
        //
        List<Question> questionList = questionBankQuestionService.listQuestionByPage(questionBankId);
        Page<Question> page = new Page<>(current,size);
        page.setRecords(questionList);
        return ResultUtils.success(page);
    }


}
