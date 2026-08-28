package cn.oyzh.easydameng.fx;

import cn.oyzh.easydameng.domain.DamengSetting;
import cn.oyzh.easydameng.store.DamengSettingStore;
import cn.oyzh.fx.editor.incubator.Editor;
import cn.oyzh.fx.plus.font.FontManager;
import javafx.scene.text.Font;

/**
 * @author oyzh
 * @since 2025-03-26
 */
public class ShellDataEditor extends Editor {

    @Override
    protected Font getEditorFont() {
        DamengSetting setting = DamengSettingStore.SETTING;
        return FontManager.toFont(setting.editorFontConfig());
    }

}
