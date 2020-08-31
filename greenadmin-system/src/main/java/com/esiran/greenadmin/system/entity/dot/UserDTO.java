package com.esiran.greenadmin.system.entity.dot;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String roleNames;
    private String roleIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
