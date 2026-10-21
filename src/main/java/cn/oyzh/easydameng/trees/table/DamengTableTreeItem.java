package cn.oyzh.easydameng.trees.table;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.controller.data.ShellDamengDataDumpController;
import cn.oyzh.easydameng.controller.data.ShellDamengDataExportController;
import cn.oyzh.easydameng.controller.table.ShellDamengTableInfoController;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.record.DamengDeleteRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengInsertRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.dameng.record.DamengRecordPrimaryKey;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengUpdateRecordParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.util.DamengI18nHelper;
import cn.oyzh.fx.db.DBObjects;
import cn.oyzh.fx.db.DBRecordData;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.CopySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库树表节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class DamengTableTreeItem extends DBTreeItem<DamengTableTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengTable value;

    /**
     * 构造表树节点
     *
     * @param table    表对象
     * @param treeView 所属树视图
     */
    public DamengTableTreeItem(DamengTable table, RichTreeView treeView) {
        super(treeView);
        this.value = table;
        this.setValue(new DamengTableTreeItemValue(this));
    }

    @Override
    public DamengTablesTreeItem parent() {
        return (DamengTablesTreeItem) super.parent();
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public ShellDamengClient client() {
        return this.parent().client();
    }

    /**
     * 获取所属模式名称
     *
     * @return 模式名称
     */
    public String schema() {
        return this.parent().schema();
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.value.getName();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public DamengConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openTable = MenuItemHelper.openTable(this::onPrimaryDoubleClick);
        items.add(openTable);
        FXMenuItem updateTable = MenuItemHelper.designTable(this::designTable);
        items.add(updateTable);
        FXMenuItem renameTable = MenuItemHelper.renameTable(this::rename);
        items.add(renameTable);
        FXMenuItem clearTable = MenuItemHelper.clearTableData(this::clearTableData);
        items.add(clearTable);
        FXMenuItem truncateTable = MenuItemHelper.truncateTable(this::truncateTable);
        items.add(truncateTable);
        FXMenuItem dropTable = MenuItemHelper.deleteTable(this::delete);
        items.add(dropTable);
        items.add(MenuItemHelper.separator());
        FXMenuItem dumpTable = MenuItemHelper.dumpData(this::dump);
        items.add(dumpTable);
        FXMenuItem exportTable = MenuItemHelper.exportData(this::export);
        items.add(exportTable);
        FXMenuItem tableInfo = MenuItemHelper.tableInfo(this::tableInfo);
        items.add(tableInfo);

        // 克隆表
        Menu cloneTable = MenuItemHelper.menu(I18nHelper.cloneTable(), new CopySVGGlyph());
        MenuItem clone1 = MenuItemHelper.menuItem(DamengI18nHelper.tableTip3(), () -> this.cloneTable(true));
        MenuItem clone2 = MenuItemHelper.menuItem(DamengI18nHelper.tableTip4(), () -> this.cloneTable(false));
        cloneTable.getItems().addAll(clone1, clone2);

        items.add(cloneTable);
        return items;
    }

    /**
     * 克隆表
     *
     * @param includeRecord 是否包含记录
     */
    private void cloneTable(boolean includeRecord) {
        StageManager.showMask(this.getWindow(), () -> this.doCloneTable(includeRecord));
    }

    /**
     * 执行克隆表
     *
     * @param includeRecord 是否包含记录
     */
    private void doCloneTable(boolean includeRecord) {
        try {
            String cloneTable = this.dbItem().cloneTable(this.tableName(), includeRecord);
            DamengTable damengTable = this.dbItem().selectTable(cloneTable);
            this.dbItem().getTableTypeChild().addTable(damengTable);
        } catch (Exception ex) {
            MessageBox.exception(ex, this.getWindow());
        }
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataDumpController.class, this.getWindow());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.setProp("tableName", this.tableName());
        fxView.display();
    }

    /**
     * 导出
     */
    private void export() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataExportController.class, this.getWindow());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.setProp("tableName", this.tableName());
        fxView.display();
    }

    /**
     * 设计表
     */
    private void designTable() {
        this.reloadChild();
        DamengEventUtil.designTable(this.value, this.dbItem());
    }

    /**
     * 截断表
     */
    private void truncateTable() {
        if (MessageBox.confirm(I18nHelper.truncateTable() + "[" + this.tableName() + "]", this.getWindow())) {
            try {
                this.dbItem().truncateTable(this.tableName());
                DamengEventUtil.tableTruncated(this, this.dbItem());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, this.getWindow());
            }
        }
    }

    /**
     * 清空表
     */
    private void clearTableData() {
        try {
            if (MessageBox.confirm(I18nHelper.clearTableData() + "[" + this.tableName() + "]", this.getWindow())) {
                this.dbItem().clearTable(this.tableName());
                DamengEventUtil.tableCleared(this, this.dbItem());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex, this.getWindow());
        }
    }

    @Override
    public void delete() {
        try {
            if (MessageBox.confirm(I18nHelper.deleteTable() + "[" + this.tableName() + "]", this.getWindow())) {
                this.dbItem().dropTable(this.tableName());
                DamengEventUtil.tableDropped(this, this.dbItem());
                this.remove();
            }
        } catch (Exception ex) {
            MessageBox.exception(ex, this.getWindow());
        }
    }

    /**
     * 查看表信息
     */
    private void tableInfo() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengTableInfoController.class, this.getWindow());
        fxView.setProp("item", this);
        fxView.display();
    }

    @Override
    public void rename() {
        try {
            // if (!MessageBox.confirm(DBI18nHelper.tableTip2())) {
            //     return;
            // }
            String tableName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.value.getName(), this.getWindow());
            // 名称为null或者跟当前名称相同，则忽略
            if (tableName == null || Objects.equals(tableName, this.value.getName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(tableName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent(), this.getWindow());
                return;
            }
            // if (this.dbItem().existTable(tableName)) {
            //     MessageBox.warn(I18nHelper.table() + " " + tableName + I18nHelper.alreadyExists());
            //     return;
            // }
            String oldName = this.value.getName();
            // 修改名称
            this.dbItem().renameTable(oldName, tableName);
            this.value.setName(tableName);
            this.refresh();
            DamengEventUtil.tableRenamed(this, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, this.getWindow());
        }
    }

    /**
     * 获取所属模式树节点
     *
     * @return 模式树节点
     */
    public DamengSchemaTreeItem dbItem() {
        if (this.parent() == null) {
            return null;
        }
        return this.parent().parent();
    }

    /**
     * 分页查询表记录
     *
     * @param pageNo  页码
     * @param limit   每页数量
     * @param filters 过滤条件
     * @param columns 查询列
     * @return 分页记录
     */
    public Paging<DamengRecord> recordPage(long pageNo, long limit, List<DamengRecordFilter> filters, List<DamengColumn> columns) {
        DamengSelectRecordParam param = new DamengSelectRecordParam();
        param.setLimit(limit);
        param.setFilters(filters);
        param.setColumns(columns);
        param.setSchema(this.schema());
        param.setStart(pageNo * limit);
        param.setTableName(this.tableName());
        List<DamengRecord> rows = this.client().selectRecords(param);
        long count = this.client().selectRecordCount(param);
        Paging<DamengRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return parent().infoName();
    }

    /**
     * 获取表列信息
     *
     * @return 表列集合
     */
    public DamengColumns columns() {
        return this.client().selectColumns(new DamengSelectColumnParam(this.schema(), this.tableName()));
    }

    /**
     * 获取表索引
     *
     * @return 索引集合
     */
    public List<DamengIndex> indexes() {
        return this.client().indexes(this.schema(), this.tableName());
    }

    /**
     * 获取表检查约束
     *
     * @return 检查约束集合
     */
    public DBObjects<DamengCheck> checks() {
        return this.client().checks(this.schema(), this.tableName());
    }

    /**
     * 获取表外键
     *
     * @return 外键集合
     */
    public List<DamengForeignKey> foreignKeys() {
        return this.client().foreignKeys(this.schema(), this.tableName());
    }

    /**
     * 获取表触发器
     *
     * @return 触发器集合
     */
    public List<DamengTrigger> triggers() {
        return this.client().selectTriggers(this.schema(), this.tableName());
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.tableOpen(this, this.dbItem());
    }

    /**
     * 表列缓存
     */
    private DamengColumns columns;

    /**
     * 获取主键列，优先返回自动递增列
     *
     * @return 主键列
     */
    public DamengColumn getPrimaryKey() {
        if (columns == null) {
            columns = this.columns();
        }
        DamengColumn dbColumn = null;
        for (DamengColumn column : this.columns.primaryKeys()) {
            if (column.isAutoIncrement()) {
                dbColumn = column;
                break;
            }
        }
        if (dbColumn == null) {
            for (DamengColumn column : this.columns.primaryKeys()) {
                return column;
            }
        }
        return dbColumn;
    }

    @Override
    public void loadChild() {
        try {
            DamengTable table = this.client().selectTable(this.schema(), this.tableName());
            if (table != null) {
                this.value.copy(table);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    /**
     * 是否存在主键
     *
     * @return 是否存在主键
     */
    public boolean hasPrimaryKey() {
        if (columns == null) {
            columns = this.columns();
        }
        return this.columns.primaryKeys().isEmpty();
    }

    /**
     * 新增记录
     *
     * @param recordData 记录数据
     * @return 影响行数
     */
    public int insertRecord(DBRecordData recordData) {
        return this.insertRecord(recordData, null);
    }

    /**
     * 新增记录
     *
     * @param recordData 记录数据
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int insertRecord(DBRecordData recordData, DamengRecordPrimaryKey primaryKey) {
        DamengInsertRecordParam param = new DamengInsertRecordParam();
        param.setRecord(recordData);
        param.setSchema(this.schema());
        param.setPrimaryKey(primaryKey);
        param.setTableName(this.tableName());
        return this.client().insertRecord(param);
    }

    /**
     * 删除记录
     *
     * @param recordData 记录数据
     * @return 影响行数
     */
    public int deleteRecord(DBRecordData recordData) {
        DamengDeleteRecordParam param = new DamengDeleteRecordParam();
        param.setSchema(this.schema());
        param.setTableName(this.tableName());
        param.setRecord(recordData);
        return this.client().deleteRecord(param);
    }

    /**
     * 删除记录
     *
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int deleteRecord(DamengRecordPrimaryKey primaryKey) {
        DamengDeleteRecordParam param = new DamengDeleteRecordParam();
        param.setSchema(this.schema());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        return this.client().deleteRecord(param);
    }

    /**
     * 查询单条记录
     *
     * @param primaryKey 主键
     * @return 记录
     */
    public DamengRecord selectRecord(DamengRecordPrimaryKey primaryKey) {
        DamengSelectRecordParam param = new DamengSelectRecordParam();
        param.setSchema(this.schema());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        return this.client().selectRecord(param);
    }

    /**
     * 更新记录
     *
     * @param recordData 记录数据
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int updateRecord(DBRecordData recordData, DamengRecordPrimaryKey primaryKey) {
        DamengUpdateRecordParam param = new DamengUpdateRecordParam();
        param.setSchema(this.schema());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        param.setUpdateRecord(recordData);
        return this.client().updateRecord(param);
    }

    /**
     * 更新记录
     *
     * @param recordData         记录数据
     * @param originalRecordData 原始记录数据
     * @return 影响行数
     */
    public int updateRecord(DBRecordData recordData, DBRecordData originalRecordData) {
        DamengUpdateRecordParam param = new DamengUpdateRecordParam();
        param.setSchema(this.schema());
        param.setTableName(this.tableName());
        param.setUpdateRecord(recordData);
        param.setRecord(originalRecordData);
        return this.client().updateRecord(param);
    }

    /**
     * 获取当前表对象
     *
     * @return 当前表对象
     */
    public DamengTable value() {
        return value;
    }
}
