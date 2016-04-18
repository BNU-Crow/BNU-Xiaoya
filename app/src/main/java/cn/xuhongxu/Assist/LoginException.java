package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/7.
 *
 * LoginException
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class LoginException extends Exception {
    LoginException(String msg) {
        super(msg);
    }

    LoginException() {
        super();
    }
}
