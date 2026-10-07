package cn.oyzh.easydameng.fx.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.popups.DamengIndexFieldPopupController;
import cn.oyzh.fx.gui.text.field.ChooseTextField;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;

/**
 * 索引字段文本框
 *
 * @author oyzh
 * @since 2024/7/16
 */
public class DamengIndexFieldTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectField());
    }

    /**
     * 构造索引字段文本框
     */
    public DamengIndexFieldTextFiled() {
    }

    /**
     * 索引
     */
    private DamengIndex dbIndex;

    /**
     * 字段列表
     */
    private List<DamengColumn> columnList;

    /**
     * 索引字段列表
     */
    private List<DamengIndex.IndexColumn> columns;

    /**
     * 构造索引字段文本框
     *
     * @param dbIndex    索引
     * @param columnList 字段列表
     * @param columns    索引字段列表
     */
    public DamengIndexFieldTextFiled(DamengIndex dbIndex, List<DamengColumn> columnList, List<DamengIndex.IndexColumn> columns) {
        this.dbIndex = dbIndex;
        this.columnList = columnList;
        this.setColumns(columns);
    }

    /**
     * 弹窗
     */
    private PopupAdapter popup;

    /**
     * 初始化弹窗
     */
    protected void initPopup() {
        this.disable();
        this.popup = PopupManager.parsePopup(DamengIndexFieldPopupController.class);
        this.popup.setProp("dbIndex", this.dbIndex);
        this.popup.setProp("columns", this.columns);
        this.popup.setProp("columnList", this.columnList);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            this.enable();
            this.skin().resetButtonColor();
            DamengIndexColumnListView listView = this.listView();
            if (listView != null) {
                this.columns = listView.getColumns();
            }
            this.initText();
        });
        this.popup.popup().setOnHiding(event -> {
            this.enable();
            this.skin().resetButtonColor();
        });
        this.popup.showPopup(this);
    }

    /**
     * 设置索引字段列表
     *
     * @param columns 索引字段列表
     */
    public void setColumns(List<DamengIndex.IndexColumn> columns) {
        this.columns = columns;
        this.initText();
    }

    /**
     * 初始化文本
     */
    protected void initText() {
        String text;
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isNotEmpty(this.columns)) {
            for (DamengIndex.IndexColumn column : this.columns) {
                builder.append(",");
                builder.append(column.getColumnName());
            }
            text = builder.substring(1);
        } else {
            text = "";
        }
        this.setText(text);
        this.setTipText(text);
    }

    /**
     * 获取列表视图
     *
     * @return 列表视图
     */
    protected DamengIndexColumnListView listView() {
        if (this.popup != null && this.popup.content() != null) {
            return (DamengIndexColumnListView) this.popup.content().lookup("#listView");
        }
        return null;
    }

    /**
     * 获取索引字段列表
     *
     * @return 索引字段列表
     */
    public List<DamengIndex.IndexColumn> getColumns() {
        return columns;
    }
}
