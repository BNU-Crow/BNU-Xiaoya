package cn.xuhongxu.Assist;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * Result
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class Result {

    Result(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        setMessage(jsonObject.getString("message"));
        setResult(jsonObject.getString("result"));
        setStatus(jsonObject.getString("status"));
    }

    private String message = "";
    private String result = "";
    private String status = "";

    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    private void setMessage(String message) {
        this.message = message.trim();
    }

    private void setResult(String result) {
        this.result = result.trim();
    }

    private void setStatus(String status) {
        this.status = status.trim();
    }

    @Override
    public String toString() {
        return "Result{" +
                "message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
