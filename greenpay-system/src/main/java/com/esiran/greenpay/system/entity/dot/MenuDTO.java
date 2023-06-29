package com.esiran.greenpay.system.entity.dot;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
public class MenuDTO {

    private Integer id;
    private String title;
    private String mark;

    private Integer type;

    private String icon;

    private String path;

    private Integer parentId;

    private Integer sorts;

    private String extra;


    private String titleDisplay;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
