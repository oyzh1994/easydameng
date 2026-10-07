package cn.oyzh.easydameng.tabs.query;

import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.fx.db.query.DBQueryResults;
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

    /**
     * 初始化查询结果信息
     *
     * @param results 查询结果集
     */
    public void init(DBQueryResults<?> results) {
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
