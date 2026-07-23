package cn.oyzh.easydameng.exception;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.ssh.SSHException;

import java.util.function.Function;

/**
 * redis异常信息解析
 *
 * @author oyzh
 * @since 2023/7/2
 */
public class DBExceptionParser implements Function<Throwable, String> {

    /**
     * 当前实例
     */
    public final static DBExceptionParser INSTANCE = new DBExceptionParser();

    @Override
    public String apply(Throwable e) {
        if (e == null) {
            return null;
        }

        if (e instanceof DBException) {
            return e.getMessage();
        }

        if (e instanceof RuntimeException) {
            if (e.getCause() != null) {
                e = e.getCause();
            }
        }

        if (e instanceof SSHException e1) {
            if (StringUtil.contains(e.getMessage(), "Auth fail")) {
                return "ssh认证失败，请检查ssh用户名、密码是否正确";
            }
            return e1.getMessage();
        }

        String message = e.getMessage();

        if (e instanceof DBException) {
            return message;
        }

        if (e instanceof UnsupportedOperationException) {
            return message;
        }

        if (e instanceof IllegalArgumentException) {
            return message;
        }

        e.printStackTrace();
        return message;
    }
}
