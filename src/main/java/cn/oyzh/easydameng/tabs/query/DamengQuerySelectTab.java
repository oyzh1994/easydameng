package cn.oyzh.easydameng.tabs.query;

import cn.oyzh.easydameng.dameng.query.DamengExecuteResult;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
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

    /**
     * 初始化查询结果
     *
     * @param title  标题
     * @param result 执行结果
     * @param dbItem 数据库节点
     */
    public void init(String title, DamengExecuteResult result, DamengSchemaTreeItem dbItem) {
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
