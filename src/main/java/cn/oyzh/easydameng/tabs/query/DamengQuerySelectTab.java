package cn.oyzh.easydameng.tabs.query;

import cn.oyzh.easydameng.dameng.query.DamengExecuteResult;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class DamengQuerySelectTab extends RichTab {

    @Override
    protected String url() {
        return DamengTab.BASE_PATH + "query/damengQuerySelectTab.fxml";
    }

    public void init(String title, DamengExecuteResult result, DamengDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public DamengQuerySelectTabController controller() {
        return (DamengQuerySelectTabController) super.controller();
    }


    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
