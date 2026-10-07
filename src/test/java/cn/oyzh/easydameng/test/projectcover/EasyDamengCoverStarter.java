package cn.oyzh.easydameng.test.projectcover;

import cn.oyzh.fx.plus.util.FXCoverChecker;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URISyntaxException;

/**
 * 达梦数据库模块界面覆盖率检查的启动程序
 *
 * @author oyzh
 * @since 2026-09-06
 */
public class EasyDamengCoverStarter extends Application {

    public static void main(String[] args) throws URISyntaxException {
        launch(EasyDamengCoverStarter.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.setPrimaryStage(primaryStage);
        FXCoverChecker coverChecker = new FXCoverChecker();
        coverChecker.setProjectPath(EasyDamengCoverStarter.class.getResource("").toExternalForm());
        coverChecker.tabCheck();
        coverChecker.viewCheck();
        coverChecker.popupCheck();
        StageManager.exit();
    }
}
