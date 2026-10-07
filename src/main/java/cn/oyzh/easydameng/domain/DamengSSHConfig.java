package cn.oyzh.easydameng.domain;

import cn.oyzh.ssh.domain.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * 达梦数据库 SSH 连接配置。
 *
 * @author oyzh
 * @since 2024-09-26
 */
@Table("t_ssh_config")
public class DamengSSHConfig extends SSHConnect implements Serializable {

    /**
     * 连接id
     * @see DamengConnect
     */
    @Column
    @PrimaryKey
    private String iid;

    /**
     * 获取连接id
     *
     * @return 连接id
     */
    public String getIid() {
        return iid;
    }

    /**
     * 设置连接id
     *
     * @param iid 连接id
     */
    public void setIid(String iid) {
        this.iid = iid;
    }
}
