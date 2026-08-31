package cn.oyzh.easydameng.data.dameng.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.data.db.dto.DBDataTransportObject;
import cn.oyzh.easydameng.data.db.ui.DBDataTransportObjectListView;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/05
 */
public class DamengDataTransportViewListView extends DBDataTransportObjectListView {

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
