package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;
import cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView;

import java.util.List;

/**
 * 达梦数据传输视图列表
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportViewListView extends DBDataTransportObjectListView {

    /**
     * 根据视图集合初始化列表
     *
     * @param views 视图集合
     */
    public void of(List<DamengView> views) {
        List<DBDataTransportObject> list = CollectionUtil.newArrayList();
        for (DamengView view : views) {
            DBDataTransportObject obj = new DBDataTransportObject();
            obj.setName(view.getName());
            list.add(obj);
        }
        this.init(list);
    }
}
