package com.sheep.mianshiy.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 审核题目请求
 *
 */
@Data
public class QuestionReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 审核状态 0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;



    private static final long serialVersionUID = 1L;

}