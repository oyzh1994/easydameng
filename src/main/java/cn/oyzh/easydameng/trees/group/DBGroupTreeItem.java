package cn.oyzh.easydameng.trees.group;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.controller.connect.DamengConnectAddController;
import cn.oyzh.easydameng.data.db.DBConnectManager;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengGroup;
import cn.oyzh.easydameng.store.DamengConnectStore;
import cn.oyzh.easydameng.store.DamengGroupStore;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.DBTreeView;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.root.DBRootTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DB分组树节点
 *
 * @author oyzh
 * @since 2023/05/12
 */
public class DBGroupTreeItem extends DBTreeItem<DBGroupTreeItemValue> implements DBConnectManager {

    /**
     * 分组对象
     */
    private final DamengGroup value;

    /**
     * DB信息储存
     */
    private final DamengConnectStore connectStore = DamengConnectStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final DamengGroupStore groupStore = DamengGroupStore.INSTANCE;

    /**
     * 构造分组树节点
     *
     * @param group    分组对象
     * @param treeView 所属树视图
     */
    public DBGroupTreeItem( DamengGroup group,  DBTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new DBGroupTreeItemValue(this));
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(false);
            this.groupStore.update(this.value);
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(true);
            this.groupStore.update(this.value);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect( this::addConnect);
        FXMenuItem renameGroup = MenuItemHelper.renameGroup( this::rename);
        FXMenuItem delGroup = MenuItemHelper.deleteGroup( this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(groupName)) {
            return;
        }
        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(groupName);
        if (this.groupStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        // 修改名称
        if (this.groupStore.update(this.value)) {
            this.refresh();
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip1())) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip2())) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value)) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<DBConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父节点
            this.parent().addConnectItems(childes);
        }
        // 移除节点
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageAdapter fxView = StageManager.parseStage(DamengConnectAddController.class, this.window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父节点
     *
     * @return 根节点
     */
    public DBRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (DBRootTreeItem) treeItem;
    }

    @Override
    public void addConnect( DamengConnect DBInfo) {
        this.addConnectItem(new DBConnectTreeItem(DBInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem( DBConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                this.connectStore.update(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems( List<DBConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem( DBConnectTreeItem item) {
        // 删除连接
        if (this.connectStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<DBConnectTreeItem> getConnectItems() {
        List<DBConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof DBConnectTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            return !Objects.equals(connectTreeItem.value().getGroupId(), this.value.getGid());
        }
        return false;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }

    /**
     * 获取分组对象
     *
     * @return 分组对象
     */
    public DamengGroup value() {
        return value;
    }
}
