package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/6.
 *
 * NeedLoginException
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class NeedLoginException extends Exception {
    NeedLoginException(String msg) {
        super(msg);
    }

    NeedLoginException() {
        super();
    }
}
