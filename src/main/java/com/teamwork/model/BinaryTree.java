package com.teamwork.model;

import jdk.nashorn.internal.ir.Symbol;
import lombok.Data;

/**
 * @Author: Yivi
 * @Date: 2021/10/20 14:09
 * @describe:
 */
@Data()
public class BinaryTree {
    protected BinaryTree left;
    protected BinaryTree right;
    protected Fraction value;
    //  符号，如果当前节点为非叶子节点生效
    protected String symbol;
    // 符号集合
    public static final String Symbols[] = {"+", "-", "*", "÷"};

//    public BinaryTree (Fraction value, BinaryTree left, BinaryTree right, String symbol) {
//        this.value = value;
//        this.left = left;
//        this.right = right;
//        this.symbol = symbol;
//    }

    @Override
    public int hashCode() {
        int hash = value == null ? 0 : value.hashCode() * 31;
        hash += left == null ? 0 : left.hashCode();
        hash += right == null ? 0 : right.hashCode();
        hash += symbol == null ? 0 : symbol.hashCode();
        return hash;
    }

    /**
     * 判断是否需要加括号
     *
     * @param leftOrRight 左括号还是右括号，true 为 left , false 为 right
     * @param s1          符号1
     * @param s2          符号2
     */
    public static boolean isNeedBracket(String s1, String s2, boolean leftOrRight) {
        if (s2 == null) return false;
        if (s1.equals(Symbols[1])) {
            // 如果s1为减号，则当s2为加减且是右括号时才需要添加左括号
            if (s2.equals(Symbols[0]) || s2.equals(Symbols[1])) {
                if (!leftOrRight) {
                    return true;
                }
            }
        } else if (s1.equals(Symbols[2])) {
            // 如果s1为乘号，则当s2为加减时需要添加括号
            if (s2.equals(Symbols[0]) || s2.equals(Symbols[1])) {
                return true;
            }
        } else if (s1.equals(Symbols[3])) {
            // 如果s1为除号，则当s2为加减或者右括号时需要添加
            if (s2.equals(Symbols[0]) || s2.equals(Symbols[1])) {
                return true;
            } else if (!leftOrRight) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个二叉树是否相等
     *
     * @param other 另外一棵树
     */
    public boolean equals(BinaryTree other) {
        boolean isLeft = false, isRight = false, isValue = false, isSymbol = false;

        // 判断symbol
        if (other.symbol != null) {
            isSymbol = other.symbol.equals(symbol);
        } else {
            isSymbol = symbol == null;
        }

        // 判断value
        if (other.value != null) {
            isValue = other.value.equals(value);
        } else {
            isValue = value == null;
        }


        // 判断左右子树
        if (isValue && isSymbol && other.left != null && other.right != null) {
            isLeft = other.left.equals(left);
            isRight = other.right.equals(right);
            if ((other.symbol.equals(Symbols[0]) || other.symbol.equals(Symbols[2])) && !isLeft && !isRight) {
                isLeft = other.left.equals(right);
                isRight = other.right.equals(left);
            }
        } else {
            isLeft = left == null;
            isRight = right == null;
        }

        return isLeft && isRight && isValue && isSymbol;
    }


    /**
     * 中序遍历
     */

    public String midTraverse() {
        BinaryTree node = this;
        StringBuilder expression = new StringBuilder();

        if(node.symbol != null) {
            String symbol = node.symbol;
            String left = node.left.midTraverse();
            String right = node.right.midTraverse();
            if(isNeedBracket(symbol,node.left.symbol,true)){
                expression.append("( ").append(left+" ) ").append(symbol + " ");
            }else {
                expression.append(left+" ").append(symbol+" ");
            }

            if(isNeedBracket(symbol,node.right.symbol,false)) {
                expression.append("( ").append(right + " ) ");
            }else {
                expression.append(right);
            }
            return expression.toString();
        }else {
            return  node.value.toString();
        }
    }

}
