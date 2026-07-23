package cn.oyzh.easydameng.dameng.event;

import cn.oyzh.easydameng.db.DBObjectList;

import java.util.List;

/**
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DamengEvents extends DBObjectList<DamengEvent> {

    public DamengEvents() {

    }

    public DamengEvents(List<DamengEvent> list) {
        super.addAll(list);
    }
}
