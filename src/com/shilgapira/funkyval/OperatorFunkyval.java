package com.shilgapira.funkyval;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code Funkyval) that evaluates an expression such as "a + 5", "a++",
 * "flag && enabled", etc.
 *
 * @author Gil Shapira
 */
public class OperatorFunkyval extends Funkyval {
    
    private Operator mOperator;
    
    private Funkyval mLeft;
    
    private Funkyval mRight;
    
    
    public OperatorFunkyval(String operator) {
        this(sOperatorStrings.get(operator));
    }
    
    private OperatorFunkyval(Operator operator) {
        mOperator = operator;
        if (mOperator == null) {
            mOperator = Operator.NOOP;
        }
        mLeft = Funkyval.NULL;
        mRight = Funkyval.NULL;
    }
    
    public void setLeft(Funkyval left) {
        mLeft = left;
    }
    
    public void setRight(Funkyval right) {
        mRight = right;
    }
    
    @Override
    public String evaluateString(Map<String, String> variables) {
        if (mLeft == Funkyval.NULL && mRight == Funkyval.NULL) {
            return Funkyval.NULL.evaluateString(variables);
        }
        
        switch (mOperator) {
            case PLUSPLUS:
            case MINUSMINUS:
            case PLUSASSIGN:
            case MINUSASSIGN:
            case MULTASSIGN:
            case DIVASSIGN:
            case MODASSIGN:
                convertComplexOperator();
                // fall down to evaluate assign after conversion
            case ASSIGN:
                String assignResult = evalAssign(variables);
                // Log.d(TAG, mOperator.name() + ", Result (S):" + assignResult); 
                return assignResult;
                
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
            case MOD:
                int arithResult = evalArithmetic(variables);
                // Log.d(TAG, mOperator.name() + ", Result (I): " + arithResult); 
                return String.valueOf(arithResult);
            
            case EQUALS:
            case NOTEQUALS:
                boolean equalResult = evalEqual(variables);
                // Log.d(TAG, mOperator.name() + ", Result (B): " + compResult); 
                return equalResult ? "1" : "0";
                
            case GREATER:
            case GREATEREQUALS:
            case LESS:
            case LESSEQUALS:
                boolean compResult = evalCompare(variables);
                // Log.d(TAG, mOperator.name() + ", Result (B): " + compResult); 
                return compResult ? "1" : "0";
            
            case AND:
            case OR:
            case NOT:
                boolean boolResult = evalBoolean(variables);
                // Log.d(TAG, mOperator.name() + ", Result (B): " + boolResult); 
                return boolResult ? "1" : "0";
            
            default:
                return Funkyval.NULL.evaluateString(variables);
        }
    }
    
    private String evalAssign(Map<String, String> variables) {
        String rightValue = mRight.evaluateString(variables);
        // Log.d(TAG, mOperator.name() + ", Right (S): " + rightValue); 
        // use left side of the assignment as an lvalue
        if (mLeft instanceof ValueFunkyval) {
            ValueFunkyval left = (ValueFunkyval) mLeft;
            String variable = left.getString().toLowerCase();
            // the value itself (not the result of its evaluation) is
            // the name of the variable
            // Log.d(TAG, mOperator.name() + ", Left (A): " + variable); 
            variables.put(variable, rightValue);
        }
        return rightValue;
    }
    
    private int evalArithmetic(Map<String, String> variables) {
        int left = mLeft.evaluateInteger(variables);
        int right = mRight.evaluateInteger(variables);
        // Log.d(TAG, mOperator.name() + ", Left (I): " + left); 
        // Log.d(TAG, mOperator.name() + ", Right (I): " + right); 
        switch (mOperator) {
            case PLUS: return left + right;
            case MINUS: return left - right;
            case MULT: return left * right;
            case DIV: return left / right;
            case MOD: return left % right;
            default: return 0;
        }
    }

    private boolean evalEqual(Map<String, String> variables) {
        String ls = mLeft.evaluateString(variables);
        String rs = mRight.evaluateString(variables);
        // Log.d(TAG, mOperator.name() + ", Left (S): " + ls); 
        // Log.d(TAG, mOperator.name() + ", Right (S): " + rs);
        
        if (ls.equalsIgnoreCase("yes") || ls.equalsIgnoreCase("true")) {
            ls = "1";
        }
        if (rs.equalsIgnoreCase("yes") || rs.equalsIgnoreCase("true")) {
            rs = "1";
        }
        
        boolean cs = ls.equalsIgnoreCase(rs);
        if (mOperator == Operator.NOTEQUALS) {
            cs = !cs;
        }
        return cs;
    }
    
    private boolean evalCompare(Map<String, String> variables) {
        int li = mLeft.evaluateInteger(variables);
        int ri = mRight.evaluateInteger(variables);
        int ci = li - ri;
        // Log.d(TAG, mOperator.name() + ", Left (I): " + li); 
        // Log.d(TAG, mOperator.name() + ", Right (I): " + ri); 
      
        switch (mOperator) {
            case GREATER: return ci > 0;
            case GREATEREQUALS: return ci >= 0;
            case LESS: return ci < 0;
            case LESSEQUALS: return ci <= 0;
            default: return false;
        }
    }
    
    private boolean evalBoolean(Map<String, String> variables) {
        boolean right = mRight.evaluateBoolean(variables);
        // Log.d(TAG, mOperator.name() + ", Right (B): " + right); 
        if (mOperator == Operator.NOT) {
            return !right;
        } else {
            boolean left = mLeft.evaluateBoolean(variables);
            // Log.d(TAG, mOperator.name() + ", Left (B): " + left); 
            if (mOperator == Operator.AND) {
                return right && left;
            } else {
                return right || left;
            }
        }
    }
    
    private void convertComplexOperator() {
        Operator suboperator;
        Funkyval subright;

        switch (mOperator) {
            case PLUSPLUS:
                suboperator = Operator.PLUS;
                subright = new ValueFunkyval("1");
                break;
                
            case MINUSMINUS:
                suboperator = Operator.MINUS;
                subright = new ValueFunkyval("1");
                break;
                
            case PLUSASSIGN:
                suboperator = Operator.PLUS;
                subright = mRight;
                break;
                
            case MINUSASSIGN:
                suboperator = Operator.MINUS;
                subright = mRight;
                break;
                
            case MULTASSIGN:
                suboperator = Operator.MULT;
                subright = mRight;
                break;
                
            case DIVASSIGN:
                suboperator = Operator.DIV;
                subright = mRight;
                break;
                
            case MODASSIGN:
                suboperator = Operator.MOD;
                subright = mRight;
                break;
                
            default:
                mOperator = Operator.NOOP;
                return;
        }
        
        mOperator = Operator.ASSIGN;
        OperatorFunkyval right = new OperatorFunkyval(suboperator);
        right.setLeft(mLeft);
        right.setRight(subright);
        mRight = right;
    }
    
    //
    // Operators
    //
    
    private enum Operator {
        ASSIGN("="),
        PLUS("+"),
        PLUSPLUS("++"),
        PLUSASSIGN("+="),
        MINUS("-"),
        MINUSMINUS("--"),
        MINUSASSIGN("-="),
        MULT("*"),
        MULTASSIGN("*="),
        DIV("/"),
        DIVASSIGN("/="),
        MOD("%"),
        MODASSIGN("%="),
        EQUALS("=="),
        NOTEQUALS("!="),
        GREATER(">"),
        GREATEREQUALS(">="),
        LESS("<"),
        LESSEQUALS("<="),
        AND("&&"),
        OR("||"),
        NOT("!"),
        NOOP("");
        
        final String mKey;
        
        Operator(String key) {
            mKey = key;
        }
    }
    
    private static final Map<String, Operator> sOperatorStrings;
    
    static {
        sOperatorStrings = new HashMap<String, OperatorFunkyval.Operator>();
        for (Operator op : Operator.values()) {
            sOperatorStrings.put(op.mKey, op);
        }
    }

}
