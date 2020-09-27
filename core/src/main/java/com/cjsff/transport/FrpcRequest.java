package com.cjsff.transport;

/**
 * @author cjsff
 */
public class FrpcRequest extends Packet{
    /**
     * 请求id
     */
    private String id;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public FrpcRequest() {
    }

    public FrpcRequest(String id, String className, String methodName, Class<?>[] paramTypes, Object[] params) {
        this.id = id;
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
