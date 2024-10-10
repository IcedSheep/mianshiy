package com.sheep.mianshiy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_sign_in
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignIn implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Date signDate;

    /**
     * 
     */
    private Date createdTime;

    private static final long serialVersionUID = 1L;
}