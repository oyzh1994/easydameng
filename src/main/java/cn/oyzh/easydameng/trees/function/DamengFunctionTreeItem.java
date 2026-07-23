package cn.oyzh.easydameng.trees.function;

import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * db树函数节点
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengFunctionTreeItem extends DBTreeItem<DamengFunctionTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengFunction value;

    public DamengFunctionTreeItem(DamengFunction function, RichTreeView treeView) {
        super(treeView);
        this.value = function;
        super.setFilterable(true);
        this.setValue(new DamengFunctionTreeItemValue(this));
    }

    @Override
    public DamengFunctionsTreeItem parent() {
        return (DamengFunctionsTreeItem) super.parent();
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public DamengClient client() {
        return this.parent().client();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public DamengConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        // FXMenuItem open = MenuItemHelper.openFunction( this::onPrimaryDoubleClick);
        // items.add(open);
        FXMenuItem design = MenuItemHelper.designFunction( this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem delete = MenuItemHelper.deleteFunction( this::delete);
        items.add(delete);
        // FXMenuItem info = MenuItemHelper.functionInfo( this::functionInfo);
        // items.add(info);
        return items;
    }

    // private void functionInfo() {
    // }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteFunction() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropFunction(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public DamengDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    public String dbName() {
        return this.parent().dbName();
    }

    public String infoName() {
        return this.parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.designFunction(this.value, this.dbItem());
    }

    public String functionName() {
        return this.value.getName();
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    @Override
    public void loadChild() {
        try {
            this.setLoaded(true);
            this.setLoading(true);
            DamengFunction function = this.client().selectFunction(this.dbName(), this.functionName());
            if (function != null) {
                this.value.copy(function);
            }
        } catch (Exception ex) {
            this.setLoaded(false);
            ex.printStackTrace();
            MessageBox.exception(ex);
        } finally {
            this.setLoading(false);
        }
    }

    @Override
    public void onPrimarySingleClick() {
        if (!this.isLoaded()) {
            super.onPrimarySingleClick();
        } else {
            super.onPrimarySingleClick();
        }
    }

    public DamengFunction value() {
        return value;
    }
}
