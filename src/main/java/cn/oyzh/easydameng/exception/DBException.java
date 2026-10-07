package cn.oyzh.easydameng.exception;

/**
 * 达梦数据库异常，统一表示数据库操作过程中出现的异常。
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class DBException extends RuntimeException {

    /**
     * 构造无提示信息的异常。
     */
    public DBException() {
        super();
    }

    /**
     * 使用指定提示信息构造异常。
     *
     * @param message 异常提示信息
     */
    public DBException(String message) {
        super(message);
    }

    /**
     * 使用指定异常作为原因构造异常。
     *
     * @param ex 原始异常
     */
    public DBException(Exception ex) {
        super(ex);
    }
}
