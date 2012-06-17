package me.tehbeard.utils.expressions;

/**
 * provides variables for an [InFixExpression]
 * @author James
 *
 */
public interface VariableProvider {

    
    public int resolveVariable(String var);
}
