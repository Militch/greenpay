package com.esiran.greenpay.system.utils;

import com.esiran.greenpay.system.entity.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author han
 * @Package com.esiran.greenpay.common.util
 * @date 2020/5/20 9:42
 */
public class TreeUtil {

    public static <T extends TreeNode<T>> List<T>  buildByLoop(List<T> treeNodes, Integer root) {
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }

            for (T node : treeNodes) {
                if (node.getParentId().equals(treeNode.getId())) {
                    if (CollectionUtils.isEmpty(treeNode.getChildrens())) {
                        treeNode.setChildrens(new ArrayList<>());
                    }
                    treeNode.getChildrens().add(node);
                }
            }
        }

        return trees;
    }

    public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    public static <T extends TreeNode<T>> T findChildren(T treeNode, List<T> TreeNodes) {
        for (T node : TreeNodes) {
            if (treeNode.getId() == node.getParentId()) {
                if (CollectionUtils.isEmpty(treeNode.getChildrens())) {
                    treeNode.setChildrens(new ArrayList<>());
                }
            }
            treeNode.getChildrens().add(findChildren(node,TreeNodes));
        }

        return treeNode;
    }

}
