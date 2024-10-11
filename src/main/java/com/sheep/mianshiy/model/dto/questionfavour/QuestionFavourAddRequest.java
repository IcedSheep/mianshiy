package com.sheep.mianshiy.model.dto.questionfavour;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题目收藏请求
 *

 */
@Data
public class QuestionFavourAddRequest implements Serializable {

    private Long questionId;

    private static final long serialVersionUID = 1L;
}