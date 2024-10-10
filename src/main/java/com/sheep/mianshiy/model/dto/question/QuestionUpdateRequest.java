package com.sheep.mianshiy.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目请求
 *

 */
@Data
public class QuestionUpdateRequest implements Serializable {

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

    /**
     * 浏览量
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;



    private static final long serialVersionUID = 1L;

}