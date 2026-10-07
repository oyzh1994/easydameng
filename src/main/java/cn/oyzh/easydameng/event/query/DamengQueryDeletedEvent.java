package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.trees.query.DamengQueryTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦查询已删除事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengQueryDeletedEvent extends Event<DamengQueryTreeItem> {

    /**
     * 获取查询唯一标识
     *
     * @return 查询唯一标识
     */
    public String queryId() {
        return this.data().value().getUid();
    }
}
