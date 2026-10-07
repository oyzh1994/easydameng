package cn.oyzh.easydameng.exception;

/**
 * Shell 命令执行异常，表示远程命令执行过程中出现的异常。
 *
 * @author oyzh
 * @since 2025/03/21
 */
public class ShellException extends RuntimeException {

    /**
     * 构造无提示信息的异常。
     */
    public ShellException() {
        super();
    }

    /**
     * 使用指定提示信息构造异常。
     *
     * @param message 异常提示信息
     */
    public ShellException(String message) {
        super(message);
    }

    /**
     * 使用指定异常作为原因构造异常。
     *
     * @param ex 原始异常
     */
    public ShellException(Throwable ex) {
        super(ex);
    }
}
