package com.sheep.mianshiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheep.mianshiy.common.ErrorCode;
import com.sheep.mianshiy.exception.BusinessException;
import com.sheep.mianshiy.exception.ThrowUtils;
import com.sheep.mianshiy.mapper.QuestionFavourMapper;
import com.sheep.mianshiy.mapper.QuestionMapper;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionFavour;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.service.QuestionFavourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
*/
@Service
@Slf4j
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour>
    implements QuestionFavourService{

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionFavourMapper questionFavourMapper;


    /**
     * 收藏 / 取消收藏
     * @param questionId
     * @param loginUser
     * @return
     */
    @Override
    public Boolean doQuestionFavour(Long questionId, User loginUser) {
        ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR);
        Question question = questionMapper.selectById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR,"题目不存在");
        QuestionFavourService questionFavourService = (QuestionFavourServiceImpl) AopContext.currentProxy();
        return questionFavourService.doQuestionFavourInner(loginUser.getId(),questionId);
    }

    /**
     * 收藏 / 取消收藏 使用了事务
     * @param userId
     * @param questionId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean doQuestionFavourInner(long userId, long questionId) {
        // 先查询点赞表是否点过赞
        QuestionFavour oldQuestionFavour = questionFavourMapper.getQuestionFavour(userId,questionId);
        //如果点过赞，删除点赞表的记录，（取消点赞）
        if (oldQuestionFavour != null) {
            try {
                questionFavourMapper.remove(userId,questionId);
            } catch (DuplicateKeyException e) {
                log.error("数据库唯一键冲突，用户id：{},题目id：{},异常信息：{}",userId,questionId,e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"不能重复操作");
            }
            catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"操作失败");
            }
            // 题目表的点赞数减一 decrease  increase
            Integer affectRows = questionMapper.decreaseFavourNum(questionId);
            ThrowUtils.throwIf(affectRows <= 0,ErrorCode.OPERATION_ERROR,"操作失败");

        }else {
            //如果没有点过赞，插入点赞表的记录，（点赞）
            QuestionFavour questionFavour = QuestionFavour.builder()
                    .questionId(questionId)
                    .userId(userId)
                    .build();
            try {
                questionFavourMapper.insert(questionFavour);
            } catch (DuplicateKeyException e) {
                log.error("数据库唯一键冲突，用户id：{},题目id：{},异常信息：{}",userId,questionId,e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"不能重复操作");
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"操作失败");
            }
            // 题目表的点赞数加一
            Integer affectRows = questionMapper.incrFavourNum(questionId);
            ThrowUtils.throwIf(affectRows <= 0,ErrorCode.OPERATION_ERROR,"操作失败");
        }
        return true;
    }
}




