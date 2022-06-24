package com.yawei.api;

import com.yawei.util.HashPassWord;
import com.yawei.util.MySqlConnect;

import javax.json.JsonArray;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import  org.json.JSONObject;



@WebServlet("/user/*")
public class UserAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String subPath = req.getPathInfo();
        JsonArray result;
        PrintWriter out  =  resp.getWriter();
        if(subPath == null){
//            依照session回傳 對應會員個資 需要驗證是會員
            if(req.getSession().getAttribute("userid")==null){//非會員
                out.print("no member");
            }else{//會員 回傳會員資料
                result = MySqlConnect.getMySql().getUserInfo(req.getSession().getAttribute("userid").toString());
                out.print(result);
            }
        }else{
            if(req.getSession().getAttribute("managerid")!=null) {//管理員
//            依照 subPath 電話回傳 指定會員資料 需要驗證是管理員
                result = MySqlConnect.getMySql().getUserInfo(subPath.substring(1));
                out.print(result);
                //回傳格式需要是json檔案
            }else {
                out.print("no manager");
            }
        }
    }

// google 登入api串接
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//                BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
//        String json = "";
//        if (br != null) {
//            json = br.readLine();
//        }
//        JSONObject obj = new JSONObject(json);
//        System.out.println("取得物件"+obj);
//
//        int id = MySqlConnect.getMySql().checkIdByMail(obj.getString("email"));
//        System.out.println("查詢是否存在此會員id:"+id);
//        if(id==0){//帳號不存在 建立帳號 註冊
//            System.out.println("建立新帳號");
//            MySqlConnect.getMySql().createUser(obj);
//            id =  MySqlConnect.getMySql().checkIdByMail(obj.getString("email"));
//        }
//        System.out.println("id:"+id);
//        if(req.getSession(false) != null) { // null 表示 session 不存在
//            req.changeSessionId();//若以存在session 變更sessionID
//        }
//        req.getSession().setAttribute("userid",id); // 帳號密碼正確 設定userid Session
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String subPath = req.getPathInfo();
        System.out.println(subPath);
        if(subPath.equals("/singup")) {
            HashMap<String, String> user = new HashMap<>();
            user.put("name", req.getParameter("user"));
            user.put("nickname", req.getParameter("user"));
            user.put("phone", req.getParameter("phone"));
            //  將密碼進行雜湊處理 後存入資料庫
            user.put("hashPW", HashPassWord.getHash(req.getParameter("password")));
            user.put("mail", req.getParameter("mail"));
            System.out.println("註冊"+user);
            MySqlConnect.getMySql().createUser(user);

            int id = MySqlConnect.getMySql().checkIdByPhone(user.get("phone")); //取得會員id
            System.out.println("id:"+id);
            if(req.getSession(false) != null) { // null 表示 session 不存在
                req.changeSessionId();//若以存在session 變更sessionID
            }
            req.getSession().setAttribute("userid",id); // 註冊完成 直接登入
            resp.sendRedirect("/usercenter");

        }else if(subPath.equals("/login")){
            String phone = req.getParameter("user");
            String password =  req.getParameter("password");
            // 對使用者密碼輸入密碼進行雜湊
            String hashed = HashPassWord.getHash(password);
            // 取得資料庫儲存的雜湊馬進行比對
            String saveHashPW = MySqlConnect.getMySql().getMemberHashPW(phone);
            System.out.println("使用者登入"+phone+"="+password);
            if(saveHashPW!=null) {
                if (saveHashPW.equals(hashed)) {
                    System.out.println("密碼正確");
                    if (req.getSession(false) != null) { // null 表示 session 不存在
                        req.changeSessionId();//若以存在session 變更sessionID
                    }
                    int userid = MySqlConnect.getMySql().checkIdByPhone(phone);
                    req.getSession().setAttribute("userid", userid); // 帳號密碼正確 設定userid Session
                }else{
                    System.out.println("密碼錯誤");
                }
            }else{
                System.out.println("帳號錯誤");
            }
            resp.sendRedirect("/usercenter");
        }else if(subPath.equals("/update")){
            //會員資料變更
//            resp.setContentType("application/json;charset=UTF-8");
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            String result = "error";
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            System.out.println("取得更新物件"+obj);
            System.out.println( req.getSession().getAttribute("userid")!=null);
            if(req.getSession().getAttribute("userid")!=null){
                System.out.println(req.getSession().getAttribute("userid")+":"+obj.getInt("id"));
                if(req.getSession().getAttribute("userid").toString().equals(String.valueOf(obj.getInt("id")))){
                    result = "會員資料變更成功";
                    System.out.println(result);
                    MySqlConnect.getMySql().updateUser(obj); //變更姓名 暱稱 mail
                    String oldPW = obj.getString("oldPW");
                    //判斷是否有要進行密碼變更 分別處理回應
                    if(oldPW.length()!=0){
                        //要變更密碼
                        String savedPW = MySqlConnect.getMySql().getMemberHashPW(obj.getString("phone"));
                        if(HashPassWord.getHash(oldPW).equals(savedPW)){
                            MySqlConnect.getMySql().updateUserHashPW(HashPassWord.getHash(obj.getString("newPW")),obj.getInt("id"));
                            result = "會員資料及密碼變更成功";
                            System.out.println(result);
                        }else{
                            result = "會員資料更新成功;舊密碼錯誤，密碼變更失敗！";
                            System.out.println(result);
                        }
                    }

                }else{
                    result = "error";
                    System.out.println(result);
                }

            }
            out.print(result);
        }
    }

//    @Override
//    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("application/json;charset=UTF-8");
//        PrintWriter out = resp.getWriter();
//        JsonArray result;
//        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
//        String json = "";
//        if (br != null) {
//            json = br.readLine();
//        }
//        JSONObject obj = new JSONObject(json);
//        System.out.println("put取得物件"+obj);
//        System.out.println( req.getSession().getAttribute("userid")!=null);
//        if(req.getSession().getAttribute("userid")!=null){
//            MySqlConnect.getMySql().updateUser(obj);
//        }
//    }
}

