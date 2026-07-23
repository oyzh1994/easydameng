//package cn.oyzh.easydameng.search;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.oyzh.easydameng.store.DBSearchHistoryStore;
//import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;
//
//import java.util.List;
//
///**
// * db搜索历史弹窗
// *
// * @author oyzh
// * @since 2024/07/23
// */
//public class DBSearchHistoryPopup extends SearchHistoryPopup {
//
//    /**
//     * 搜索历史储存
//     */
//    private final DBSearchHistoryStore historyStore = DBSearchHistoryStore.INSTANCE;
//
//    @Override
//    public List<String> getHistories() {
//        List<String> list = this.historyStore.getSearchKw();
//        if (CollUtil.isNotEmpty(list)) {
//            return list.reversed();
//        }
//        return list;
//    }
//}
