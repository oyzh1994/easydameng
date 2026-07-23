package cn.oyzh.easydameng.store;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easydameng.domain.DamengSetting;
import cn.oyzh.store.jdbc.JdbcKeyValueStore;


/**
 * db设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
public class DamengSettingStore extends JdbcKeyValueStore<DamengSetting> {

    /**
     * 当前实例
     */
    public static final DamengSettingStore INSTANCE = new DamengSettingStore();

    /**
     * 当前设置
     */
    public static final DamengSetting SETTING = INSTANCE.load();

    public DamengSetting load() {
        DamengSetting setting = null;
        try {
            setting = super.select();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("load setting error", ex);
        }
        if (setting == null) {
            setting = new DamengSetting();
        }
        return setting;
    }

    public boolean replace(DamengSetting model) {
        if (model != null) {
            return this.update(model);
        }
        return false;
    }

    @Override
    protected Class<DamengSetting> modelClass() {
        return DamengSetting.class;
    }
}
