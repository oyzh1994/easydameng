package cn.oyzh.easydameng.dameng.trigger;

import cn.oyzh.easydameng.db.DBObjectList;

import java.util.List;

/**
 * db表触发器列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DamengTriggers extends DBObjectList<DamengTrigger> {

    public DamengTriggers() {

    }

    public DamengTriggers(List<DamengTrigger> list) {
        super.addAll(list);
    }
}
