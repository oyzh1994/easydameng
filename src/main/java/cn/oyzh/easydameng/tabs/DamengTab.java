package cn.oyzh.easydameng.tabs;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class DamengTab extends RichTab {

    public static final String BASE_PATH = "/tabs/";

    protected String getBasePath() {
        return BASE_PATH;
    }

    public abstract DamengDatabaseTreeItem dbItem() ;
}
