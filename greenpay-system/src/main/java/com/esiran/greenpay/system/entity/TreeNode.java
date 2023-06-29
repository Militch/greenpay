package com.esiran.greenpay.system.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author han
 * @Package com.esiran.greenpay.system.entity
 * @date 2020/5/20 9:38
 */
@Data
public class TreeNode<T> {
    protected Integer id;

    protected Integer parentId;

    protected List<T> childrens = new ArrayList<>();

}
