package com.esiran.greenadmin.system.entity.dot;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
public class UserDTO {



    private Integer id;

    /**
     * 用户名
     */
    private String username;

    private String password;

    /**
     * 用户邮箱
     */
    private String email;


    private String roleNames;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
