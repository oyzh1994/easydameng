package cn.oyzh.easydameng.tabs.query;

import cn.oyzh.easydameng.dameng.query.DamengExplainResult;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db解释tab
 *
 * @author oyzh
 * @since 2024/08/16
 */
public class DamengQueryExplainTab extends RichTab {

    @Override
    protected String url() {
        return DamengTab.BASE_PATH + "query/damengQueryExplainTab.fxml";
    }

    public void init(String title, DamengExplainResult result) {
        this.setTitle(title);
        this.controller().init(result);
    }

    @Override
    public DamengQueryExplainTabController controller() {
        return (DamengQueryExplainTabController) super.controller();
    }

    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
