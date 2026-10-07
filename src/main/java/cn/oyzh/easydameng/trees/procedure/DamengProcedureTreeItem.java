package cn.oyzh.easydameng.trees.procedure;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.util.ShellDamengViewFactory;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库树存储过程节点
 *
 * @author oyzh
 * @since 2024/12/27
 */
public class DamengProcedureTreeItem extends DBTreeItem<DamengProcedureTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengProcedure value;

    /**
     * 获取当前存储过程对象
     *
     * @return 当前存储过程对象
     */
    public DamengProcedure value() {
        return value;
    }

    /**
     * 构造存储过程树节点
     *
     * @param procedure 存储过程对象
     * @param treeView  所属树视图
     */
    public DamengProcedureTreeItem(DamengProcedure procedure, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = procedure;
        this.setValue(new DamengProcedureTreeItemValue(this));
    }

    @Override
    public DamengProceduresTreeItem parent() {
        return (DamengProceduresTreeItem) super.parent();
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
        // FXMenuItem open = MenuItemHelper.openProcedure( this::onPrimaryDoubleClick);
        // items.add(open);
        FXMenuItem design = MenuItemHelper.designProcedure(this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem delete = MenuItemHelper.deleteProcedure(this::delete);
        items.add(delete);
        FXMenuItem info = MenuItemHelper.procedureInfo(this::procedureInfo);
        items.add(info);
        return items;
    }

    /**
     * 查看存储过程信息
     */
    private void procedureInfo() {
        ShellDamengViewFactory.procedureInfo(this);
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteProcedure() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropProcedure(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
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
     * 获取所属模式名称
     *
     * @return 模式名称
     */
    public String schema() {
        return this.parent().schema();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return this.parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.designProcedure(this.value, this.dbItem());
    }

    /**
     * 获取存储过程名称
     *
     * @return 存储过程名称
     */
    public String procedureName() {
        return this.value.getName();
    }
}
