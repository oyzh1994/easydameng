package cn.oyzh.easydameng.dameng.record;

import cn.oyzh.common.object.Destroyable;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.exception.DBException;
import cn.oyzh.easydameng.listener.DBStatusListener;
import cn.oyzh.easydameng.listener.DBStatusListenerManager;
import cn.oyzh.easydameng.util.DBDataUtil;
import cn.oyzh.easydameng.util.DBNodeUtil;
import cn.oyzh.easydameng.util.DBRecordUtil;
import cn.oyzh.fx.gui.text.field.BinaryTextFiled;
import cn.oyzh.fx.plus.node.NodeDestroyUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.util.concurrent.atomic.LongAdder;

/**
 * db表记录属性
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class DamengRecordProperty extends SimpleObjectProperty<Object> implements Destroyable {

    /**
     * 是否变更
     */
    private SimpleBooleanProperty changedProperty;

    /**
     * 表字段
     */
    private DamengColumn column;

    // /**
    //  * 表字段列表
    //  */
    // private DamengColumns columns;

    /**
     * 表记录
     */
    private DamengRecord record;

    /**
     * 原始数据
     */
    private Object original;

    /**
     * 设置为null标志位
     */
    private boolean setToNullFlag;

    /**
     * 只读模式
     */
    private final boolean readonly;

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

    /**
     * 节点
     */
    private Node node;

//    /**
//     * 内存泄露计数器
//     */
//    private final static LongAdder ADDER = new LongAdder();

    @Override
    public Object getValue() {
        if (this.readonly) {
//        if (this.readonly || !this.record.isEditable()) {
            return DBRecordUtil.formatValue(super.getValue(), this.column);
        }
        if (this.node == null) {
            this.node = DBRecordUtil.getNode(this, super.getValue(), this.column);
            TableViewUtil.rowOnCtrlS(this.node);
            TableViewUtil.selectRowOnMouseClicked(this.node);
//            if (!JarUtil.isInJar()) {
//                ADDER.increment();
//                System.out.println("adder:" + ADDER.longValue());
//            }
        }
        return this.node;
    }

    /**
     * 抛弃
     */
    public void discard() {
        if (this.isChanged() && this.node != null) {
            DBNodeUtil.setNodeVal(this.node, super.get());
        }
        this.setChanged(false);
    }

    public SimpleBooleanProperty changedProperty() {
        if (this.changedProperty == null) {
            this.changedProperty = new SimpleBooleanProperty();
        }
        return this.changedProperty;
    }

    public boolean isChanged() {
        return this.changedProperty != null && this.changedProperty.get();
    }

    public void setChanged(boolean changed) {
        this.changedProperty().set(changed);
        DBStatusListener listener;
        if (this.column.getSchema() != null) {
            listener = DBStatusListenerManager.getListener(this.column.getDbName() + ":" + this.column.getSchema() + ":" + this.column.getTableName());
        } else {
            listener = DBStatusListenerManager.getListener(this.column.getDbName() + ":" + this.column.getTableName());
        }
        if (listener != null) {
            listener.changed(null, null, null);
        }
        // // 重新格式化值
        // if (!changed && this.node instanceof BinaryTextFiled filed) {
        //     filed.setText(BinaryTextFiled.format(filed.getValue(), filed.getScale()));
        // }
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

    public Node getControl() {
        return this.node;
    }

    public void vCopy() {
        ClipboardUtil.copy(this.node);
    }

    public void vPaste() {
        ClipboardUtil.paste(this.node);
    }

    // public void vDelete() {
    //     DamengEventUtil.recordDelete(this.record);
    // }
    //
    // /**
    //  * 转换为字段列表
    //  *
    //  * @return 字段列表
    //  */
    // private DamengColumns toColumns() {
    //     Set<String> cols = this.record.columns();
    //     DamengColumns columns = new DamengColumns();
    //     int pos = 0;
    //     for (String col : cols) {
    //         DamengColumn column = new DamengColumn(col);
    //         column.setPosition(pos++);
    //         column.setTableName(this.column.getTableName());
    //         columns.add(column);
    //     }
    //     return columns;
    // }

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

    public void vSetToNull() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StringUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.clear();
            }
            textField.setPromptText(DBRecordUtil.nullPromptText());
            NodeUtil.unFocus(this.node);
        }
        this.setToNullFlag = true;
    }

    public void vSetToEmptyString() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StringUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.setText("");
            }
            textField.setPromptText("");
            NodeUtil.unFocus(this.node);
        }
    }

    public DamengColumn getColumn() {
        return column;
    }

    public void setColumn(DamengColumn column) {
        this.column = column;
    }

    public Object getOriginal() {
        return original;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void destroy() {
        if (this.node != null) {
            NodeDestroyUtil.destroyObject(this.node);
            this.node = null;
            this.column.destroy();
            this.column = null;
            this.record = null;
            this.original = null;
            this.changedProperty.unbind();
            this.changedProperty = null;
//            if (!JarUtil.isInJar()) {
//                ADDER.decrement();
//                System.out.println("adder:" + ADDER.longValue());
//            }
        }
    }
}
