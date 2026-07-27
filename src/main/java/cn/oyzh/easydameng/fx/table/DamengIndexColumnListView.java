package cn.oyzh.easydameng.fx.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db索引字段选择框
 *
 * @author oyzh
 * @since 2024/07/16
 */
public class DamengIndexColumnListView extends FXListView<FXHBox> {

    /**
     * 字段名称列表
     */
    private List<String> columnNames;

    public DamengIndexColumnListView() {

    }

    public void init(DamengIndex dbIndex, List<DamengColumn> columnList) {
        this.clearItems();
        this.columnNames = columnList.parallelStream().map(DamengColumn::getName).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(dbIndex.getColumns())) {
            for (DamengIndex.IndexColumn column : dbIndex.getColumns()) {
                this.addColumn(column);
            }
        }
    }

    public void addColumn(DamengIndex.IndexColumn column) {
        FXComboBox<String> comboBox = new FXComboBox<>();
        comboBox.setRealWidth(150);
        comboBox.setRealHeight(25);
        comboBox.setItem(this.columnNames);
        comboBox.addClass("popover-item");
        if (StringUtil.isNotBlank(column.getColumnName())) {
            comboBox.select(column.getColumnName());
        } else {
            comboBox.selectFirst();
        }
        ListViewUtil.selectRowOnMouseClicked(comboBox, comboBox);
        this.addItem(comboBox);
    }

    public List<DamengIndex.IndexColumn> getColumns() {
        List<DamengIndex.IndexColumn> list = new ArrayList<>();
        for (Node item : this.getItems()) {
            FXComboBox<String> comboBox = (FXComboBox<String>) item;
            DamengIndex.IndexColumn indexColumn = new DamengIndex.IndexColumn(comboBox.getValue());
            list.add(indexColumn);
        }
        return list;
    }
}
