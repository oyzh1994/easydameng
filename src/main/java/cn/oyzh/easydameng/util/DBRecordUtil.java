package cn.oyzh.easydameng.util;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.record.DamengRecordProperty;
import cn.oyzh.fx.editor.incubator.control.JsonTextFiled;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.text.field.BinaryTextFiled;
import cn.oyzh.fx.gui.text.field.BitTextField;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.DateTextField;
import cn.oyzh.fx.gui.text.field.DateTimeTextField;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.ExampleTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.SelectTextFiled;
import cn.oyzh.fx.gui.text.field.TimeTextField;
import cn.oyzh.fx.gui.text.field.YearTextField;
import cn.oyzh.fx.plus.font.FontUtil;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/7/17
 */
public class DBRecordUtil {

    public static Node getNode(DamengRecordProperty property, Object object, DamengColumn column) {
        Node node;
        String columnType = column.getType();
        if (column.supportJson()) {
            JsonTextFiled textField = new JsonTextFiled();
            textField.setValue(object);
            node = textField;
        } else if (column.supportBinary()) {
            BinaryTextFiled textField = new BinaryTextFiled();
            textField.setValue(object);
            node = textField;
        } else if (column.supportEnum()) {
            SelectTextFiled<String> textField = new SelectTextFiled<>();
            textField.setEditable(false);
            textField.setItemList(column.getValueList());
            textField.setValue(object);
            node = textField;
        } else if (column.supportInteger()) {
            NumberTextField textField = new NumberTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportDigits()) {
            DecimalTextField textField = new DecimalTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportBit()) {
            BitTextField textField = new BitTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isDateType()) {
            DateTextField textField = new DateTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isTimeType()) {
            TimeTextField textField = new TimeTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isYearType()) {
            YearTextField textField = new YearTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportTimestamp()) {
            DateTimeTextField textField = new DateTimeTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportString()) {
            ClearableTextField textField = new ClearableTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportGeometry()) {
            ExampleTextField textField = new ExampleTextField();
            textField.setExample(column.exampleValue());
            textField.setValue(object);
            node = textField;
        } else {
            ClearableTextField textField = new ClearableTextField();
            textField.setValue(object);
            node = textField;
        }
        if (node instanceof TextField textField) {
            if (object == null) {
                textField.setPromptText(nullPromptText());
            }
            textField.setContextMenu(getColumnContextMenu(property));
            textField.textProperty().addListener((observable, oldValue, newValue) -> property.setChanged(true));
        }
        return node;
    }

    public static String formatValue(Object object, DamengColumn column) {
        String val = null;
        String columnType = column.getType();
        if (StringUtil.isBlank(columnType)) {
            if (object instanceof CharSequence sequence) {
                val = sequence.toString();
            } else if (object instanceof byte[] bytes) {
                val = new String(bytes);
            } else if (object instanceof Date date) {
                val = date.toString();
            } else if (object != null) {
                val = object.toString();
            }
        } else if (column.supportJson()) {
            val = JsonTextFiled.format(object);
        } else if (column.supportBinary()) {
            val = BinaryTextFiled.format(object);
        } else if (column.supportEnum()) {
            val = SelectTextFiled.format(object);
        } else if (column.supportInteger()) {
            val = NumberTextField.format(object);
        } else if (column.supportDigits()) {
            val = DecimalTextField.format(object);
        } else if (column.supportBit()) {
            val = BitTextField.format(object);
        } else if (column.isDateType()) {
            val = DateTextField.format(object);
        } else if (column.isTimeType()) {
            val = TimeTextField.format(object);
        } else if (column.isYearType()) {
            val = YearTextField.format(object);
        } else if (column.supportTimestamp()) {
            val = DateTimeTextField.format(object);
        } else if (column.supportString()) {
            val = ClearableTextField.format(object);
        } else if (column.supportGeometry()) {
            val = ExampleTextField.format(object);
        } else {
            val = ClearableTextField.format(object);
        }
        return val;
    }

    public static String nullPromptText() {
        return "(Null)";
    }

    // public static double suitableColumnWidth(String columnType) {
    //     if (DBColumnUtil.isGeometryType(columnType)) {
    //         return 120;
    //     }
    //     if (DBColumnUtil.isPointType(columnType)) {
    //         return 110;
    //     }
    //     if (DBColumnUtil.isMultiPointType(columnType)) {
    //         return 200;
    //     }
    //     if (DBColumnUtil.isPolygonType(columnType)) {
    //         return 220;
    //     }
    //     if (DBColumnUtil.isMultiPolygonType(columnType)) {
    //         return 420;
    //     }
    //     if (DBColumnUtil.isLineStringType(columnType)) {
    //         return 180;
    //     }
    //     if (DBColumnUtil.isMultiLineStringType(columnType)) {
    //         return 320;
    //     }
    //     if (DBColumnUtil.isGeomCollectionType(columnType)) {
    //         return 600;
    //     }
    //     if (DBColumnUtil.isYearType(columnType)) {
    //         return 80;
    //     }
    //     if (DBColumnUtil.supportJson(columnType)) {
    //         return 150;
    //     }
    //     if (DBColumnUtil.supportTimestamp(columnType)) {
    //         return 160;
    //     }
    //     if (DBColumnUtil.supportBinary(columnType)) {
    //         return 140;
    //     }
    //     if (DBColumnUtil.isDateType(columnType)) {
    //         return 110;
    //     }
    //     return 100;
    // }

    /**
     * 计算合适的字段宽
     *
     * @param column 字段
     * @return 结果
     */
    public static double suitableColumnWidth(DamengColumn column) {
        double w1 = FontUtil.textWidth(column.getName());
        double w2;
        if (column.supportSize() && column.getSize() != null) {
            w2 = FontUtil.textWidth(column.getType() + "(" + column.getSize() + ")");
        } else {
            w2 = FontUtil.textWidth(column.getType());
        }
        double w3 = Math.max(w1, w2);
        return w3 + 30;
    }

    public static ContextMenu getColumnContextMenu(DamengRecordProperty property) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(getColumnMenuItem(property));
        return contextMenu;
    }

    public static List<FXMenuItem> getColumnMenuItem(DamengRecordProperty property) {
        List<FXMenuItem> menuItems = new ArrayList<>();
        FXMenuItem copy = MenuItemHelper.copy_no_graphic(property::vCopy);
        menuItems.add(copy);
        FXMenuItem paste = MenuItemHelper.paste_no_graphic(property::vPaste);
        menuItems.add(paste);
        FXMenuItem setToNull = MenuItemHelper.setToNull_no_graphic(property::vSetToNull);
        menuItems.add(setToNull);
        FXMenuItem setToEmptyString = MenuItemHelper.setToEmptyString_no_graphic(property::vSetToEmptyString);
        menuItems.add(setToEmptyString);
        FXMenuItem copyAsInsertStatement = MenuItemHelper.copyAsInsertStatement_no_graphic(property::vCopyAsInsertSql);
        menuItems.add(copyAsInsertStatement);
        FXMenuItem copyAsUpdateStatement = MenuItemHelper.copyAsUpdateStatement_no_graphic(property::vCopyAsUpdateSql);
        menuItems.add(copyAsUpdateStatement);
        return menuItems;
    }
}
