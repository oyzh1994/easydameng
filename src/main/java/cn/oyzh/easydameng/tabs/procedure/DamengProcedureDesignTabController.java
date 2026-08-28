package cn.oyzh.easydameng.tabs.procedure;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.routine.DamengRoutineParam;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.fx.DBSecurityTypeComboBox;
import cn.oyzh.easydameng.fx.DBStatusTableView;
import cn.oyzh.easydameng.generator.routine.DamengProcedureSqlGenerator;
import cn.oyzh.easydameng.listener.DBStatusListener;
import cn.oyzh.easydameng.listener.DBStatusListenerManager;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.editor.incubator.Editor;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
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
import java.util.ResourceBundle;


/**
 * db存储过程内容组件
 *
 * @author oyzh
 * @since 2024/07/08
 */
public class DamengProcedureDesignTabController extends RichTabController {

    /**
     * 过程
     */
    private DamengProcedure procedure;

    public DamengProcedure getProcedure() {
        return procedure;
    }

    /**
     * db数据库树节点
     */
    private DamengDatabaseTreeItem dbItem;

    /**
     * 定义
     */
    @FXML
    private Editor definition;

    /**
     * 预览
     */
    @FXML
    private Editor preview;

    /**
     * 切换面板
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * 安全性
     */
    @FXML
    private DBSecurityTypeComboBox securityType;

    /**
     * 特征，parallelEnable
     */
    @FXML
    private FXCheckBox parallelEnable;

    /**
     * 参数表单
     */
    @FXML
    private DBStatusTableView<DamengRoutineParam> paramTable;

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
     * 执行初始化
     *
     * @param procedure 查询对象
     * @param dbItem    db库树节点
     */
    public void init(DamengProcedure procedure, DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
        this.procedure = procedure;
        // 更新新数据标志位
        this.newData = this.procedure.isNew();
        StageManager.showMask(this::doInit);
    }

    /**
     * 执行初始化
     */
    private void doInit() {

        // 初始化监听器
        this.initDBListener();

        // 初始化信息
        FXUtil.runWait(this::initInfo);
    }

    /**
     * 初始化数据监听器
     */
    private void initDBListener() {
        // 销毁监听器
        if (this.listener != null) {
            this.listener.destroy();
            DBStatusListenerManager.unbindListener(this.definition, this.listener);
            DBStatusListenerManager.unbindListener(this.securityType, this.listener);
            DBStatusListenerManager.unbindListener(this.parallelEnable, this.listener);
        }
        // 初始化监听器
        this.listener = new DBStatusListener(this.procedure.getDbName() + ":" + this.procedure.getName()) {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                initChangedFlag();
            }
        };
        DBStatusListenerManager.bindListener(this.definition, this.listener);
        DBStatusListenerManager.bindListener(this.securityType, this.listener);
        DBStatusListenerManager.bindListener(this.parallelEnable, this.listener);
        this.paramTable.setStatusListener(this.listener);
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
     * 初始化信息
     */
    protected void initInfo() {
        // 更新初始化标志位
        this.initiating = true;

        // 如果是新数据，则默认触发变更
        if (this.newData) {
            this.unsaved = true;
            String defDefinition = """
                    BEGIN
                        -- Routine body goes here...
                    
                        RETURN;
                    END;
                    """;
            this.definition.setText(defDefinition);
        } else {
            // 查询函数信息
            this.procedure = this.dbItem.selectProcedure(this.procedure.getName());
            // 初始化数据
            this.definition.setText(this.procedure.getDefinition());
            this.definition.forgetHistory();
            this.paramTable.setItem(this.procedure.getParams());
            this.securityType.select(this.procedure.getSecurityType());
            String characteristic = this.procedure.getCharacteristic();
            if (StringUtil.isNotBlank(characteristic)) {
                if (characteristic.contains("PARALLEL_ENABLE")) {
                    this.parallelEnable.setSelected(true);
                }
            }
        }

        // 标记为结束
        FXUtil.runPulse(() -> this.initiating = false);
    }

    /**
     * 保存
     */
    @FXML
    private void save() {
        StageManager.showMask(this::doSave);
    }

    /**
     * 函数名称
     */
    private String procedureName;

    /**
     * 执行保存
     */
    private void doSave() {
        try {
            // 创建临时对象
            DamengProcedure tempProcedure = this.tempData();

            // 过程名称
            if (this.newData) {
                procedureName = MessageBox.prompt(I18nHelper.pleaseInputFunctionName(), procedureName);
                if (procedureName == null) {
                    return;
                }
                tempProcedure.setName(procedureName);
            } else {
                procedureName = tempProcedure.getName();
            }

            // 创建过程
            if (this.newData) {
                this.dbItem.createProcedure(tempProcedure);
                DamengProcedure procedure = this.dbItem.selectProcedure(procedureName);
                this.dbItem.getProcedureTypeChild().addProcedure(procedure);
                DamengEventUtil.procedureAdded(this.dbItem);
                this.initDBListener();
            } else {// 修改过程
                this.dbItem.alertProcedure(tempProcedure);
                DamengEventUtil.procedureAlerted(procedureName, this.dbItem);
            }
            // 更新保存标志位
            this.unsaved = false;
            // 更新新数据标志位
            this.newData = false;
            this.procedure = tempProcedure;
            // 刷新tab
            FXUtil.runWait(this::initInfo);
            // 重置表格
            this.paramTable.reset();
            // 初始化预览
            this.initPreview();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        } finally {
            this.flushTab();
        }
    }

    /**
     * 获取临时数据
     *
     * @return 临时数据
     */
    private DamengProcedure tempData() {
        // 创建临时对象
        DamengProcedure tempProcedure = new DamengProcedure();
        tempProcedure.setName(this.procedure.getName());

        // 基本信息处理
        tempProcedure.setDbName(this.procedure.getDbName());
        tempProcedure.setParams(this.paramTable.getItems());
        tempProcedure.setDefinition(this.definition.getTextTrim());
        tempProcedure.setSecurityType(this.securityType.getSelectedItem());

        // 特征处理
        StringBuilder characteristic = new StringBuilder();
        if (this.parallelEnable.isSelected()) {
            characteristic.append(",PARALLEL_ENABLE");
        }
        if (!characteristic.isEmpty()) {
            tempProcedure.setCharacteristic(characteristic.substring(1));
        }

        return tempProcedure;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        // 监听事件
        NodeUtil.nodeOnCtrlS(this.getTab(), this::save);
        NodeUtil.nodeOnCtrlS(this.definition, this::save);
        NodeUtil.nodeOnCtrlS(this.securityType, this::save);
        NodeUtil.nodeOnCtrlS(this.parallelEnable, this::save);
        this.paramTable.setCtrlSAction(this::save);

        // 切换面板监听
        this.tabPane.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 1) {
                NodeGroupUtil.display(this.getTab(), "param");
            } else {
                NodeGroupUtil.disappear(this.getTab(), "param");
            }
            if (newValue.intValue() == 3) {
                this.initPreview();
            }
        });
    }

    /**
     * 初始化预览
     */
    private void initPreview() {
        DamengProcedure temp = this.tempData();
        if (StringUtil.isBlank(temp.getName())) {
            temp.setName("Unnamed_Procedure");
        }
        String sql = DamengProcedureSqlGenerator.INSTANCE.generate(temp);
        this.preview.text(sql);
    }

    /**
     * 添加参数
     */
    @FXML
    private void addParam() {
        try {
            DamengRoutineParam param = new DamengRoutineParam();
            param.setCreated(true);
            this.paramTable.addItem(param);
            this.paramTable.selectLast();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 删除参数
     */
    @FXML
    private void deleteParam() {
        try {
            DamengRoutineParam param = this.paramTable.getSelectedItem();
            if (param != null) {
                // 非新增的数据进行提示
                if (!param.isCreated() && !MessageBox.confirm(I18nHelper.delete() + " " + param.getName())) {
                    return;
                }
                this.paramTable.getItems().remove(param);
                // 从table移除数据
                if (param.isCreated()) {
                    this.paramTable.removeItem(param);
                } else {// 标记为删除
                    param.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移参数
     */
    @FXML
    private void moveParamUp() {
        try {
            TableViewUtil.moveUp(this.paramTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移参数
     */
    @FXML
    private void moveParamDown() {
        try {
            TableViewUtil.moveDown(this.paramTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public boolean isUnsaved() {
        return unsaved;
    }

    public void setUnsaved(boolean unsaved) {
        this.unsaved = unsaved;
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 初始化参数列表
        this.paramTable.itemList().addListener((ListChangeListener<DamengRoutineParam>) c -> {
            while (c.next() && (c.wasAdded() || c.wasReplaced())) {
                this.initParamTable();
            }
        });
        this.initParamTable();
    }

    private void initParamTable() {
        for (DamengRoutineParam index : this.paramTable.itemList()) {
            index.setDbClient(this.dbItem.client());
        }
    }
}
