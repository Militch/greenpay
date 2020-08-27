package com.esiran.greenadmin.system.entity.dot;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
@ApiModel("menuDto")
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
