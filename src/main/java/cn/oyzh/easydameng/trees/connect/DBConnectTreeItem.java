package cn.oyzh.easydameng.trees.connect;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.controller.connect.DamengConnectUpdateController;
import cn.oyzh.easydameng.controller.database.DamengDatabaseAddController;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.db.DBClientUtil;
import cn.oyzh.easydameng.db.DBConnectManager;
import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.store.DamengConnectStore;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.DBTreeView;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * db树连接节点
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DBConnectTreeItem extends DBTreeItem<DBConnectTreeItemValue> {

    /**
     * db信息
     */
    private DamengConnect value;

    /**
     * db客户端
     */
    private DamengClient client;

    /**
     * 已取消操作标志位
     */
    private boolean canceled;

    /**
     * redis信息储存
     */
    private final DamengConnectStore connectStore = DamengConnectStore.INSTANCE;

    public DBConnectTreeItem(DamengConnect value, DBTreeView treeView) {
        super(treeView);
        this.value(value);
//        // 监听键变化
//        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
//            DBEventUtil.treeChildChanged();
//            this.flushLocal();
//        });
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.loadChild();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            FXMenuItem cancelConnect = MenuItemHelper.cancelConnect( this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            FXMenuItem closeConnect = MenuItemHelper.closeConnect( this::closeConnect);
            FXMenuItem editConnect = MenuItemHelper.editConnect( this::editConnect);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect( this::repeatConnect);
            FXMenuItem addDatabase = MenuItemHelper.addDatabase( this::addDatabase);
            FXMenuItem reload = MenuItemHelper.reloadDatabase( this::reloadChild);

            items.add(closeConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(addDatabase);
            items.add(reload);
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect( this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect( this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect( this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect( this::delete);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect( this::repeatConnect);
            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(deleteConnect);
        }
        return items;
    }

    /**
     * 新增数据库
     */
    @FXML
    private void addDatabase() {
        StageAdapter fxView = StageManager.parseStage(DamengDatabaseAddController.class, this.window());
        fxView.setProp("connectItem", this);
        fxView.showAndWait();
        String databaseName = fxView.getProp("databaseName");
        if (StringUtil.isNotBlank(databaseName)) {
            this.addDatabase(databaseName);
        }
    }

    public void addDatabase(String databaseName) {
        DBDatabase database = this.client.database(databaseName);
        super.addChild(new DamengDatabaseTreeItem(database, this.getTreeView()));
    }

    /**
     * 取消连接
     */
    public void cancelConnect() {
        this.canceled = true;
        ThreadUtil.start(() -> {
            this.client.close();
            this.stopWaiting();
        });
    }

    /**
     * 连接
     */
    public void connect() {
        if (!this.isConnected() && !this.isConnecting()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.client.start();
                        if (!this.isConnected()) {
                            if (!this.canceled) {
                                MessageBox.warn("[" + this.value.getName() + "] " + I18nHelper.connectFail());
                            }
                            this.canceled = false;
                            this.closeConnect(false);
                        } else {
                            this.loadChild();
                        }
                    })
                    .onFinish(this::refresh)
                    .onSuccess(this::expend)
                    .onError(MessageBox::exception)
                    .build();
            // 执行连接
            this.startWaiting(task);
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
            this.closeConnect(true);
        }
    }

    /**
     * 关闭连接
     *
     * @param waiting 是否开启等待动画
     */
    public void closeConnect(boolean waiting) {
        Runnable func = () -> {
            this.client.close();
            this.clearChild();
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func::run)
                    .onSuccess(this::refresh)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm(I18nHelper.closeAndContinue())) {
                return;
            }
            this.closeConnect();
        }
        StageAdapter fxView = StageManager.parseStage(DamengConnectUpdateController.class, this.window());
        fxView.setProp("info", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        DamengConnect dbInfo = new DamengConnect();
        dbInfo.copy(this.value);
        dbInfo.setName(this.value.getName() + "-" + I18nHelper.repeat());
        dbInfo.setCollects(Collections.emptyList());
        if (this.connectStore.insert(dbInfo)) {
            this.connectManager().addConnect(dbInfo);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.connectManager().delConnectItem(this)) {
                DamengEventUtil.connectDeleted(this.value);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(connectName)) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        this.value.setName(connectName);
        // 修改名称
        if (this.connectStore.update(this.value)) {
            this.setValue(new DBConnectTreeItemValue(this));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 设置值
     *
     * @param value redis信息
     */
    public void value(DamengConnect value) {
        this.value = value;
        this.client = DBClientUtil.newClient(value);
        this.setValue(new DBConnectTreeItemValue(this));
    }

    public DamengConnect value() {
        return value;
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 获取当前父节点
     *
     * @return 父节点
     */
    public DBConnectManager connectManager() {
        Object object = this.getParent();
        if (object instanceof DBConnectManager connectManager) {
            return connectManager;
        }
        return null;
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void loadChild() {
        List<DBDatabase> databases = this.client.databases();
        List<TreeItem<?>> list = new ArrayList<>();
        for (DBDatabase database : databases) {
            list.add(new DamengDatabaseTreeItem(database, this.getTreeView()));
        }
        this.setChild(list);
        this.expend();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isConnected()) {
            this.connect();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public boolean existDatabase(String dbName) {
        return this.client.existDatabase(dbName);
    }

    public void createDatabase(DBDatabase database) {
        this.client.createDatabase(database);
    }

    public boolean alterDatabase(DBDatabase database) {
        return this.client.alterDatabase(database);
    }

    public String databaseCollation(String dbName) {
        return this.client.databaseCollation(dbName);
    }

    public boolean dropDatabase(String dbName) {
        return this.client.dropDatabase(dbName);
    }

    public String type() {
        return this.value.getType();
    }


    public DamengClient getClient() {
        return client;
    }
}
