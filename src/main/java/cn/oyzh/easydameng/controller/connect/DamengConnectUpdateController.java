package cn.oyzh.easydameng.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengSSHConfig;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.fx.DBTypeComboBox;
import cn.oyzh.easydameng.store.DamengConnectStore;
import cn.oyzh.easydameng.store.DamengSSHConfigStore;
import cn.oyzh.easydameng.util.DBConnectUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * db信息修改业务
 *
 * @author oyzh
 * @since 2023/12/22
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/damengConnectUpdate.fxml"
)
public class DamengConnectUpdateController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

    /**
     * tab组件
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * db信息
     */
    private DamengConnect damengConnect;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 备注
     */
    @FXML
    private FXTextArea remark;

    /**
     * 类型
     */
    @FXML
    private DBTypeComboBox type;

    /**
     * 用户名
     */
    @FXML
    private ClearableTextField user;

    /**
     * 认证密码
     */
    @FXML
    private ClearableTextField password;

    /**
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 超时时间
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 执行超时
     */
    @FXML
    private NumberTextField executeTimeOut;

    /**
     * ssh面板
     */
    @FXML
    private FXTab sshTab;

    /**
     * 开启ssh
     */
    @FXML
    private FXToggleSwitch sshForward;

    /**
     * ssh主机地址
     */
    @FXML
    private ClearableTextField sshHost;

    /**
     * ssh主机端口
     */
    @FXML
    private PortTextField sshPort;

    /**
     * ssh主机端口
     */
    @FXML
    private NumberTextField sshTimeout;

    /**
     * ssh主机用户
     */
    @FXML
    private ClearableTextField sshUser;

    /**
     * ssh主机密码
     */
    @FXML
    private ClearableTextField sshPassword;

    /**
     * db连接储存对象
     */
    private final DamengConnectStore connectStore = DamengConnectStore.INSTANCE;

    /**
     * ssh配置储存
     */
    private final DamengSSHConfigStore sshConfigStore = DamengSSHConfigStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (!this.hostPort.validate()) {
            this.tabPane.select(0);
            return null;
        }
        if (!this.hostIp.validate()) {
            this.tabPane.select(0);
            return null;
        }
        hostText = hostIp + ":" + this.hostPort.getValue();
        return hostText;
    }

    /**
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private DamengSSHConfig getSSHConfig() {
        DamengSSHConfig sshConfig = new DamengSSHConfig();
        sshConfig.setHost(this.sshHost.getText());
        sshConfig.setUser(this.sshUser.getText());
        sshConfig.setPort(this.sshPort.getIntValue());
        sshConfig.setPassword(this.sshPassword.getText());
        sshConfig.setTimeout(this.sshTimeout.getIntValue());
        return sshConfig;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StringUtil.isBlank(host) || StringUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        } else {
            DamengConnect damengConnect = new DamengConnect();
            damengConnect.setHost(host);
            damengConnect.setConnectTimeOut(3);
            damengConnect.setId(this.damengConnect.getId());
            damengConnect.setUser(this.user.getText());
            damengConnect.setPassword(this.password.getText());
            damengConnect.setSshForward(this.sshForward.isSelected());
            if (damengConnect.isSSHForward()) {
                damengConnect.setSshConfig(this.getSSHConfig());
            }
            DBConnectUtil.testConnect(this.stage, damengConnect);
        }
    }

    /**
     * 修改db信息
     */
    @FXML
    private void update() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StringUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            this.damengConnect.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();

            this.damengConnect.setHost(host.trim());
            this.damengConnect.setUser(this.user.getText());
            // ssh配置
            this.damengConnect.setSshConfig(this.getSSHConfig());
            this.damengConnect.setSshForward(this.sshForward.isSelected());
            this.damengConnect.setType(this.type.getType());
            this.damengConnect.setRemark(this.remark.getTextTrim());
            this.damengConnect.setPassword(this.password.getText());
            this.damengConnect.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            // 保存数据
            if (this.connectStore.replace(this.damengConnect)) {
                DamengEventUtil.connectUpdated(this.damengConnect);
                MessageBox.okToast(I18nHelper.operationSuccess());
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // 连接ip处理
        this.hostIp.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为ip端口
            if (t1 != null && t1.contains(":")) {
                try {
                    this.hostIp.setText(t1.split(":")[0]);
                    this.hostPort.setValue(Integer.parseInt(t1.split(":")[1]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.sshTab, "ssh");
            } else {
                NodeGroupUtil.disable(this.sshTab, "ssh");
            }
        });
    }

    @Override
    public void onWindowShown( WindowEvent event) {
        super.onWindowShown(event);
        this.damengConnect = this.getProp("info");
        this.name.setText(this.damengConnect.getName());
        this.user.setText(this.damengConnect.getUser());
        this.hostIp.setText(this.damengConnect.hostIp());
        this.type.selectType(this.damengConnect.getType());
        this.remark.setText(this.damengConnect.getRemark());
        this.hostPort.setValue(this.damengConnect.hostPort());
        this.password.setText(this.damengConnect.getPassword());
        this.connectTimeOut.setValue(this.damengConnect.getConnectTimeOut());
        // ssh配置
        this.sshForward.setSelected(this.damengConnect.isSSHForward());
        DamengSSHConfig sshConfig = this.sshConfigStore.getByIid(this.damengConnect.getId());
        if (sshConfig != null) {
            this.sshHost.setText(sshConfig.getHost());
            this.sshUser.setText(sshConfig.getUser());
            this.sshPort.setValue(sshConfig.getPort());
            this.sshTimeout.setValue(sshConfig.getTimeout());
            this.sshPassword.setText(sshConfig.getPassword());
        }
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.connectUpdateTitle();
    }
}

