package com.shilgapira.funkyval;

import java.util.Map;

/**
 * A {@code Funkyval) that evaluates an expression such as "80", "foo", "-5", etc.
 *
 * @author Gil Shapira
 */
public class ValueFunkyval extends Funkyval {
    
    private String mValue;
    
    
    public ValueFunkyval(String value) {
        mValue = value;
    }
    
    public String getString() {
        return mValue;
    }

    @Override
    public String evaluateString(Map<String, String> variables) {
        String varValue = variables.get(mValue.toLowerCase());
        if (varValue != null) {
            // Log.d(TAG, "VALUE, Result (R): " + varValue + " (value of: " + mValue + ")");
            return varValue;
        }
        
        // Log.d(TAG, "VALUE, Result (V): " + mValue); 
        return mValue;
    }

}
