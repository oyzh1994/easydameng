package cn.oyzh.easydameng.trees.view;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.record.DamengDeleteRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengInsertRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.dameng.record.DamengRecordPrimaryKey;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengUpdateRecordParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.util.ShellDamengViewFactory;
import cn.oyzh.fx.db.DBRecordData;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库树视图节点
 *
 * @author oyzh
 * @since 2024/12/27
 */
public class DamengViewTreeItem extends DBTreeItem<DamengViewTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengView value;

    /**
     * 获取当前视图对象
     *
     * @return 当前视图对象
     */
    public DamengView value() {
        return value;
    }

    /**
     * 构造视图树节点
     *
     * @param view     视图对象
     * @param treeView 所属树视图
     */
    public DamengViewTreeItem(DamengView view, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = view;
        this.setValue(new DamengViewTreeItemValue(this));
    }

    @Override
    public DamengViewsTreeItem parent() {
        return (DamengViewsTreeItem) super.parent();
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
     * 获取连接信息
     *
     * @return 连接信息
     */
    public DamengConnect info() {
        return this.parent().info();
    }

    /**
     * 获取视图列信息
     *
     * @return 视图列集合
     */
    public DamengColumns viewColumns() {
        this.value.setColumns(new DamengColumns(this.columns()));
        return this.value.getColumns();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem open = MenuItemHelper.openView(this::onPrimaryDoubleClick);
        items.add(open);
        FXMenuItem design = MenuItemHelper.designView(this::designView);
        items.add(design);
        FXMenuItem renameView = MenuItemHelper.renameView(this::rename);
        items.add(renameView);
        items.add(MenuItemHelper.separator());
        FXMenuItem delete = MenuItemHelper.deleteView(this::delete);
        items.add(delete);
        FXMenuItem info = MenuItemHelper.viewInfo(this::viewInfo);
        items.add(info);
        return items;
    }

    /**
     * 查看视图信息
     */
     private void viewInfo() {
//         StageAdapter fxView = StageManager.parseStage(DamengViewInfoController.class, this.getWindow());
//         fxView.setProp("item", this);
//         fxView.display();
         ShellDamengViewFactory.viewInfo(this);
     }

    /**
     * 设计视图
     */
    private void designView() {
        DamengEventUtil.designView(this.value, this.dbItem());
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteView() + " " + this.value.getName() + "?", this.getWindow())) {
            return;
        }
        try {
            this.dbItem().dropView(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex, this.getWindow());
        }
    }

    /**
     * 获取所属模式树节点
     *
     * @return 模式树节点
     */
    public DamengSchemaTreeItem dbItem() {
        return this.parent().parent();
    }

    /**
     * 分页查询视图记录
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
        param.setStart(pageNo * limit);
        param.setSchema(this.schema());
        param.setTableName(this.viewName());
        List<DamengRecord> records = this.client().viewRecords(this.schema(), this.viewName(), pageNo * limit, limit, filters);
        long count = this.client().selectRecordCount(param);
        Paging<DamengRecord> paging = new Paging<>(records, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return this.parent().infoName();
    }

    /**
     * 获取视图列信息
     *
     * @return 视图列集合
     */
    public DamengColumns columns() {
        return new DamengColumns(this.client().viewColumns(this.schema(), this.viewName()));
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.viewOpen(this, this.dbItem());
    }

    /**
     * 获取主键列，优先返回自动递增列
     *
     * @return 主键列
     */
    public DamengColumn getPrimaryKey() {
        if (this.value.getColumns() == null) {
            this.viewColumns();
        }
        DamengColumn dbColumn = null;
        if (this.value.columns() != null) {
            for (DamengColumn column : this.value.columns()) {
                if (column.isAutoIncrement()) {
                    dbColumn = column;
                    break;
                }
            }
        }
        return dbColumn;
    }

    /**
     * 是否可更新
     *
     * @return 是否可更新
     */
    public boolean isUpdatable() {
        return this.value.isUpdatable();
    }

    /**
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String viewName() {
        return this.value.getName();
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
        param.setTableName(this.viewName());
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
        param.setRecord(recordData);
        param.setSchema(this.schema());
        param.setTableName(this.viewName());
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
        param.setTableName(this.viewName());
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
        param.setTableName(this.viewName());
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
        param.setTableName(this.viewName());
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
        param.setTableName(this.viewName());
        param.setUpdateRecord(recordData);
        param.setRecord(originalRecordData);
        return this.client().updateRecord(param);
    }

    @Override
    public void rename() {
        try {
            String viewName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.viewName(), this.getWindow());
            // 名称为null或者跟当前名称相同，则忽略
            if (viewName == null || Objects.equals(viewName, this.viewName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(viewName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent(), this.getWindow());
                return;
            }
            String oldName = this.viewName();
            // 修改名称
            this.dbItem().renameTable(oldName, viewName);
            this.value.setName(viewName);
            this.refresh();
            DamengEventUtil.viewRenamed(this, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, this.getWindow());
        }
    }
}
