package com.yawei.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.JsonObject;
import javax.json.JsonArray;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


public interface DatabaseConnect {

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
//    確認會員是否有登記電話
    public boolean checkPhone(String id);
//    確認電話是否有被註冊
    public boolean checkPhoneExist(String id);




}
