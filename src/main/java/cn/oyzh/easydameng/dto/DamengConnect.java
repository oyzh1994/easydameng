package cn.oyzh.easydameng.dto;


/**
 * 达梦数据库连接对象，保存连接所需的地址、端口与账号信息。
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class DamengConnect {

    /**
     * 地址
     */
    private String host = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 5236;

    /**
     * 用户
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * db索引
     */
    private int db = 0;

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置用户名
     *
     * @param user 用户名
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取数据库编号
     *
     * @return 数据库编号
     */
    public int getDb() {
        return db;
    }

    /**
     * 设置数据库编号
     *
     * @param db 数据库编号
     */
    public void setDb(int db) {
        this.db = db;
    }
}
