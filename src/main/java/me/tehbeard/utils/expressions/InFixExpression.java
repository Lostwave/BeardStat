package me.tehbeard.utils.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InFixExpression {
    
    List<String> expr = new ArrayList<String>();
    
    public InFixExpression(String expr){
        for(String exp : convertInfixToPostfix(expr).split(" ")){
            if(exp.length() > 0){
                this.expr.add(exp);
            }
        }
    }
    
    private static String convertInfixToPostfix(String infix)
    {
        int length = infix.length();
        Stack<Character> stack = new Stack<Character>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i < length; i++)
        {
            if (infix.charAt(i) == '(')
            {
                stack.push(infix.charAt(i));
                postfix.append(" ");
            }
            else if ((infix.charAt(i) == '*') || (infix.charAt(i) == '+') || (infix.charAt(i) == '-') || (infix.charAt(i) == '/'))
            {
                while ((stack.size() > 0) && (stack.peek() != '('))
                {
                    if (comparePrecedence(stack.peek(), infix.charAt(i)))
                    {
                        postfix.append(" " + stack.pop() + " ");
                    }
                    else
                    {
                        break;
                    }
                }
                stack.push(infix.charAt(i));
                postfix.append(" ");
            }
            else if (infix.charAt(i) == ')')
            {
                while ((stack.size() > 0) && (stack.peek() != '('))
                {
                    postfix.append(" " + stack.pop() + " ");
                }
                if (stack.size() > 0)
                    stack.pop(); // popping out the left brace '('
            }
            else
            {
                if(infix.charAt(i)!=' '){
                    postfix.append(infix.charAt(i));
                }
            }
        }
        while (stack.size() > 0)
        {
            postfix.append(" " + stack.pop() + " ");
        }
        return postfix.toString().replaceAll("  ", " ");
    }

    private static boolean comparePrecedence(char top, char p_2)
    {
        if (top == '+' && p_2 == '*') // + has lower precedence than *
            return false;

        if (top == '*' && p_2 == '-') // * has higher precedence over -
            return true;

        if (top == '+' && p_2 == '-') // + has same precedence over +
            return true;

        return true;
    }

    
    public int getValue(VariableProvider provider){
        Stack<Integer> stack = new Stack<Integer>();  
        for(String exp : expr){
            int i = 0;
            switch(exp.charAt(0)){
            case '+':i = stack.pop(); stack.push(stack.pop() + i);break;
            case '-':i = stack.pop(); stack.push(stack.pop() - i);break;
            case '*':i = stack.pop(); stack.push(stack.pop() * i);break;
            case '/':i = stack.pop(); stack.push(stack.pop() / i);break;
            
            case '$':
                String stat = exp.substring(1);
                stack.push(provider.resolveVariable(stat));
                
                ;break;
            default: stack.push(Integer.parseInt(exp));break;
            }
        }
        if(stack.size() == 0){throw new IllegalArgumentException("Expression resulted in a empty stack!");}
        return stack.pop();
    }
}
