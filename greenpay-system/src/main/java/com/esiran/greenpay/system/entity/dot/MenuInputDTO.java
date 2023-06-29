package com.esiran.greenpay.system.entity.dot;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
public class MenuInputDTO {

    private Integer id;
    /**
     * 菜单标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "标识不能为为空")
    private String mark;

    /**
     * 菜单类型（1:目录,2:菜单,3:按钮）
     */
    @NotNull(message = "类型不能为空")
    private Integer type;

    /**
     * 目录图标
     */
    private String icon;

    /**
     * 菜单路由
     */
    @NotBlank(message = "路由不能为空")
    private String path;

    /**
     * 上级菜单ID
     */
    @NotNull(message = "上级菜单ID不能为空")
    private Integer parentId;

    /**
     * 排序权重
     */
    private Integer sorts;

    private String extra;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
