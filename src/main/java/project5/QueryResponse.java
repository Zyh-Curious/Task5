package project5;

public class QueryResponse {
    private String expressKey;
    private String msg;

    public QueryResponse() {
    }

    public QueryResponse(String msg, String expressKey) {
        this.msg = msg;
        this.expressKey = expressKey;
    }



    public String getExpressKey() {
        return expressKey;
    }

    public void setExpressKey(String expressKey) {
        this.expressKey = expressKey;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
