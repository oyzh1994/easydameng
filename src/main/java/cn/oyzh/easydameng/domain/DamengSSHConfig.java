package cn.oyzh.easydameng.domain;

import cn.oyzh.ssh.domain.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
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

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }
}
