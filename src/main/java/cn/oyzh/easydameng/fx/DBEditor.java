//package cn.oyzh.easydameng.fx;
//
//import cn.oyzh.easydameng.db.DBDialect;
//import cn.oyzh.easydameng.domain.DamengSetting;
//import cn.oyzh.easydameng.store.DamengSettingStore;
//import cn.oyzh.fx.editor.incubator.Editor;
//import cn.oyzh.fx.editor.incubator.EditorFormatType;
//import cn.oyzh.fx.plus.font.FontManager;
//import javafx.scene.text.Font;
//
///**
// * db编辑器
// *
// * @author oyzh
// * @since 2025/10/29
// */
//public class DBEditor extends Editor {
//
//    @Override
//    public void initNode() {
//        super.initNode();
//        super.setFormatType(EditorFormatType.SQL);
//    }
//
//    /**
//     * 方言
//     */
//    private DBDialect dialect;
//
//    public DBDialect getDialect() {
//        return dialect;
//    }
//
//    public void setDialect(DBDialect dialect) {
//        this.dialect = dialect;
//    }
//
//    @Override
//    protected Font getEditorFont() {
//        DamengSetting setting = DamengSettingStore.SETTING;
//        return FontManager.toFont(setting.editorFontConfig());
//    }
//}
