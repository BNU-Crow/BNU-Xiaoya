package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * EncryptedParam
 *
 * @author Hongxu Xu
 * @version 0.1
 */
class EncryptedParam {
    private String params;
    private String token;
    private String timestamp;

    EncryptedParam(String params, String token, String timestamp) {
        this.params = params;
        this.token = token;
        this.timestamp = timestamp;
    }

    String getParams() {
        return params;
    }

    void setParams(String params) {
        this.params = params;
    }

    String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    String getTimestamp() {
        return timestamp;
    }

    void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "EncryptedParam{" +
                "params='" + params + '\'' +
                ", token='" + token + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
