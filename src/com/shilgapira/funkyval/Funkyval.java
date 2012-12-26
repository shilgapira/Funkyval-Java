package com.shilgapira.funkyval;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Evaluates arithmetic, boolean and string expressions with support for 
 * getting and setting values from variables.</p>
 * 
 * <p>Examples:</p>
 * <ul>
 * <li> open == true
 * <li> open = false
 * <li> !open
 * <li> state = (8 * 4)
 * <li> state++
 * <li> state >= 1
 * <li> (state >= 1) && (open == true)
 * <li> state = 0, open = false
 * <li> (number % 2) == 1
 * <li> (2 + 2) == 4
 * </ul>
 *
 * @author Gil Shapira
 */
public abstract class Funkyval {
    
    //
    // Creation
    //
    
    /**
     * Builds a {@code Funkyval} object from an expression. Doesn't support operator
     * precedence atm, so complex expressions need liberal amounts of parenthesis.
     */
    public static Funkyval fromExpression(String expression) {
        return buildFunkyval(expression);
    }
    
    //
    // Evaluating
    //
    
    public abstract String evaluateString(Map<String, String> variables);
    
    public int evaluateInteger(Map<String, String> variables) {
        String evaluation = evaluateString(variables);
        try {
            return Integer.parseInt(evaluation);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public boolean evaluateBoolean(Map<String, String> variables) {
        String evaluation = evaluateString(variables);
        return (evaluation.equalsIgnoreCase("1") || 
                evaluation.equalsIgnoreCase("yes") || 
                evaluation.equalsIgnoreCase("true"));
    }
    
    public void perform(Map<String, String> variables) {
        evaluateString(variables);
    }
    
    //
    // Null placeholder
    //
    
    static final Funkyval NULL = new Funkyval() {
        @Override
        public String evaluateString(Map<String, String> variables) {
            // Log.d(TAG, "NULL, Result (S): 0"); 
            return "0";
        }
    };
    
    //
    // Builder
    //
    
    private static Funkyval buildFunkyval(String expression) {
        if (expression == null) {
            return NULL;
        }
        if (expression.indexOf(",") != -1) {
            String[] strings = expression.split(",");
            List<Funkyval> funkyvals = new LinkedList<Funkyval>();
            for (String string : strings) {
                funkyvals.add(buildFromString(string));
            }
            return new GroupFunkyval(funkyvals);
        } else {
            return buildFromString(expression);
        }
    }
    
    private static Funkyval buildFromString(String string) {
        char[] chars = string.toCharArray();
        return buildFromSubstring(chars, 0, chars.length);
    }
    
    private static Funkyval buildFromSubstring(char[] chars, int start, int end) {
        Funkyval[] funks = new Funkyval[3];
        int count = 0;
        
        // scan the range of the string looking for at most 3 funkyvals, e.g.,
        // "a + b", "a ++", "a == b", "!a"
        for (int i = start; i < end && count < 3; i++) {
            char c = chars[i];
            
            if (c == ' ') {
                // skip whitespace
                continue;
            }
            
            if (Character.isLetterOrDigit(c) || c == '_' || (count != 1 && c == '-')) {
                // values, such as 'foo', '15', '-8', 'bar19', etc
                StringBuilder valueBuilder = new StringBuilder(1);
                valueBuilder.append(c);
                
                int len = 1;
                while (i + len < end && (Character.isLetterOrDigit(chars[i + len]) || (chars[i + len] == '_'))) {
                    valueBuilder.append(chars[i + len]);
                    len++;
                }
                i += len - 1;
                
                funks[count] = new ValueFunkyval(valueBuilder.toString());
                count++;
            } else if (c == '(') {
                // parenthesis, we simply build recursively on what's inside them.
                // we might pass other parenthesis on the way though so we track the depth 
                int depth = 0;
                // subexpression starts 1 char after the '(' and 1 char before the ')'
                int substart = i + 1;
                while (i < end) {
                    char k = chars[i];
                    
                    if (k == ')') {
                        depth--;
                    } else if (k == '(') {
                        depth++;
                    }
                    
                    if (depth == 0) {
                        // some sanity checking
                        if (substart < i) {
                            funks[count] = buildFromSubstring(chars, substart, i);
                            count++;
                        }
                        break;
                    } else {
                        // only increment if we're not at the end yet
                        i++;
                    }
                }
            } else {
                // operator, such as '=', '++', '!=', '&', etc 
                StringBuilder valueBuilder = new StringBuilder(1);
                valueBuilder.append(c);

                if (i + 1 < end) {
                    char k = chars[i + 1];
                    if (k == '=' || (k == '+' && c == '+') || (k == '-' && c == '-') || (k == '|' && c == '|') || (k == '&' && c == '&')) {
                        valueBuilder.append(k);
                        i++;
                    }
                }
                
                funks[count] = new OperatorFunkyval(valueBuilder.toString());
                count++;
            }
        }

        if (count == 0) {
            // empty expression
            return Funkyval.NULL;
        } else if (count == 1) {
            // 1 funkyval, can be anything really
            return funks[0];
        } else if (count == 2) {
            // 2 funkyvals, one of them should be an operator
            if (funks[0] instanceof OperatorFunkyval) {
                OperatorFunkyval op = (OperatorFunkyval) funks[0];
                op.setRight(funks[1]);
                return op;
            } else if (funks[1] instanceof OperatorFunkyval) {
                OperatorFunkyval op = (OperatorFunkyval) funks[1];
                op.setLeft(funks[0]);
                return op;
            } else {
                return Funkyval.NULL;
            }
        } else {
            // 3 funkyvals, middle one must be an operator
            if (funks[1] instanceof OperatorFunkyval) {
                OperatorFunkyval op = (OperatorFunkyval) funks[1];
                op.setLeft(funks[0]);
                op.setRight(funks[2]);
                return op;
            } else {
                return Funkyval.NULL;
            }
        }
    }
    
}
