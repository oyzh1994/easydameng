package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportFunctionListView extends DBDataTransportObjectListView {

    public void of(List<DamengFunction> functions) {
        List<DBDataTransportObject> list = CollectionUtil.newArrayList();
        for (DamengFunction function : functions) {
            DBDataTransportObject obj = new DBDataTransportObject();
            obj.setName(function.getName());
            list.add(obj);
        }
        this.init(list);
    }
}
