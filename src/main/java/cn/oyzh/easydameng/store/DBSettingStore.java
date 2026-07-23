//package cn.oyzh.easydameng.store;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.oyzh.common.json.JSONUtil;
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.easydameng.DamengConst;
//import cn.oyzh.easydameng.domain.DamengSetting;
//import cn.oyzh.store.json.ObjectFileStore;
//import lombok.NonNull;
//
//
///**
// * db设置储存
// *
// * @author oyzh
// * @since 2022/8/26
// */
//public class DBSettingStore extends ObjectFileStore<DamengSetting> {
//
//    /**
//     * 当前实例
//     */
//    public static final DBSettingStore INSTANCE = new DBSettingStore();
//
//    /**
//     * 当前设置
//     */
//    public static final DamengSetting SETTING = INSTANCE.load();
//
//    {
//        this.filePath(DamengConst.STORE_PATH + "db_setting.json");
//        JulLog.info("dbSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//    }
//
//    @Override
//    public synchronized DamengSetting load() {
//        DamengSetting setting = null;
//        try {
//            // 读取配置文件内容
//            String text = FileUtil.readString(this.storeFile(), this.charset());
//            if (StrUtil.isNotBlank(text)) {
//                // 将配置文件内容解析为dbSetting对象
//                setting = JSONUtil.toBean(text, DamengSetting.class);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        // 如果解析失败，则返回一个新的dbSetting对象
//        if (setting == null) {
//            setting = new DamengSetting();
//        }
//        return setting;
//    }
//
//    @Override
//    public boolean update( DamengSetting setting) {
//        return this.saveData(setting);
//    }
//}
