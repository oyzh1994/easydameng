package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;

import java.util.List;

/**
 * 达梦数据传输触发器列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportTriggerListView extends DBDataTransportObjectListView {

    /**
     * 根据触发器集合初始化列表
     *
     * @param triggers 触发器集合
     */
    public void of(List<DamengTrigger> triggers) {
        List<DBDataTransportObject> list = CollectionUtil.newArrayList();
        for (DamengTrigger trigger : triggers) {
            DBDataTransportObject obj = new DBDataTransportObject();
            obj.setName(trigger.getName());
            list.add(obj);
        }
        this.init(list);
    }
}
