package com.teamwork.model;

import lombok.Data;

import java.util.Stack;

/**
 * @Author: Yivi
 * @Date: 2021/10/20 15:22
 * @describe:
 */
@Data
public class Expression {
    public static final String Symbols[] = {"+", "-", "*", "÷"};
    public static final int MAX = 3;

    private BinaryTree root;
    private String expression;
    private Fraction value;

    public Expression() {
        super();
    }


    /**
     * 根据最大值以及最大计算符号个数随机生成一棵表达式二叉树
     */
    public BinaryTree generateBinaryTree(int maxNum, int maxSymbol) {
        BinaryTree root = new BinaryTree();
        if (maxSymbol == 0) {
            root.value = Fraction.newRandomFraction(maxNum);
        } else {
            root.symbol = Symbols[(int) (Math.random() * 4)];
            int leaveSymbolNum = maxSymbol - 1;
            int symbolNumToLeft = (int) (Math.random() * (leaveSymbolNum + 1));
            root.left = generateBinaryTree(maxNum, symbolNumToLeft);
            root.right = generateBinaryTree(maxNum, leaveSymbolNum - symbolNumToLeft);
        }
        return root;
    }

    /**
     * 根据传入的二叉树表达式获取结果
     */
    public Fraction getResult(BinaryTree root) {
        if (root == null) return new Fraction(0, 1);
        if (root.left == null && root.right == null) {
            return root.value;
        }
        String symbol = root.symbol;
        Fraction leftResult = getResult(root.left);
        Fraction rightResult = getResult(root.right);
        root.value = operate(leftResult, rightResult, symbol);
        // 如果结果小于0,交换子树
        if (root.value.getMolecular() < 0) {
            root.value.setMolecular(-root.value.getMolecular());
            BinaryTree node = root.left;
            root.left = root.right;
            root.right = node;
        }
        return root.value;
    }


    /**
     * 计算函数
     */
    private Fraction operate(Fraction left, Fraction right, String symbol) {
        switch (symbol) {
            case "+": {
                left.addition(right);
                break;
            }
            case "-": {
                left.subtraction(right);
                break;
            }
            case "*": {
                left.multiplication(right);
                break;
            }
            case "÷": {
                left.division(right);
                break;
            }
            default: {
                return new Fraction(0, 1);
            }
        }
        return left;
    }

    /**
     * 根据最大值来随机生成表达式
     */

    public Expression(int maxNum) {
        // 随机确定符号数
        int symbolNum = (int) (Math.random() * MAX) + 1;
        root = generateBinaryTree(maxNum, symbolNum);
        expression = root.midTraverse() + "=";
        value = getResult(root);
    }

    /**
     * 根据中缀表达式生成后缀表达式
     */
    public static String expressionToPostfix(String expression) {
        Stack<String> stack = new Stack<>();
        StringBuilder queue = new StringBuilder();
        int index = 0;
        String[] s = expression.split(" ");
        while (index < s.length) {
            String c = s[index];
            // 如果是数字，就入队列
            if (!isOperator(c) && !c.equals("(") && !c.equals(")")) {
                queue.append(s[index] + " ");
            // 如果是左括号，就入栈
            } else if (c.equals("(")) {
                stack.push(c);
            // 如果是右括号，就弹出栈中的元素，直到遇到左括号为止。左右括号均不入队列
            } else if (c.equals(")")) {
                while (!"(".equals(stack.peek())) {
                    queue.append(stack.pop() + " ");
                }
                // 弹出左括号
                stack.pop();
                // 如果是运算符，分下面的情况讨论
            } else if (isOperator(c)) {
                // 如果符号栈为空，就直接压入栈
                if (stack.isEmpty()) {
                    stack.push(c);
                    // 如果符号栈的栈顶是左括号，则压入栈中
                } else if ("C".equals(stack.peek())) {
                    stack.push(c);
                    // 如果当前元素的优先级比符号栈的栈顶元素优先级高，则压入栈中
                } else if (priority(c) > priority(stack.peek())) {
                    stack.push(c);
                    // 如果此时遍历的运算符的优先级小于等于此时符号栈栈顶的运算符的优先级，
                    // 则将符号栈的栈顶元素弹出并且放到队列中，并且将正在遍历的符号压入符号栈
                } else if (priority(c) <= priority(stack.peek())) {
                    queue.append(stack.pop() + " ");
                    stack.push(c);
                }
            }

            index++;
        }

        // 遍历完后，将栈中元素全部弹出到队列中
        while (!stack.isEmpty()) {
            queue.append(stack.pop() + " ");
        }
        return queue.toString();
    }

    /**
     * 由后缀表达式计算结果
     * */
    public static String getPostfixResult(String postfix) {
        String[] items = postfix.split(" ");
        Stack<String> stack = new Stack();
        for(int i=0;i<items.length;i++) {
            if(isOperator(items[i])) {
                Fraction a = new Fraction(stack.pop());
                Fraction b = new Fraction(stack.pop());
                switch (items[i]) {
                    case "+" : {
                        a.addition(b);
                        stack.push(a.toString());
                        break;
                    }
                    case "-" : {
                        b.subtraction(a);
                        stack.push(b.toString());
                        break;
                    }
                    case "*" : {
                        a.multiplication(b);
                        stack.push(a.toString());
                        break;
                    }
                    case "÷"  :{
                        b.division(a);
                        stack.push(b.toString());
                        break;
                    }
                    default: {
                        System.out.println("表达式有误，请检查");
                    }
                }
                System.out.println(a);
            }else {
                stack.push(items[i]);
            }
        }
        return stack.pop();
    }


    private static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("÷");
    }

    private static int priority(String symbol) {
        switch (symbol) {
            case "*":
            case "÷":
                return 3;
            case "+":
            case "-":
                return 2;
            case "(":
                return 1;
            default:
                return 0;

        }
    }

    @Override
    public int hashCode() {
        return root.hashCode() + value.hashCode();
    }
}
