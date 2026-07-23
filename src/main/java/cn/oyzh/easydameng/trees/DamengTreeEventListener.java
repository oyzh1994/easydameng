// package cn.oyzh.easydameng.trees;
//
// import cn.oyzh.common.util.StringUtil;
// import cn.oyzh.easydameng.event.database.DamengDatabaseAddedEvent;
// import cn.oyzh.easydameng.event.event.DamengEventAddedEvent;
// import cn.oyzh.easydameng.event.event.DamengEventAlertedEvent;
// import cn.oyzh.easydameng.event.function.DamengFunctionAddedEvent;
// import cn.oyzh.easydameng.event.function.DamengFunctionAlertedEvent;
// import cn.oyzh.easydameng.event.procedure.DamengProcedureAddedEvent;
// import cn.oyzh.easydameng.event.procedure.DamengProcedureAlertedEvent;
// import cn.oyzh.easydameng.event.query.DamengQueryAddedEvent;
// import cn.oyzh.easydameng.event.view.DamengViewAddedEvent;
// import cn.oyzh.easydameng.event.view.DamengViewAlertedEvent;
// import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
// import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
// import cn.oyzh.easydameng.trees.event.DamengEventTreeItem;
// import cn.oyzh.easydameng.trees.function.DamengFunctionTreeItem;
// import cn.oyzh.easydameng.trees.procedure.DamengProcedureTreeItem;
// import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
// import cn.oyzh.event.EventListener;
// import cn.oyzh.event.EventSubscribe;
//
// /**
//  * @author oyzh
//  * @since 2024-09-12
//  */
// public class DamengTreeEventListener implements EventListener {
//
// //    @PostConstruct
// //    private void init(){
// //        EventListener.super.register();
// //    }
// //
// //    @PreDestroy
// //    private void destroy(){
// //        EventListener.super.unregister();
// //    }
//
//     /**
//      * 查询新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onQueryAdded(DamengQueryAddedEvent event) {
//         event.getDbItem().getQueryTypeChild().addChild(event.data());
//     }
//
//     /**
//      * 视图新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void viewAdded(DamengViewAddedEvent event) {
//         DamengDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getViewTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 视图变更事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void viewAlerted(DamengViewAlertedEvent event) {
//         String viewName = event.data();
//         for (DamengViewTreeItem viewItem : event.getDbItem().getViewChild()) {
//             if (StringUtil.equalsIgnoreCase(viewName, viewItem.viewName())) {
//                 viewItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//
//     // /**
//     //  * 表添加事件
//     //  *
//     //  * @param event 事件
//     //  */
//     // @EventSubscribe
//     // private void onTableAdded(DamengTableAddedEvent event) {
//     //     DamengDatabaseTreeItem dbItem = event.data();
//     //     if (dbItem != null) {
//     //         dbItem.getTableTypeChild().reloadChild();
//     //     }
//     // }
//
//     // /**
//     //  * 表修改事件
//     //  *
//     //  * @param event 事件
//     //  */
//     // @EventSubscribe
//     // private void onTableAlerted(DamengTableAlertedEvent event) {
//     //     String tableName = event.data();
//     //     for (DamengTableTreeItem tableItem : event.getDbItem().getTableChild()) {
//     //         if (StringUtil.equalsIgnoreCase(tableName, tableItem.tableName())) {
//     //             tableItem.reloadChild();
//     //             break;
//     //         }
//     //     }
//     // }
//
//     /**
//      * 过程添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onProcedureAdded(DamengProcedureAddedEvent event) {
//         DamengDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getProcedureTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 过程修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onProcedureAlerted(DamengProcedureAlertedEvent event) {
//         String procedureName = event.data();
//         for (DamengProcedureTreeItem procedureItem : event.getDbItem().getProcedureChild()) {
//             if (StringUtil.equalsIgnoreCase(procedureName, procedureItem.procedureName())) {
//                 procedureItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 函数添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onFunctionAdded(DamengFunctionAddedEvent event) {
//         DamengDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getFunctionTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 函数修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onFunctionAlerted(DamengFunctionAlertedEvent event) {
//         String functionName = event.data();
//         for (DamengFunctionTreeItem functionItem : event.getDbItem().getFunctionChild()) {
//             if (StringUtil.equalsIgnoreCase(functionName, functionItem.functionName())) {
//                 functionItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 事件添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onEventAdded(DamengEventAddedEvent event) {
//         DamengDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getEventTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 事件修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onEventAlerted(DamengEventAlertedEvent event) {
//         String functionName = event.data();
//         for (DamengEventTreeItem eventTreeItem : event.getDbItem().getEventChild()) {
//             if (StringUtil.equalsIgnoreCase(functionName, eventTreeItem.eventName())) {
//                 eventTreeItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 数据库新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onDatabaseAdded(DamengDatabaseAddedEvent event) {
//         DBConnectTreeItem connectItem = event.getConnectItem();
//         if (connectItem != null) {
//             connectItem.reloadChild();
//         }
//     }
//
// }
