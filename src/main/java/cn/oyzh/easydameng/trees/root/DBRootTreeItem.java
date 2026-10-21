package cn.oyzh.easydameng.trees.root;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.controller.connect.DamengConnectAddController;
import cn.oyzh.easydameng.data.db.DBConnectManager;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengGroup;
import cn.oyzh.easydameng.dto.DamengInfoExport;
import cn.oyzh.easydameng.store.DamengConnectStore;
import cn.oyzh.easydameng.store.DamengGroupStore;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.DBTreeView;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.group.DBGroupTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileChooserHelper;
import cn.oyzh.fx.plus.chooser.FileExtensionFilter;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DB树根节点
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class DBRootTreeItem extends DBTreeItem<DBRootTreeItemValue> implements DBConnectManager {

    /**
     * DB信息储存
     */
    private final DamengConnectStore connectStore = DamengConnectStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final DamengGroupStore groupStore = DamengGroupStore.INSTANCE;

    /**
     * 构造数据库树根节点
     *
     * @param treeView 所属树视图
     */
    public DBRootTreeItem( DBTreeView treeView) {
        super(treeView);
        this.setValue(new DBRootTreeItemValue());
        // 初始化子节点
        this.initChildes();
//        // 监听变化
//        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
//            DBEventUtil.treeChildChanged();
//            this.flushLocal();
//        });
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        // 初始化分组
        List<DamengGroup> groups = this.groupStore.load();
        if (CollectionUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>();
            for (DamengGroup group : groups) {
                list.add(new DBGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<DamengConnect> infos = this.connectStore.load();
        if (CollectionUtil.isNotEmpty(infos)) {
            this.addConnects(infos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect( this::addConnect);
        FXMenuItem exportConnect = MenuItemHelper.exportConnect( this::exportConnect);
        FXMenuItem importConnect = MenuItemHelper.importConnect( this::importConnect);
        FXMenuItem addGroup = MenuItemHelper.addFolder1( this::addGroup);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(exportConnect);
        items.add(importConnect);
        items.add(addGroup);
        return items;
    }

    /**
     * 导出连接
     */
    public void exportConnect() {
        List<DamengConnect> infos = this.connectStore.load();
        if (infos.isEmpty()) {
            MessageBox.warn(I18nHelper.connectionIsEmpty(), this.getWindow());
            return;
        }
        DamengInfoExport export = DamengInfoExport.fromConnects(infos);
        FileExtensionFilter extensionFilter = FXChooser.jsonExtensionFilter();
        File file = FileChooserHelper.save(I18nHelper.saveConnection(), I18nResourceBundle.i18nString("base.database", "base.connect", "base._json"), extensionFilter);
        if (file != null) {
            try {
                FileUtil.writeUtf8String(export.toJSONString(), file);
                MessageBox.okToast(I18nHelper.operationSuccess(), this.getWindow());
            } catch (Exception ex) {
                MessageBox.warn(I18nHelper.operationFail(), this.getWindow());
            }
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile(), this.getWindow());
            return;
        }
        File file = CollectionUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    public void importConnect() {
        FileExtensionFilter filter1 = FXChooser.jsonExtensionFilter();
        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 解析连接文件
     *
     * @param file 文件
     */
    private void parseConnect(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn(I18nHelper.fileNotExists(), this.getWindow());
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nHelper.notSupportFolder(), this.getWindow());
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn(I18nHelper.invalidFormat(), this.getWindow());
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty(), this.getWindow());
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            DamengInfoExport export = DamengInfoExport.fromJSON(text);
            List<DamengConnect> infos = export.getConnects();
            if (CollectionUtil.isNotEmpty(infos)) {
                for (DamengConnect info : infos) {
                    if (this.connectStore.insert(info)) {
                        this.addConnect(info);
                    } else {
                        MessageBox.warn(I18nHelper.connect() + "[" + info.getName() + "]" + I18nHelper.importFail(), this.getWindow());
                    }
                }
                MessageBox.okToast(I18nHelper.operationSuccess(), this.getWindow());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException(), this.getWindow());
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageManager.showStage(DamengConnectAddController.class, this.getWindow());
    }

    /**
     * 添加分组
     */
    public void addGroup() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.getWindow());

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StringUtil.isBlank(groupName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty(), this.getWindow());
            return;
        }

        DamengGroup group = new DamengGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists(), this.getWindow());
            return;
        }
        if (this.groupStore.insert(group)) {
            this.addChild(new DBGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn(I18nHelper.operationFail(), this.getWindow());
        }
    }

    /**
     * 获取分组树节点组件
     *
     * @param groupId 分组id
     */
    private DBGroupTreeItem getGroupItem(String groupId) {
        if (StringUtil.isNotBlank(groupId)) {
            List<DBGroupTreeItem> items = this.getGroupItems();
            Optional<DBGroupTreeItem> groupTreeItem = items.parallelStream().filter(g -> Objects.equals(g.value().getGid(), groupId)).findAny();
            return groupTreeItem.orElse(null);
        }
        return null;
    }

    /**
     * 获取分组树节点组件
     *
     * @return 分组树节点组件
     */
    private List<DBGroupTreeItem> getGroupItems() {
        List<DBGroupTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof DBGroupTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    /**
     * 连接新增事件
     *
     * @param info 连接
     */
    public void infoAdd(DamengConnect info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接
     */
    public void infoUpdate(DamengConnect info) {
        f1:
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                for (DBConnectTreeItem connectTreeItem : groupTreeItem.getConnectedItems()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect( DamengConnect info) {
        DBGroupTreeItem groupItem = this.getGroupItem(info.getGroupId());
        if (groupItem == null) {
            super.addChild(new DBConnectTreeItem(info, this.getTreeView()));
        } else {
            groupItem.addConnect(info);
        }
    }

    @Override
    public void addConnectItem( DBConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.connectStore.update(item.value());
            }
            super.addChild(item);
            this.expend();
        }
    }

    @Override
    public void addConnectItems( List<DBConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.expend();
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
        for (TreeItem<?> child : this.richChildren()) {
            if (child instanceof DBConnectTreeItem connectTreeItem) {
                items.add(connectTreeItem);
            } else if (child instanceof DBGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectItems());
            }
        }
        return items;
    }

    @Override
    public List<DBConnectTreeItem> getConnectedItems() {
        List<DBConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (Object item : this.richChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.isConnected()) {
                    items.add(connectTreeItem);
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectedItems());
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
        return item instanceof DBConnectTreeItem;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}
