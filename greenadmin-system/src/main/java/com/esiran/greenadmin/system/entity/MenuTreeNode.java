package com.esiran.greenadmin.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeNode extends TreeNode<MenuTreeNode> {
    private String title;
    private String mark;

    private Integer type;
    private String icon;

    private String path;
    private Integer sorts;

    private String extra;
}
