/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.src.Config;
import net.optifine.entity.model.anim.ExpressionParser;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.ModelVariable;
import net.optifine.entity.model.anim.ParseException;

public class ModelVariableUpdater {
    private String modelVariableName;
    private String expressionText;
    private ModelVariable modelVariable;
    private IExpression expression;

    public boolean initialize(IModelResolver mr) {
        this.modelVariable = mr.getModelVariable(this.modelVariableName);
        if (this.modelVariable == null) {
            Config.warn("Model variable not found: " + this.modelVariableName);
            return false;
        }
        try {
            ExpressionParser expressionparser = new ExpressionParser(mr);
            this.expression = expressionparser.parse(this.expressionText);
            return true;
        }
        catch (ParseException parseexception) {
            Config.warn("Error parsing expression: " + this.expressionText);
            Config.warn(String.valueOf(parseexception.getClass().getName()) + ": " + parseexception.getMessage());
            return false;
        }
    }

    public ModelVariableUpdater(String modelVariableName, String expressionText) {
        this.modelVariableName = modelVariableName;
        this.expressionText = expressionText;
    }

    public void update() {
        float f = this.expression.eval();
        this.modelVariable.setValue(f);
    }
}

