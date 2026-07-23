package cn.oyzh.easydameng.tabs.query;

import cn.oyzh.easydameng.dameng.query.DamengQueryResults;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db查询信息tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class DamengQueryInfoTab extends RichTab {

    @Override
    protected String url() {
        return DamengTab.BASE_PATH + "query/damengQueryInfoTab.fxml";
    }

    public void init(DamengQueryResults<?> results) {
        this.controller().init(results);
    }

    @Override
    public DamengQueryInfoTabController controller() {
        return (DamengQueryInfoTabController) super.controller();
    }


    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
