package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;

import java.util.List;

/**
 * 达梦数据传输过程列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportProcedureListView extends DBDataTransportObjectListView {

    /**
     * 根据过程集合初始化列表
     *
     * @param procedures 过程集合
     */
    public void of(List<DamengProcedure> procedures) {
        List<DBDataTransportObject> list = CollectionUtil.newArrayList();
        for (DamengProcedure procedure : procedures) {
            DBDataTransportObject obj = new DBDataTransportObject();
            obj.setName(procedure.getName());
            list.add(obj);
        }
        this.init(list);
    }
}
