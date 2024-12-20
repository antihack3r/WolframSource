/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorClass;

public class ReflectorMethod {
    private ReflectorClass reflectorClass = null;
    private String targetMethodName = null;
    private Class[] targetMethodParameterTypes = null;
    private boolean checked = false;
    private Method targetMethod = null;

    public ReflectorMethod(ReflectorClass p_i90_1_, String p_i90_2_) {
        this(p_i90_1_, p_i90_2_, null, false);
    }

    public ReflectorMethod(ReflectorClass p_i91_1_, String p_i91_2_, Class[] p_i91_3_) {
        this(p_i91_1_, p_i91_2_, p_i91_3_, false);
    }

    public ReflectorMethod(ReflectorClass p_i92_1_, String p_i92_2_, Class[] p_i92_3_, boolean p_i92_4_) {
        this.reflectorClass = p_i92_1_;
        this.targetMethodName = p_i92_2_;
        this.targetMethodParameterTypes = p_i92_3_;
        if (!p_i92_4_) {
            Method method = this.getTargetMethod();
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public Method getTargetMethod() {
        if (this.checked) {
            return this.targetMethod;
        }
        this.checked = true;
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            if (this.targetMethodParameterTypes == null) {
                Method[] amethod = ReflectorMethod.getMethods(oclass, this.targetMethodName);
                if (amethod.length <= 0) {
                    Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                    return null;
                }
                if (amethod.length > 1) {
                    Config.warn("(Reflector) More than one method found: " + oclass.getName() + "." + this.targetMethodName);
                    int i = 0;
                    while (i < amethod.length) {
                        Method method = amethod[i];
                        Config.warn("(Reflector)  - " + method);
                        ++i;
                    }
                    return null;
                }
                this.targetMethod = amethod[0];
            } else {
                this.targetMethod = ReflectorMethod.getMethod(oclass, this.targetMethodName, this.targetMethodParameterTypes);
            }
            if (this.targetMethod == null) {
                Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                return null;
            }
            this.targetMethod.setAccessible(true);
            return this.targetMethod;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public boolean exists() {
        if (this.checked) {
            return this.targetMethod != null;
        }
        return this.getTargetMethod() != null;
    }

    public Class getReturnType() {
        Method method = this.getTargetMethod();
        return method == null ? null : method.getReturnType();
    }

    public void deactivate() {
        this.checked = true;
        this.targetMethod = null;
    }

    public static Method getMethod(Class p_getMethod_0_, String p_getMethod_1_, Class[] p_getMethod_2_) {
        Method[] amethod = p_getMethod_0_.getDeclaredMethods();
        int i = 0;
        while (i < amethod.length) {
            Class[] aclass;
            Method method = amethod[i];
            if (method.getName().equals(p_getMethod_1_) && Reflector.matchesTypes(p_getMethod_2_, aclass = method.getParameterTypes())) {
                return method;
            }
            ++i;
        }
        return null;
    }

    public static Method[] getMethods(Class p_getMethods_0_, String p_getMethods_1_) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] amethod = p_getMethods_0_.getDeclaredMethods();
        int i = 0;
        while (i < amethod.length) {
            Method method = amethod[i];
            if (method.getName().equals(p_getMethods_1_)) {
                list.add(method);
            }
            ++i;
        }
        Method[] amethod1 = list.toArray(new Method[list.size()]);
        return amethod1;
    }
}

