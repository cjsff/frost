package com.cjsff.transport;

/**
 * @author cjsff
 */
public class FrpcResponse extends Packet{

    private String id;
    private String result;
    private String error;

    public FrpcResponse() {
    }

    public FrpcResponse(String id, String result, String error) {
        this.id = id;
        this.result = result;
        this.error = error;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isError() {
        return error != null;
    }
}
