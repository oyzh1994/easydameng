package cn.oyzh.easydameng.trees.event;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db树事件节点
 *
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventTreeItem extends DBTreeItem<DamengEventTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengEvent value;

    public DamengEvent value() {
        return value;
    }

    public DamengEventTreeItem(DamengEvent event, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = event;
        this.setValue(new DamengEventTreeItemValue(this));
        // // 监听展开
        // super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) e -> this.flushLocal());
    }

    @Override
    public DamengEventsTreeItem parent(){
        return (DamengEventsTreeItem) super.parent();
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
        // FXMenuItem open = MenuItemHelper.openEvent( this::onPrimaryDoubleClick);
        // items.add(open);
        FXMenuItem renameEvent = MenuItemHelper.renameEvent( this::rename);
        items.add(renameEvent);
        FXMenuItem design = MenuItemHelper.designEvent( this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem delete = MenuItemHelper.deleteEvent( this::delete);
        items.add(delete);
        FXMenuItem info = MenuItemHelper.eventInfo( this::eventInfo);
        items.add(info);
        return items;
    }

    private void eventInfo() {
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteEvent() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropEvent(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public DamengDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    public String dbName() {
        return parent().dbName();
    }

    public String infoName() {
        return parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.designEvent(this.value, this.dbItem());
    }

    public String eventName() {
        return this.value.getName();
    }

    @Override
    public void rename() {
        try {
            String newName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.eventName());
            // 名称为null或者跟当前名称相同，则忽略
            if (newName == null || Objects.equals(newName, this.eventName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(newName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent());
                return;
            }
            String oldName = this.eventName();
            // 修改名称
            this.dbItem().renameEvent(oldName, newName);
            this.value.setName(newName);
            this.refresh();
            DamengEventUtil.eventRenamed(this, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }
}
