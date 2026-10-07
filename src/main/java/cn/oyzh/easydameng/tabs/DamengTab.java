package cn.oyzh.easydameng.tabs;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * 达梦数据库标签页基类，定义标签页基础路径与数据库节点访问入口
 *
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class DamengTab extends RichTab {

    /**
     * 标签页资源基础路径
     */
    public static final String BASE_PATH = "/tabs/";

    /**
     * 获取标签页资源基础路径
     *
     * @return 标签页资源基础路径
     */
    protected String getBasePath() {
        return BASE_PATH;
    }

    /**
     * 获取数据库节点
     *
     * @return 数据库节点
     */
    public abstract DamengSchemaTreeItem dbItem() ;
}
