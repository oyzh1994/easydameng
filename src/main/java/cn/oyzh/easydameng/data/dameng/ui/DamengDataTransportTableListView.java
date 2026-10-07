package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;

import java.util.List;

/**
 * 达梦数据传输表列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportTableListView extends DBDataTransportObjectListView {

    /**
     * 根据表集合初始化列表
     *
     * @param tables 表集合
     */
    public void of(List<DamengTable> tables) {
        List<DBDataTransportObject> list = CollectionUtil.newArrayList();
        for (DamengTable table : tables) {
            DBDataTransportObject obj = new DBDataTransportObject();
            obj.setName(table.getName());
            list.add(obj);
        }
        this.init(list);
    }

}
