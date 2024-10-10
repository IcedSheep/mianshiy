package com.sheep.mianshiy.model.dto.question;

import com.sheep.mianshiy.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题目请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 推荐答案
     */
    private String answer;


    /**
     * 审核状态 0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 仅会员可见（1 表示仅会员可见）
     */
    private Integer needVip;

    /**
     * 题目难度程度（0 简单，1 中等，2 困难）
     */
    private Integer difficult;


    private static final long serialVersionUID = 1L;
}