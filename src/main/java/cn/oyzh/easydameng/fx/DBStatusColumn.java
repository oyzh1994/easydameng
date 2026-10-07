package cn.oyzh.easydameng.fx;

import cn.oyzh.fx.db.DBObject;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 数据库对象状态表格列
 *
 * @author oyzh
 * @since 2024/7/22
 */
public class DBStatusColumn<S extends DBObject> extends FXTableColumn<S, Object> {

    /**
     * 构造状态表格列
     */
    public DBStatusColumn() {
        this.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.setMaxWidth(25);
        this.setRealWidth(25);
        this.setSortable(false);
        this.setResizable(false);
        this.setReorderable(false);
    }
}
