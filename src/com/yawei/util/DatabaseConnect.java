package com.yawei.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.JsonObject;
import javax.json.JsonArray;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


public interface DatabaseConnect {


//    取得所有商品資料
    public abstract JsonArray getAllProducts() throws SQLException;
//    取得指定商品資料
    public abstract JsonArray getProduct(String id);
//    取得下筆商品編號
    public abstract  int getNewProductNo();
//    新增商品
    public abstract JsonArray createProduct(JSONObject data,String path);
//    變更商品資訊
    public abstract JsonArray updateProduct(JSONObject data);
//    取得會員資訊
    public abstract JsonArray getUserInfo(String id);
//    查詢會員
    public abstract JsonArray searchUsers(String keyword);
//    註冊 新建會員
    public abstract void createUser(HashMap<String,String> user);
//    變更會員資料
    public abstract void updateUser(JSONObject data);
//    變更會員密碼
    public abstract void updateUserHashPW(String pa, int  userId);
//    會員登入帳號密碼檢查
    public abstract String getMemberHashPW(String memberPhone);
//    管理者登入帳號密碼檢查
    public abstract String getManagerHashPW(String managerid);
//    管理者登入帳號密碼變更
    public abstract void updateManagerHashPW(String pa , String managerId);
//    通過mail查詢會員id
    public abstract int checkIdByMail(String phone);
//    通過電話查詢會員id
    public int checkIdByPhone(String phone);
//    取得所有商品容量資訊
    public abstract JsonArray getCapacity();
//    取得所有運費資訊
    public abstract JsonArray getTote();
//    變更運費資訊
    public abstract void updateTote(JSONObject object);
//    取得所有付款資訊
    public abstract JsonArray getPay();
//    取得最新公告
    public abstract JsonArray getNewAnno();
//    取得所有公告
    public abstract JsonArray getAllAnno();
//    變更公告
    public abstract void updateAnno(JSONObject object);
//    取得使用者常用地址資訊
    public abstract JsonArray getUserAddress(String id);
//    新增常用地址
    public abstract void createAddress(JSONObject object);
//    刪除常用地址
    public abstract void deleteAddress(JSONObject object);
//    取得下筆訂單編號
    public abstract  int getNewOrderListNo();
//    新增訂單  改寫至 OrderList 類別中
//    public abstract  void createOrderList(JSONObject object,String userid);
//    public abstract  void createOrderList(OrderList object);

//    取得指定會員訂單資料  會員專用
    public abstract JSONArray getOrderListByMemberId(String id);
//    取得指定會員訂單資料  管理者專用
    public abstract  JSONArray getOrderListByMemberIdforManager(String id);
//    取得指定日期訂單資料
    public abstract  JSONArray getOrderListByDate(String date);
//    取得指定天數內的訂單資料
    public abstract  JSONArray getOrderListByDays(String days);
//    取得指定編號訂單資料  會員專用
    public abstract  JSONArray getOrderListByNo(String no,String id);
//    取得指定編號訂單資料  管理者專用
    public abstract  JSONArray getOrderListByNoForManager(String no);
//    取消訂單  會員專用
    public abstract  void cancelOrder(String no,String id);
//    更新訂單資訊  管理者專用
    public abstract void updateOrder(JSONObject object);
//    確認會員是否有登記電話
    public boolean checkPhone(String id);
//    確認電話是否有被註冊
    public boolean checkPhoneExist(String id);




}
