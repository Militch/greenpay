package com.esiran.greenadmin.system.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode<T> {
    protected Integer id;

    protected Integer parentId;

    protected List<T> children = new ArrayList<>();
    public void add(T t){
        children.add(t);
    }
}
