package com.shilgapira.funkyval;

import java.util.List;
import java.util.Map;

/**
 * A {@code Funkyval) that groups together several disjointed expressions.
 * It evaluates all expressions and returns the result of the first one.
 *
 * @author Gil Shapira
 */
public class GroupFunkyval extends Funkyval {
    
    private List<Funkyval> mFunkyvals;
    
    
    public GroupFunkyval(List<Funkyval> funkyvals) {
        mFunkyvals = funkyvals;
    }

    @Override
    public String evaluateString(Map<String, String> variables) {
        String result = null;
        for (Funkyval funkyval : mFunkyvals) {
            String value = funkyval.evaluateString(variables);
            if (result == null) {
                result = value; 
            }
        }
        // Log.d(TAG, "GROUP, Result (S): " + result); 
        return result;
    }

}
