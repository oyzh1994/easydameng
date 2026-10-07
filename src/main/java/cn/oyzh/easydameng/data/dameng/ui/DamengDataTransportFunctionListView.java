package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;

import java.util.List;

/**
 * 达梦数据传输函数列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportFunctionListView extends DBDataTransportObjectListView {

    /**
     * 根据函数集合初始化列表
     *
     * @param functions 函数集合
     */
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
