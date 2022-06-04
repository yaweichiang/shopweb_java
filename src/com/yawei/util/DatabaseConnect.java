package com.yawei.util;

import com.mysql.cj.x.protobuf.MysqlxCrud;

import javax.json.JsonObject;
import java.sql.Connection;
import java.util.ArrayList;


public interface DatabaseConnect {
//    取得資料庫連線
    public abstract Connection getConnection();
//    取得所有商品資料
    public abstract ArrayList<JsonObject> getAllProducts();
//    取得指定商品資料
    public abstract ArrayList<JsonObject> getProduct(String id);
//    新增商品
    public abstract ArrayList<JsonObject> createProduct(JsonObject object);
//    變更商品資訊
    public abstract ArrayList<JsonObject> updateProduct(JsonObject object);
//    取得會員資訊
    public abstract ArrayList<JsonObject> getUserInfo(String userPhone);
//    註冊 新建會員
    public abstract void createUser(JsonObject object);
//    變更會員資料
    public abstract ArrayList<JsonObject> updateUser(JsonObject object);
//    變更會員密碼
    public abstract ArrayList<JsonObject> updateUserPW(JsonObject object);
//    會員登入帳號密碼檢查
    public abstract boolean checkUserLogin(JsonObject object);
//    管理者登入帳號密碼檢查
    public abstract Boolean checkManagerLogin(JsonObject object);
//    檢查電話是否已註冊過
    public abstract boolean checkPhone(String phone);
//    取得所有商品容量資訊
    public abstract ArrayList<JsonObject> getCapacity();
//    取得所有運費資訊
    public abstract ArrayList<JsonObject> getTote();
//    變更運費資訊
    public abstract ArrayList<JsonObject> updateTote(JsonObject object);
//    取得所有付款資訊
    public abstract ArrayList<JsonObject> getPay();
//    取得最新公告
    public abstract ArrayList<JsonObject> getNewAnno();
//    取得所有公告
    public abstract ArrayList<JsonObject> getAllAnno();
//    變更公告
    public abstract void updateAnno();
//    取得使用者常用地址資訊
    public abstract ArrayList<JsonObject> getUserAddress(String phone);
//    新增常用地址
    public abstract ArrayList<JsonObject> createAddress(JsonObject object);
//    刪除常用地址
    public abstract ArrayList<JsonObject> deleteAddress(JsonObject object);
//    取得下筆訂單編號
    public abstract  ArrayList<JsonObject> getNewOrderListNo();
//    新增訂單
    public abstract  void createOrderList(JsonObject object);
//    取得指定會員訂單資料  會員專用
    public abstract  ArrayList<JsonObject> getOrderListByPhone(String phone);
//    取得指定會員訂單資料  管理者專用
    public abstract  ArrayList<JsonObject> getOrderListByPhoneforManager(String phone);
//    取得指定日期訂單資料
    public abstract  ArrayList<JsonObject> getOrderLsitByDate(String date);
//    取得指定天數內的訂單資料
    public abstract  ArrayList<JsonObject> getOrderListByDays(String days);
//    取得指定編號訂單資料  會員專用
    public abstract  ArrayList<JsonObject> getOrderListByNo(String no);
//    取得指定編號訂單資料  管理者專用
    public abstract  ArrayList<JsonObject> getOrderListByNoforManager(String no);
//    取消訂單  會員專用
    public abstract  ArrayList<JsonObject> cancelOrder(JsonObject object);
//    更新訂單資訊  管理者專用
    public abstract ArrayList<JsonObject> updateOrder(JsonObject object);






}
