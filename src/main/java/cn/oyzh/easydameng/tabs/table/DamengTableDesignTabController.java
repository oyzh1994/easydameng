package cn.oyzh.easydameng.tabs.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.fx.DBCharsetComboBox;
import cn.oyzh.easydameng.fx.DBCollationComboBox;
import cn.oyzh.easydameng.fx.DBEditor;
import cn.oyzh.easydameng.fx.DBStatusTableView;
import cn.oyzh.easydameng.fx.table.DamengEngineComboBox;
import cn.oyzh.easydameng.fx.table.DamengRowFormatComboBox;
import cn.oyzh.easydameng.generator.table.DamengTableAlertSqlGenerator;
import cn.oyzh.easydameng.generator.table.DamengTableCreateSqlGenerator;
import cn.oyzh.easydameng.listener.DBStatusListener;
import cn.oyzh.easydameng.listener.DBStatusListenerManager;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.check.DamengCheckControl;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumnControl;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeyControl;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.index.DamengIndexControl;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.table.DamengAlertTableParam;
import cn.oyzh.easydameng.dameng.table.DamengCreateTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggerControl;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.ParentTabController;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * db表设计业务
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class DamengTableDesignTabController extends ParentTabController {

    // /**
    //  * 新增按钮
    //  */
    // @FXML
    // private SVGGlyph add;
    //
    // /**
    //  * 删除按钮
    //  */
    // @FXML
    // private SVGGlyph delete;
    //
    // /**
    //  * 上移按钮
    //  */
    // @FXML
    // private SVGGlyph moveUp;
    //
    // /**
    //  * 下移按钮
    //  */
    // @FXML
    // private SVGGlyph moveDown;

    /**
     * 切换面板
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * 引擎
     */
    @FXML
    private DamengEngineComboBox tableEngine;

    /**
     * 字符集
     */
    @FXML
    private DBCharsetComboBox tableCharset;

    /**
     * 排序方式
     */
    @FXML
    private DBCollationComboBox tableCollation;

    /**
     * 行格式组件
     */
    @FXML
    private FXHBox tableRowFormatBox;

    /**
     * 行格式
     */
    @FXML
    private DamengRowFormatComboBox tableRowFormat;

    /**
     * 自动递增组件
     */
    @FXML
    private FXHBox tableAutoIncrementBox;

    /**
     * 自动递增
     */
    @FXML
    private NumberTextField tableAutoIncrement;

    /**
     * 注释
     */
    @FXML
    private FXTextArea tableComment;

    /**
     * sql预览
     */
    @FXML
    private DBEditor sqlPreview;

    /**
     * 表字段组件
     */
    @FXML
    private DBStatusTableView<DamengColumnControl> columnTable;

    // /**
    //  * 字段状态列
    //  */
    // @FXML
    // private DBStatusColumn<DamengColumnControl> colStatus;
    //
    // /**
    //  * 字段名称列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, String> colName;
    //
    // /**
    //  * 字段类型列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, String> colType;
    //
    // /**
    //  * 字段长度
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, Integer> colSize;
    //
    // /**
    //  * 字段小数点列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, Integer> colDigits;
    //
    // /**
    //  * 字段是否主键列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, Boolean> colPrimaryKey;
    //
    // /**
    //  * 字段可为null列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, Boolean> colNullable;
    //
    // /**
    //  * 字段注释列
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, String> colComment;

    // /**
    //  * 字段配置
    //  */
    // @FXML
    // private FXTableColumn<DamengColumnControl, String> colConfig;

    /**
     * 表索引组件
     */
    @FXML
    private DBStatusTableView<DamengIndexControl> indexTable;

    // /**
    //  * 索引状态列
    //  */
    // @FXML
    // private DBStatusColumn<DamengIndexControl> indexStatus;
    //
    // /**
    //  * 索引名称列
    //  */
    // @FXML
    // private TableColumn<DamengIndexControl, String> indexName;
    //
    // /**
    //  * 索引字段列
    //  */
    // @FXML
    // private TableColumn<DamengIndexControl, String> indexColumn;
    //
    // /**
    //  * 索引类型列
    //  */
    // @FXML
    // private TableColumn<DamengIndexControl, String> indexType;
    //
    // /**
    //  * 索引方法列
    //  */
    // @FXML
    // private TableColumn<DamengIndexControl, String> indexMethod;
    //
    // /**
    //  * 索引注释列
    //  */
    // @FXML
    // private FXTableColumn<DamengIndexControl, String> indexComment;

    /**
     * 表外键组件
     */
    @FXML
    private DBStatusTableView<DamengForeignKeyControl> foreignKeyTable;

    // /**
    //  * 外键状态列
    //  */
    // @FXML
    // private DBStatusColumn<DamengForeignKeyControl> foreignKeyStatus;
    //
    // /**
    //  * 外键名称列
    //  */
    // @FXML
    // private TableColumn<DamengForeignKeyControl, String> foreignKeyName;
    //
    // /**
    //  * 外键字段列
    //  */
    // @FXML
    // private TableColumn<DamengForeignKeyControl, String> foreignKeyColumn;
    //
    // /**
    //  * 外键引用库
    //  */
    // @FXML
    // private TableColumn<DamengForeignKeyControl, String> foreignKeyPKDatabase;
    //
    // /**
    //  * 外键引用表
    //  */
    // @FXML
    // private TableColumn<DamengForeignKeyControl, String> foreignKeyPKTable;
    //
    // /**
    //  * 外键引用字段
    //  */
    // @FXML
    // private FXTableColumn<DamengForeignKeyControl, String> foreignKeyPKColumn;
    //
    // /**
    //  * 外键删除策略
    //  */
    // @FXML
    // private FXTableColumn<DamengForeignKeyControl, String> foreignKeyDeletePolicy;
    //
    // /**
    //  * 外键更新策略
    //  */
    // @FXML
    // private FXTableColumn<DamengForeignKeyControl, String> foreignKeyUpdatePolicy;

    /**
     * db表
     */
    private DamengTable damengTable;

    /**
     * 触发器组件
     */
    @FXML
    private DBStatusTableView<DamengTriggerControl> triggerTable;

    // /**
    //  * 删除的触发器
    //  */
    // private final List<DamengTrigger> deletedTriggers = new ArrayList<>();

    // /**
    //  * 触发器状态
    //  */
    // @FXML
    // private DBStatusColumn<DamengTriggerControl> triggerStatus;
    //
    // /**
    //  * 触发器名称
    //  */
    // @FXML
    // private FXTableColumn<DamengTriggerControl, String> triggerName;
    //
    // /**
    //  * 触发器策略
    //  */
    // @FXML
    // private FXTableColumn<DamengTriggerControl, String> triggerPolicy;
    //
    // /**
    //  * 触发器定义
    //  */
    // @FXML
    // private FXTableColumn<DamengTriggerControl, String> triggerDefinition;

    /**
     * 检查器组件
     */
    @FXML
    private DBStatusTableView<DamengCheckControl> checkTable;

    // /**
    //  * 检查器状态
    //  */
    // @FXML
    // private DBStatusColumn<DamengCheckControl> checkStatus;
    //
    // /**
    //  * 检查器名称
    //  */
    // @FXML
    // private FXTableColumn<DamengCheckControl, String> checkName;
    //
    // /**
    //  * 检查器子语句
    //  */
    // @FXML
    // private FXTableColumn<DamengCheckControl, String> checkClause;

    /**
     * db库节点
     */
    private DamengDatabaseTreeItem dbItem;

    // /**
    //  * db库节点
    //  */
    // private DamengTableTreeItem tableItem;

    /**
     * 数据监听器
     */
    private DBStatusListener listener;

    /**
     * 未保存标志位
     */
    private boolean unsaved;

    /**
     * 新数据标志位
     */
    private boolean newData;

    /**
     * 初始化中标志位
     */
    private boolean initiating;

    /**
     * 额外信息
     */
    @FXML
    private DamengTableColumnExtraController tableColumnExtraController;

    private DamengCreateTableParam initCreateParam() {
        return (DamengCreateTableParam) this.initParam(true);
    }

    private DamengAlertTableParam initAlertParam() {
        return (DamengAlertTableParam) this.initParam(false);
    }

    private Object initParam(boolean isCreate) {
        DamengTable tempTable = new DamengTable();
        // 数据库
        tempTable.setDbName(this.damengTable.getDbName());

        // 表名称
        if (!this.newData) {
            tempTable.setName(this.damengTable.getName());
        }

        // 注释
        String comment = this.tableComment.getText();
        if (!StringUtil.equals(comment, this.damengTable.getComment())) {
            tempTable.setComment(comment);
        }

        // 引擎
        String engine = this.tableEngine.getSelectedItem();
        if (!StringUtil.equalsIgnoreCase(engine, this.damengTable.getEngine())) {
            tempTable.setEngine(engine);
        }

        // 字符集
        String charset = this.tableCharset.getSelectedItem();
        if (!StringUtil.equalsIgnoreCase(charset, this.damengTable.getCharset())) {
            tempTable.setCharset(charset);
        }

        // 排序
        String collation = this.tableCollation.getSelectedItem();
        if (!StringUtil.equalsIgnoreCase(collation, this.damengTable.getCollation())) {
            tempTable.setCollation(collation);
        }

        // 行格式
        if (this.tableRowFormatBox.isVisible()) {
            String rowFormat = this.tableRowFormat.getValue();
            if (!StringUtil.equalsIgnoreCase(rowFormat, this.damengTable.getRowFormat())) {
                tempTable.setRowFormat(rowFormat);
            }
        }

        // 自动递增
        if (this.tableAutoIncrementBox.isVisible()) {
            Long autoIncrement = this.tableAutoIncrement.getValue();
            if (!Objects.equals(autoIncrement, this.damengTable.getAutoIncrement())) {
                tempTable.setAutoIncrement(autoIncrement);
            }
        }

        // 字段处理
        DamengColumns columns = new DamengColumns();
        for (DamengColumn column : this.columnTable.getItems()) {
            if (!column.isInvalid()) {
                columns.add(column);
            }
        }
        if (CollectionUtil.isNotEmpty(this.columnTable.getDeleteItems())) {
            columns.addAll(this.columnTable.getDeleteItems());
        }

        // 索引处理
        DamengIndexes indexes = new DamengIndexes();
        for (DamengIndex index : this.indexTable.getItems()) {
            if (!index.isInvalid()) {
                indexes.add(index);
            }
        }
        if (CollectionUtil.isNotEmpty(this.indexTable.getDeleteItems())) {
            indexes.addAll(this.indexTable.getDeleteItems());
        }

        // 外键处理
        DamengForeignKeys foreignKeys = new DamengForeignKeys();
        for (DamengForeignKey foreignKey : this.foreignKeyTable.getItems()) {
            if (!foreignKey.isInvalid()) {
                foreignKeys.add(foreignKey);
            }
        }
        if (CollectionUtil.isNotEmpty(this.foreignKeyTable.getDeleteItems())) {
            foreignKeys.addAll(this.foreignKeyTable.getDeleteItems());
        }

        // 触发器处理
        DamengTriggers triggers = new DamengTriggers();
        for (DamengTrigger trigger : this.triggerTable.getItems()) {
            if (!trigger.isInvalid()) {
                triggers.add(trigger);
            }
        }
        if (CollectionUtil.isNotEmpty(this.triggerTable.getDeleteItems())) {
            triggers.addAll(this.triggerTable.getDeleteItems());
        }

        // 检查处理
        DamengChecks checks = null;
        if (this.dbItem.isSupportCheckFeature()) {
            checks = new DamengChecks();
            for (DamengCheck check : this.checkTable.getItems()) {
                if (!check.isInvalid()) {
                    checks.add(check);
                }
            }
            if (CollectionUtil.isNotEmpty(this.checkTable.getDeleteItems())) {
                checks.addAll(this.checkTable.getDeleteItems());
            }
        }
        if (isCreate) {
            return this.dbItem.createTableParam(tempTable, columns, indexes, foreignKeys, triggers, checks);
        }
        return this.dbItem.alterTableParam(tempTable, columns, indexes, foreignKeys, triggers, checks);
    }

    /**
     * 保存db表
     */
    @FXML
    private void save() {
        StageManager.showMask(this::doSave);
    }

    /**
     * 执行保存
     */
    private void doSave() {
        try {
            // 字段检查
            for (DamengColumn column : this.columnTable.getItems()) {
                if (column.isInvalid()) {
                    this.tabPane.selectTab("columnTab");
                    MessageBox.warn(I18nHelper.invalidColumn());
                    return;
                }
            }

            // 索引检查
            for (DamengIndex index : this.indexTable.getItems()) {
                if (index.isInvalid()) {
                    this.tabPane.selectTab("indexTab");
                    MessageBox.warn(I18nHelper.invalidIndex());
                    return;
                }
            }

            // 外键检查
            for (DamengForeignKey foreignKey : this.foreignKeyTable.getItems()) {
                if (foreignKey.isInvalid()) {
                    this.tabPane.selectTab("foreignKeyTab");
                    MessageBox.warn(I18nHelper.invalidForeignKey());
                    return;
                }
            }

            // 触发器检查
            for (DamengTrigger trigger : this.triggerTable.getItems()) {
                if (trigger.isInvalid()) {
                    this.tabPane.selectTab("triggerTab");
                    MessageBox.warn(I18nHelper.invalidTrigger());
                    return;
                }
            }

            // 检查检查
            if (this.dbItem.isSupportCheckFeature()) {
                for (DamengCheck check : this.checkTable.getItems()) {
                    if (check.isInvalid()) {
                        this.tabPane.selectTab("checkTab");
                        MessageBox.warn(I18nHelper.invalidCheck());
                        return;
                    }
                }
            }

            String tableName;
            // 表名称
            if (this.newData) {
                tableName = MessageBox.prompt(I18nHelper.pleaseInputTableName());
                if (tableName == null) {
                    return;
                }
            } else {
                tableName = this.damengTable.getName();
            }

            // this.disableTab();

            DamengTable table = this.damengTable;
            // 创建表
            if (this.newData) {
                DamengCreateTableParam param = this.initCreateParam();
                param.setTableName(tableName);
                this.dbItem.createTable(param);
                table = this.dbItem.selectTable(tableName);
                this.dbItem.getTableTypeChild().addTable(table);
                DamengEventUtil.tableAdded(this.dbItem);
            } else {// 修改表
                DamengAlertTableParam param = this.initAlertParam();
                this.dbItem.alterTable(param);
                DamengEventUtil.tableAlerted(tableName, this.dbItem);
            }
            // this.dbItem.getTableTypeChild().reloadChild();
            // 判断结果
            this.unsaved = false;
            // 初始化信息
            this.initInfo(table);
            // 重置表格
            this.resetTable();
            // 初始化预览
            this.initPreview();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        } finally {
            // this.enableTab();
            this.flushTab();
        }
    }

    /**
     * 初始化变更标志
     */
    private void initChangedFlag() {
        if (!this.initiating) {
            this.unsaved = true;
            this.flushTab();
        }
    }

    /**
     * 重置表单组件
     *
     * @throws Exception 异常
     */
    protected void resetTable() throws Exception {
        this.indexTable.reset();
        this.checkTable.reset();
        this.columnTable.reset();
        this.triggerTable.reset();
        this.foreignKeyTable.reset();
    }

    // protected void resetAllTable() throws Exception {
    //     this.indexTable.reset();
    //     this.checkTable.reset();
    //     this.columnTable.reset();
    //     this.triggerTable.reset();
    //     this.foreignKeyTable.reset();
    // }

    /**
     * 初始化信息
     */
    protected void initInfo(DamengTable table) {
        // 更新初始化标志位
        this.initiating = true;
        this.damengTable = table;
        this.newData = table.isNew();
        // 新数据
        if (table.isNew()) {
            // this.newData = true;
            this.damengTable = new DamengTable();
            this.damengTable.setDbName(this.dbItem.dbName());
            // this.damengTable.setName(I18nHelper.unnamedTable());
            this.initNew();
        } else {// 已有数据
            // this.newData = false;
            this.damengTable = this.dbItem.selectFullTable(table.getName());
            this.initNormal();
        }
        // 标记为结束
        FXUtil.runPulse(() -> this.initiating = false);
    }

    /**
     * 初始化信息
     */
    protected void initNew() {
        NodeGroupUtil.display(this.getTab(), "action2");
        // 重载表数据
        this.tableEngine.select("innoDB");
        // 字符集
        if (this.tableCharset.isItemEmpty()) {
            this.tableCharset.init(this.dbItem.client());
        }
    }

    /**
     * 初始化信息
     */
    protected void initNormal() {
        // this.moveUp.disappear();
        NodeGroupUtil.disappear(this.getTab(), "action2");

        // 基本信息
        this.tableEngine.select(this.damengTable.getEngine());
        this.tableComment.setText(this.damengTable.getComment());
        // 字符集
        if (this.tableCharset.isItemEmpty()) {
            this.tableCharset.init(this.dbItem.client());
        }
        this.tableCharset.select(this.damengTable.getCharset());
        // 排序规则
        this.tableCollation.init(this.damengTable.getCharset(), this.dbItem.client());
        this.tableCollation.select(this.damengTable.getCollation());

        // 检查器
        if (this.dbItem.isSupportCheckFeature()) {
            this.checkTable.setItem(DamengCheckControl.of(this.dbItem.checks(this.tableName())));
        }
        // 索引
        this.indexTable.setItem(DamengIndexControl.of(this.dbItem.indexes(this.tableName())));
        // 字段
        this.columnTable.setItem(DamengColumnControl.of(this.dbItem.columns(this.tableName())));
        // 触发器
        this.triggerTable.setItem(DamengTriggerControl.of(this.dbItem.triggers(this.tableName())));
        // 外键
        this.foreignKeyTable.setItem(DamengForeignKeyControl.of(this.dbItem.foreignKeys(this.tableName())));

        // 行格式
        if (this.damengTable.isInnoDB()) {
            this.tableRowFormatBox.display();
            this.tableRowFormat.select(this.damengTable.getRowFormat());
        }

        // 表自动递增
        if (this.damengTable.hasAutoIncrement()) {
            this.tableAutoIncrementBox.display();
            this.tableAutoIncrement.setValue(this.damengTable.getAutoIncrement());
        }
    }

    /**
     * 新增字段
     */
    private void addColumn() {
        DamengColumnControl column = new DamengColumnControl();
        column.setCreated(true);
        column.setNullable(true);
        this.columnTable.addItem(column);
        this.columnTable.selectLast();
    }

    /**
     * 删除字段
     */
    private void deleteColumn() {
        try {
            DamengColumn column = this.columnTable.getSelectedItem();
            if (column == null) {
                return;
            }
            // 确认操作
            if (!column.isCreated() && !MessageBox.confirm(I18nHelper.deleteField() + " " + column.getName())) {
                return;
            }
            // 从table移除数据
            this.columnTable.removeItem(column);
            column.setDeleted(true);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    // /**
    //  * 上移字段
    //  */
    // private void moveColumnUp() {
    //     try {
    //         TableViewUtil.moveUp(this.columnTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    // /**
    //  * 下移字段
    //  */
    // private void moveColumnDown() {
    //     try {
    //         TableViewUtil.moveDown(this.columnTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    /**
     * 新增索引
     */
    private void addIndex() {
        DamengIndexControl index = new DamengIndexControl();
        index.setCreated(true);
        this.indexTable.addItem(index);
        this.indexTable.selectLast();
    }

    /**
     * 删除索引
     */
    private void deleteIndex() {
        // try {
        //     DamengIndex index = this.indexTable.getSelectedItem();
        //     if (index == null) {
        //         return;
        //     }
        //     // 从table移除数据
        //     if (index.isCreated()) {
        //         this.indexTable.removeItem(index);
        //         return;
        //     }
        //     // 删除数据
        //     if (MessageBox.confirm(I18nHelper.deleteIndex() + " " + index.getName())) {
        //         index.setDeleted(true);
        //     }
        // } catch (Exception ex) {
        //     MessageBox.exception(ex);
        // }
        try {
            DamengIndex index = this.indexTable.getSelectedItem();
            if (index == null) {
                return;
            }
            // 确认操作
            if (!index.isCreated() && !MessageBox.confirm(I18nHelper.deleteIndex() + " " + index.getName())) {
                return;
            }
            // 从table移除数据
            this.indexTable.removeItem(index);
            index.setDeleted(true);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    // /**
    //  * 上移索引
    //  */
    // private void moveIndexUp() {
    //     try {
    //         TableViewUtil.moveUp(this.indexTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    // /**
    //  * 下移索引
    //  */
    // private void moveIndexDown() {
    //     try {
    //         TableViewUtil.moveDown(this.indexTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    /**
     * 新增外键
     */
    private void addForeignKey() {
        DamengForeignKeyControl foreignKey = new DamengForeignKeyControl();
        foreignKey.setCreated(true);
        this.foreignKeyTable.addItem(foreignKey);
        this.foreignKeyTable.selectLast();
    }

    /**
     * 删除外键
     */
    private void deleteForeignKey() {
        // try {
        //     DamengForeignKey foreignKey = this.foreignKeyTable.getSelectedItem();
        //     if (foreignKey == null) {
        //         return;
        //     }
        //     // 从table移除数据
        //     if (foreignKey.isCreated()) {
        //         this.foreignKeyTable.removeItem(foreignKey);
        //         return;
        //     }
        //     // 删除数据
        //     if (MessageBox.confirm(I18nHelper.deleteForeignKey() + " " + foreignKey.getName())) {
        //         foreignKey.setDeleted(true);
        //         this.foreignKeyTable.removeItem(foreignKey);
        //     }
        // } catch (Exception ex) {
        //     MessageBox.exception(ex);
        // }
        try {
            DamengForeignKey foreignKey = this.foreignKeyTable.getSelectedItem();
            if (foreignKey == null) {
                return;
            }
            // 确认操作
            if (!foreignKey.isCreated() && !MessageBox.confirm(I18nHelper.deleteForeignKey() + " " + foreignKey.getName())) {
                return;
            }
            // 从table移除数据
            this.foreignKeyTable.removeItem(foreignKey);
            foreignKey.setDeleted(true);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    // /**
    //  * 上移外键
    //  */
    // private void moveForeignKeyUp() {
    //     try {
    //         TableViewUtil.moveUp(this.foreignKeyTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    // /**
    //  * 下移外键
    //  */
    // private void moveForeignKeyDown() {
    //     try {
    //         TableViewUtil.moveDown(this.foreignKeyTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    /**
     * 新增触发器
     */
    private void addTrigger() {
        DamengTriggerControl trigger = new DamengTriggerControl();
        trigger.setCreated(true);
        this.triggerTable.addItem(trigger);
        this.triggerTable.selectLast();
    }

    /**
     * 删除触发器
     */
    private void deleteTrigger() {
        // try {
        //     DamengTrigger trigger = this.triggerTable.getSelectedItem();
        //     if (trigger == null) {
        //         return;
        //     }
        //     // 从table移除数据
        //     if (trigger.isCreated()) {
        //         this.triggerTable.removeItem(trigger);
        //         return;
        //     }
        //     // 删除数据
        //     if (MessageBox.confirm(I18nHelper.deleteTrigger() + " " + trigger.getName())) {
        //         trigger.setDeleted(true);
        //         this.save();
        //         // this.triggerTable.removeItem(trigger);
        //         // this.deletedTriggers.add(trigger);
        //     }
        // } catch (Exception ex) {
        //     MessageBox.exception(ex);
        // }
        try {
            DamengTrigger trigger = this.triggerTable.getSelectedItem();
            if (trigger == null) {
                return;
            }
            // 确认操作
            if (!trigger.isCreated() && !MessageBox.confirm(I18nHelper.deleteTrigger() + " " + trigger.getName())) {
                return;
            }
            // 从table移除数据
            this.triggerTable.removeItem(trigger);
            trigger.setDeleted(true);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    // /**
    //  * 上移触发器
    //  */
    // private void moveTriggerUp() {
    //     try {
    //         TableViewUtil.moveUp(this.triggerTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    // /**
    //  * 下移触发器
    //  */
    // private void moveTriggerDown() {
    //     try {
    //         TableViewUtil.moveDown(this.triggerTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    /**
     * 新增检查
     */
    private void addCheck() {
        DamengCheckControl check = new DamengCheckControl();
        check.setCreated(true);
        this.checkTable.addItem(check);
        this.checkTable.selectLast();
    }

    /**
     * 删除检查
     */
    private void deleteCheck() {
        // try {
        //     DamengCheck check = this.checkTable.getSelectedItem();
        //     if (check == null) {
        //         return;
        //     }
        //     // 从table移除数据
        //     if (check.isCreated()) {
        //         this.checkTable.removeItem(check);
        //         return;
        //     }
        //     // 删除数据
        //     if (MessageBox.confirm(I18nHelper.deleteCheck() + " " + check.getName())) {
        //         check.setDeleted(true);
        //         this.save();
        //     }
        // } catch (Exception ex) {
        //     MessageBox.exception(ex);
        // }
        try {
            DamengCheck check = this.checkTable.getSelectedItem();
            if (check == null) {
                return;
            }
            // 确认操作
            if (!check.isCreated() && !MessageBox.confirm(I18nHelper.deleteCheck() + " " + check.getName())) {
                return;
            }
            // 从table移除数据
            this.checkTable.removeItem(check);
            check.setDeleted(true);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    // /**
    //  * 上移检查
    //  */
    // private void moveCheckUp() {
    //     try {
    //         TableViewUtil.moveUp(this.checkTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    // /**
    //  * 下移检查
    //  */
    // private void moveCheckDown() {
    //     try {
    //         TableViewUtil.moveDown(this.checkTable);
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // // 字段
        // this.colName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        // this.colType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        // this.colSize.setCellValueFactory(new PropertyValueFactory<>("sizeControl"));
        // this.colDigits.setCellValueFactory(new PropertyValueFactory<>("digitsControl"));
        // this.colComment.setCellValueFactory(new PropertyValueFactory<>("commentControl"));
        // this.colNullable.setCellValueFactory(new PropertyValueFactory<>("nullableControl"));
        // this.colPrimaryKey.setCellValueFactory(new PropertyValueFactory<>("primaryKeyControl"));
        // this.colConfig.setCellValueFactory(new PropertyValueFactory<>("configControl"));

        // // 索引
        // this.indexName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        // this.indexColumn.setCellValueFactory(new PropertyValueFactory<>("columnControl"));
        // this.indexType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        // this.indexMethod.setCellValueFactory(new PropertyValueFactory<>("methodControl"));
        // this.indexComment.setCellValueFactory(new PropertyValueFactory<>("commentControl"));

        // // 外键
        // this.foreignKeyName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        // this.foreignKeyColumn.setCellValueFactory(new PropertyValueFactory<>("columnControl"));
        // this.foreignKeyPKTable.setCellValueFactory(new PropertyValueFactory<>("primaryKeyTableControl"));
        // this.foreignKeyDeletePolicy.setCellValueFactory(new PropertyValueFactory<>("deletePolicyControl"));
        // this.foreignKeyUpdatePolicy.setCellValueFactory(new PropertyValueFactory<>("updatePolicyControl"));
        // this.foreignKeyPKColumn.setCellValueFactory(new PropertyValueFactory<>("primaryKeyColumnControl"));
        // this.foreignKeyPKDatabase.setCellValueFactory(new PropertyValueFactory<>("primaryKeyDatabaseControl"));

        // // 触发器
        // this.triggerName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        // this.triggerPolicy.setCellValueFactory(new PropertyValueFactory<>("policyControl"));
        // this.triggerDefinition.setCellValueFactory(new PropertyValueFactory<>("definitionControl"));

        // // 触发器
        // this.checkName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        // this.checkClause.setCellValueFactory(new PropertyValueFactory<>("clauseControl"));

        // 表单保存事件
        this.indexTable.setCtrlSAction(this::save);
        this.checkTable.setCtrlSAction(this::save);
        this.columnTable.setCtrlSAction(this::save);
        this.triggerTable.setCtrlSAction(this::save);
        this.foreignKeyTable.setCtrlSAction(this::save);

        // 监听事件
        NodeUtil.nodeOnCtrlS(this.getTab(), this::save);
        NodeUtil.nodeOnCtrlS(this.tableComment, this::save);
        NodeUtil.nodeOnCtrlS(this.tableAutoIncrement, this::save);

        // 更新字段列表
        this.columnTable.itemsProperty().get().addListener((ListChangeListener<DamengColumn>) c -> {
            //CacheHelper.set("dameng:columnList", this.columnTable.getItems());
            this.initIndexTable();
            this.initForeignKeyTable();
        });

        // // 监听列表变化
        // this.checkTable.itemList().addListener(this.listChangeListener);
        // this.indexTable.itemList().addListener(this.listChangeListener);
        // this.columnTable.itemList().addListener(this.listChangeListener);
        // this.triggerTable.itemList().addListener(this.listChangeListener);
        // this.foreignKeyTable.itemList().addListener(this.listChangeListener);
    }

//    @Override
//    public void onTabClosed(Event event) {
//        super.onTabClosed(event);
//        CacheHelper.clear();
//    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 字符集选中事件
        this.tableCharset.selectedItemChanged((observable, oldValue, newValue) -> {
            this.tableCollation.init(newValue, this.dbItem.client());
            this.tableCollation.selectFirst();
        });
        // 引擎选中事件
        this.tableEngine.selectedItemChanged((observable, oldValue, newValue) -> {
            if (this.tableEngine.isInnoDB()) {
                this.tableRowFormatBox.display();
                if (StringUtil.isBlank(this.damengTable.getRowFormat())) {
                    this.tableRowFormat.select(this.damengTable.getRowFormat());
                } else {
                    this.tableRowFormat.select(3);
                }
            } else {
                this.tableRowFormatBox.disappear();
            }
        });
        // 表格下标监听
        this.tabPane.selectedItemChanged((observable, oldValue, newValue) -> {
            String tabId = newValue == null ? null : newValue.getId();
            if (StringUtil.equalsAny(tabId, "columnTab", "indexTab", "foreignKeyTab", "triggerTab", "checkTab")) {
                // this.add.display();
                NodeGroupUtil.display(this.getTab(), "action1");
                if (this.newData) {
                    // this.moveUp.display();
                    NodeGroupUtil.display(this.getTab(), "action2");
                }
            } else {
                // this.add.disappear();
                NodeGroupUtil.disappear(this.getTab(), "action1");
                if (this.newData) {
                    // this.moveUp.disappear();
                    NodeGroupUtil.disappear(this.getTab(), "action2");
                }
            }
            // 预览
            if (StringUtil.equals(tabId, "previewTab")) {
                this.initPreview();
            }
        });
        // 初始化监听器
        this.listener = new DBStatusListener() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                initChangedFlag();
            }
        };

        // 监听列表变化
        this.checkTable.setStatusListener(this.listener);
        this.indexTable.setStatusListener(this.listener);
        this.columnTable.setStatusListener(this.listener);
        this.triggerTable.setStatusListener(this.listener);
        this.foreignKeyTable.setStatusListener(this.listener);

        this.columnTable.selectedIndexChanged((observable, oldValue, newValue) -> this.tableColumnExtraController.init(this.columnTable.getSelectedItem(), this.dbItem.client()));
        // 初始化索引列表
        this.indexTable.itemList().addListener((ListChangeListener<DamengIndex>) c -> {
            while (c.next() && (c.wasAdded() || c.wasReplaced())) {
                this.initIndexTable();
            }
        });
        this.initIndexTable();
        // 初始化外键列表
        this.foreignKeyTable.itemList().addListener((ListChangeListener<DamengForeignKey>) c -> {
            while (c.next() && (c.wasAdded() || c.wasReplaced())) {
                this.initForeignKeyTable();
            }
        });
        this.initForeignKeyTable();
    }

    private void initIndexTable() {
        List list= this.columnTable.getItems();
        for (DamengIndexControl index : this.indexTable.itemList()) {
            index.setColumnList(list);
        }
    }

    private void initForeignKeyTable() {
        List list= this.columnTable.getItems();
        for (DamengForeignKeyControl foreignKey : this.foreignKeyTable.itemList()) {
            foreignKey.setColumnList(list);
            foreignKey.setDbName(this.dbItem.dbName());
            foreignKey.setDbClient(this.dbItem.client());
        }
    }

    /**
     * 初始化预览
     */
    private void initPreview() {
        String sql;
        if (this.newData) {
            DamengCreateTableParam param = this.initCreateParam();
            if (param.tableName() == null) {
                param.setTableName(I18nHelper.unnamedTable());
            }
            sql = DamengTableCreateSqlGenerator.generateSql(param);
        } else {
            DamengAlertTableParam param = this.initAlertParam();
            sql = DamengTableAlertSqlGenerator.generateSql(param);
        }
        this.sqlPreview.text(sql);
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        // 初始化表单
        this.initTable();
        // // 组件管理
        // this.delete.visibleProperty().bind(this.add.visibleProperty());
        // this.delete.managedProperty().bind(this.add.managedProperty());
        // this.moveDown.visibleProperty().bind(this.moveUp.visibleProperty());
        // this.moveDown.managedProperty().bind(this.moveUp.managedProperty());
        // this.tableRowFormat.managedBindVisible();
        // this.tableAutoIncrementBox.managedBindVisible();
    }

    /**
     * 执行初始化
     *
     * @param table  表信息
     * @param dbItem db库树节点
     */
    public void init(DamengTable table, DamengDatabaseTreeItem dbItem) throws Exception {
        // 获取对象
        this.dbItem = dbItem;
        // 初始化引擎
        this.tableEngine.init(this.dbItem.client());

        // 设置缓存
        //CacheHelper.set("dameng:dbName", this.dbItem.dbName());
        //CacheHelper.set("dameng:dbClient", this.dbItem.client());

        // 初始化信息
        this.initInfo(table);

        // // 初始化监听器
        // this.initDBListener();

        // 监听组件
        DBStatusListenerManager.bindListener(this.tableEngine, this.listener);
        DBStatusListenerManager.bindListener(this.tableCharset, this.listener);
        DBStatusListenerManager.bindListener(this.tableComment, this.listener);
        DBStatusListenerManager.bindListener(this.tableRowFormat, this.listener);
        DBStatusListenerManager.bindListener(this.tableCollation, this.listener);
        DBStatusListenerManager.bindListener(this.tableAutoIncrement, this.listener);

        // 移除tab
        if (!this.dbItem.isSupportCheckFeature()) {
            this.tabPane.removeTab("checkTab");
        }
    }

    /**
     * 执行添加
     */
    @FXML
    private void doAdd() {
        if (this.tabPane.isSelectedTab("columnTab")) {
            this.addColumn();
        } else if (this.tabPane.isSelectedTab("indexTab")) {
            this.addIndex();
        } else if (this.tabPane.isSelectedTab("foreignKeyTab")) {
            this.addForeignKey();
        } else if (this.tabPane.isSelectedTab("triggerTab")) {
            this.addTrigger();
        } else if (this.tabPane.isSelectedTab("checkTab")) {
            this.addCheck();
        }
    }

    /**
     * 执行删除
     */
    @FXML
    private void doDelete() {
        if (this.tabPane.isSelectedTab("columnTab")) {
            this.deleteColumn();
        } else if (this.tabPane.isSelectedTab("indexTab")) {
            this.deleteIndex();
        } else if (this.tabPane.isSelectedTab("foreignKeyTab")) {
            this.deleteForeignKey();
        } else if (this.tabPane.isSelectedTab("triggerTab")) {
            this.deleteTrigger();
        } else if (this.tabPane.isSelectedTab("checkTab")) {
            this.deleteCheck();
        }
    }

    /**
     * 执行上移
     */
    @FXML
    private void doMoveUp() {
        try {
            if (this.tabPane.isSelectedTab("columnTab")) {
                // this.moveColumnUp();
                TableViewUtil.moveUp(this.columnTable);
            } else if (this.tabPane.isSelectedTab("indexTab")) {
                // this.moveIndexUp();
                TableViewUtil.moveUp(this.indexTable);
            } else if (this.tabPane.isSelectedTab("foreignKeyTab")) {
                // this.moveForeignKeyUp();
                TableViewUtil.moveUp(this.foreignKeyTable);
            } else if (this.tabPane.isSelectedTab("triggerTab")) {
                // this.moveTriggerUp();
                TableViewUtil.moveUp(this.triggerTable);
            } else if (this.tabPane.isSelectedTab("checkTab")) {
                // this.moveCheckUp();
                TableViewUtil.moveUp(this.checkTable);
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 执行下移
     */
    @FXML
    private void doMoveDown() {
        try {
            if (this.tabPane.isSelectedTab("columnTab")) {
                // this.moveColumnDown();
                TableViewUtil.moveDown(this.columnTable);
            } else if (this.tabPane.isSelectedTab("indexTab")) {
                // this.moveIndexDown();
                TableViewUtil.moveDown(this.indexTable);
            } else if (this.tabPane.isSelectedTab("foreignKeyTab")) {
                // this.moveForeignKeyDown();
                TableViewUtil.moveDown(this.foreignKeyTable);
            } else if (this.tabPane.isSelectedTab("triggerTab")) {
                // this.moveTriggerDown();
                TableViewUtil.moveDown(this.triggerTable);
            } else if (this.tabPane.isSelectedTab("checkTab")) {
                // this.moveCheckDown();
                TableViewUtil.moveDown(this.checkTable);
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public String tableName() {
        return this.damengTable.getName();
    }

    public String dbName() {
        return this.damengTable.getDbName();
    }

    @Override
    public List<? extends SubTabController> getSubControllers() {
        return List.of(this.tableColumnExtraController);
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    // public void setDbItem(DamengDatabaseTreeItem dbItem) {
    //     this.dbItem = dbItem;
    // }

    public boolean isUnsaved() {
        return unsaved;
    }

    // public void setUnsaved(boolean unsaved) {
    //     this.unsaved = unsaved;
    // }
}
