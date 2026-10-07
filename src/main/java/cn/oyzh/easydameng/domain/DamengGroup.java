package cn.oyzh.easydameng.domain;


import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.plus.domain.AppGroup;
import cn.oyzh.store.jdbc.Table;

/**
 * 达梦数据库连接分组。
 *
 * @author oyzh
 * @since 2023/12/15
 */
@Table("t_group")
public class DamengGroup extends AppGroup implements ObjectComparator<DamengGroup> {

    /**
     * 构造默认分组
     */
    public DamengGroup() {
        super();
    }

    /**
     * 构造分组
     *
     * @param name    分组名称
     * @param groupId 父分组id
     * @param expand  是否展开
     */
    public DamengGroup(String name, String groupId, boolean expand) {
        super(name, groupId, expand);
    }

    @Override
    public boolean compare(DamengGroup t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.getName(), t1.getName());
    }
}
