package cn.oyzh.easydameng.dameng.index;

import cn.oyzh.easydameng.data.db.DBObjectList;

import java.util.Collection;

/**
 * db表索引
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class DamengIndexes extends DBObjectList<DamengIndex> {

    public DamengIndexes() {

    }

    public DamengIndexes(Collection<DamengIndex> list) {
        super.addAll(list);
    }
}
