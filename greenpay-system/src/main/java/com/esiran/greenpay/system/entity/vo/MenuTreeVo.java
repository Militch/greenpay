package com.esiran.greenpay.system.entity.vo;

import com.esiran.greenpay.system.entity.TreeNode;
import lombok.Data;

/**
 * @author han
 * @Package com.esiran.greenpay.system.entity.vo
 * @date 2020/5/20 9:38
 */
@Data
public class MenuTreeVo extends TreeNode<MenuTreeVo> {

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单标识
     */
    private String mark;

    /**
     * 菜单类型（1:目录,2:菜单,3:按钮）
     */
    private Integer type;

    /**
     * 目录图标
     */
    private String icon;

    /**
     * 菜单路由
     */
    private String path;

    /**
     * 上级菜单ID
     */
    private Integer parentId;

    /**
     * 排序权重
     */
    private Integer sorts;

    private String extra;
}
