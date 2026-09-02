package cn.oyzh.easydameng.dameng.record;

import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.exception.DBException;
import cn.oyzh.easydameng.listener.DBStatusListener;
import cn.oyzh.easydameng.listener.DBStatusListenerManager;
import cn.oyzh.easydameng.util.DBDataUtil;
import cn.oyzh.easydameng.util.DBNodeUtil;
import cn.oyzh.easydameng.util.DBRecordUtil;
import cn.oyzh.fx.db.DBRecordProperty;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;

/**
 * db表记录属性
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class DamengRecordProperty extends DBRecordProperty {

    /**
     * 表字段
     */
    private DamengColumn column;

    /**
     * 表记录
     */
    private DamengRecord record;

    public DamengRecordProperty(DamengRecord record, DamengColumn column, Object value, boolean readonly) {
        super(value);
        this.column = column;
        this.record = record;
        // this.columns = columns;
        if (!readonly) {
            this.original = value;
        }
        this.readonly = readonly;
    }

    @Override
    public Object get() {
        if (this.readonly || !this.isChanged() || this.node == null) {
            return super.get();
        }
        if (this.setToNullFlag) {
            return null;
        }
        try {
            return DBNodeUtil.getNodeVal(this.node);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public void set(Object newValue) {
        super.set(newValue);
        if (this.node != null) {
            DBNodeUtil.setNodeVal(node, newValue);
        }
    }

    @Override
    public Object getValue() {
        if (this.readonly) {
            return DBRecordUtil.formatValue(super.getValue(), this.column);
        }
        if (this.node == null) {
            this.node = DBRecordUtil.getNode(this, super.getValue(), this.column);
            TableViewUtil.rowOnCtrlS(this.node);
            TableViewUtil.selectRowOnMouseClicked(this.node);
        }
        return this.node;
    }

    @Override
    public void discard() {
        if (this.isChanged() && this.node != null) {
            DBNodeUtil.setNodeVal(this.node, super.get());
        }
        super.setChanged(false);
    }

    @Override
    public void setChanged(boolean changed) {
        this.changedProperty().set(changed);
        DBStatusListener listener = DBStatusListenerManager.getListener(this.column.getSchema() + ":" + this.column.getTableName());
        if (listener != null) {
            listener.changed(null, null, null);
        }
        this.setToNullFlag = false;
    }

    public void updateOriginal() {
        try {
            if (this.node != null) {
                super.set(DBNodeUtil.getNodeVal(this.node));
                this.original = super.get();
            }
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    /**
     * 复制为insert语句
     */
    public void vCopyAsInsertSql() {
        DamengColumns columns = this.record.getColumns();
        String sql = DBDataUtil.toInsertSql(columns, this.record, true);
        ClipboardUtil.copy(sql);
    }

    /**
     * 复制为update语句
     */
    public void vCopyAsUpdateSql() {
        DamengColumns columns = this.record.getColumns();
        String sql = DBDataUtil.toUpdateSql(columns, this.record);
        ClipboardUtil.copy(sql);
    }

    public DamengColumn getColumn() {
        return column;
    }

    public void setColumn(DamengColumn column) {
        this.column = column;
    }

    @Override
    public void destroy() {
        if (this.node != null) {
            this.column = null;
            this.record = null;
            super.destroy();
        }
    }
}
