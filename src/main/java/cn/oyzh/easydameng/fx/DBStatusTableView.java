package cn.oyzh.easydameng.fx;

import cn.oyzh.fx.db.DBObject;
import cn.oyzh.fx.db.listener.DBStatusListener;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import javafx.collections.ListChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据库对象状态表格视图
 *
 * @author oyzh
 * @since 2024/07/22
 */
public class DBStatusTableView<S extends DBObject> extends FXTableView<S> {

    /**
     * 待删除项列表
     */
    private List<S> deleteItems;

    /**
     * 重置状态，清空待删除项并清除全部对象状态
     *
     * @throws Exception 异常
     */
    public void reset() throws Exception {
        this.deleteItems = null;
        this.clearStatus();
    }

    /**
     * 清除全部对象的状态
     *
     * @throws Exception 异常
     */
    public void clearStatus() throws Exception {
        for (DBObject object : this.getItems()) {
            object.clearStatus();
        }
    }

    /**
     * 状态监听器
     */
    private DBStatusListener statusListener;

    {
        this.itemList().addListener((ListChangeListener<S>) c -> {
            if (this.statusListener == null) {
                return;
            }
            if (!c.next()) {
                return;
            }
            if (c.wasReplaced()) {
                List<? extends S> list = c.getList();
                if (list != null) {
                    for (S status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
            } else if (c.wasAdded()) {
                List<? extends S> list = c.getAddedSubList();
                if (list != null) {
                    for (DBObject status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
                this.statusListener.changed(null, null, null);
            } else if (c.wasRemoved()) {
                List<? extends S> list = c.getRemoved();
                if (list != null) {
                    for (S status : list) {
                        if (!status.isCreated()) {
                            if (this.deleteItems == null) {
                                this.deleteItems = new CopyOnWriteArrayList<>();
                            }
                            this.deleteItems.add(status);
                        }
                        status.statusProperty().removeListener(this.statusListener);
                    }
                }
            }
        });
    }

    /**
     * 获取待删除项列表
     *
     * @return 待删除项列表
     */
    public List<S> getDeleteItems() {
        return deleteItems;
    }

    /**
     * 设置待删除项列表
     *
     * @param deleteItems 待删除项列表
     */
    public void setDeleteItems(List<S> deleteItems) {
        this.deleteItems = deleteItems;
    }

    /**
     * 获取状态监听器
     *
     * @return 状态监听器
     */
    public DBStatusListener getStatusListener() {
        return statusListener;
    }

    /**
     * 设置状态监听器
     *
     * @param statusListener 状态监听器
     */
    public void setStatusListener(DBStatusListener statusListener) {
        this.statusListener = statusListener;
    }
}
