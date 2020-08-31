package com.esiran.greenadmin.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleSelectVo {
    private Integer id;
    private String name;
    private String roleCode;
    private boolean checked;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
