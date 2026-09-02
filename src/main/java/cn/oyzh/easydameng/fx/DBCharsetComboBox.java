//package cn.oyzh.easydameng.fx;
//
//import cn.oyzh.easydameng.dameng.ShellDamengClient;
//import cn.oyzh.fx.plus.controls.combo.FXComboBox;
//
///**
// * @author oyzh
// * @since 2024/1/26
// */
//public class DBCharsetComboBox extends FXComboBox<String> {
//
//    public void init(ShellDamengClient client) {
//        this.clearItems();
//        // 空数据
//        this.addItem("");
//        // 正常数据
//        for (String charset : client.charsets()) {
//            this.addItem(charset.toUpperCase());
//        }
//    }
//
//    @Override
//    public void select(String obj) {
//        if (obj != null) {
//            super.select(obj.toUpperCase());
//        }
//    }
//}
