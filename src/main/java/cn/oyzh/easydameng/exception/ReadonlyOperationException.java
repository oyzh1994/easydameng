package cn.oyzh.easydameng.exception;

/**
 * 只读操作异常，表示当前处于只读模式下执行了不被允许的写操作。
 *
 * @author oyzh
 * @since 2023/12/09
 */
public class ReadonlyOperationException extends DBException {

    /**
     * 使用默认提示信息构造异常。
     */
    public ReadonlyOperationException() {
        this("只读模式不支持此操作");
    }

    /**
     * 使用指定提示信息构造异常。
     *
     * @param msg 异常提示信息
     */
    public ReadonlyOperationException(String msg) {
        super(msg);
    }
}
