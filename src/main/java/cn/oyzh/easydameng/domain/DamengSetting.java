package cn.oyzh.easydameng.domain;


import cn.oyzh.fx.plus.domain.AppSetting;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;

/**
 * 达梦数据库客户端设置。
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Table("t_setting")
public class DamengSetting extends AppSetting {

    /**
     * 记录每页限制
     */
    @Column
    private Integer recordPageLimit;

    /**
     * 设置记录每页限制，非法值将被重置为默认值 100
     *
     * @param recordPageLimit 记录每页限制
     */
    public void setRecordPageLimit(Integer recordPageLimit) {
        if (recordPageLimit == null || recordPageLimit <= 0) {
            this.recordPageLimit = 100;
        } else {
            this.recordPageLimit = recordPageLimit;
        }
    }

    /**
     * 获取记录每页限制，未设置或非法时返回默认值 100
     *
     * @return 记录每页限制
     */
    public Integer getRecordPageLimit() {
        if (this.recordPageLimit == null || this.recordPageLimit <= 0) {
            return 100;
        }
        return this.recordPageLimit;
    }
}
