package com.yawei.api;

import com.yawei.bean.User;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
                out.print("not member");
            }else{//會員 回傳會員資料
                User member = new User(req.getSession().getAttribute("userid").toString());
                out.print(member);
            }
        }else{
            if(req.getSession().getAttribute("managerid")!=null) {//管理員
//            依照 subPath 電話回傳 指定會員資料 需要驗證是管理員
                User member = new User(subPath.substring(1));
                out.print(member);
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
        if(subPath.equals("/singup")) {
            String name = req.getParameter("user");
            String phone = req.getParameter("phone");
            String mail = req.getParameter("mail");
            String password = req.getParameter("password");
            if(!User.checkPhone(phone)) { // 電話沒有重複 !false 建立會員
                User member = new User(name, phone, mail, password);
                member.create();
                if(req.getSession(false) != null) { // null 表示 session 不存在
                    req.changeSessionId();//若以存在session 變更sessionID
                }
                req.getSession().setAttribute("userid",member.getId()); // 註冊完成 直接登入
            }
            resp.sendRedirect("/usercenter");

        }else if(subPath.equals("/login")){
            String phone = req.getParameter("user");
            String password =  req.getParameter("password");
            String inputCheckCode = req.getParameter("checkCode");


            if(inputCheckCode.equals(req.getSession().getAttribute("checkCode"))){
                User member = User.login(phone,password);
                if(member != null){
                    if (req.getSession(false) != null) { // null 表示 session 不存在
                        req.changeSessionId();//若以存在session 變更sessionID
                    }
                    req.getSession().setAttribute("userid", member.getId()); // 帳號密碼正確 設定userid Session
                }
            }
            resp.sendRedirect("/usercenter");
        }else if(subPath.equals("/update")){
            //會員資料變更
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            String result = "error";
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            if(req.getSession().getAttribute("userid")!=null){
                User member = new User(String.valueOf(obj.getInt("id")));
                if(req.getSession().getAttribute("userid").toString().equals(String.valueOf(obj.getInt("id")))){
                    result = "會員資料變更成功";
                    member.update(obj.getString("name"),obj.getString("nickname"),obj.getString("email"));
                    String oldPW = obj.getString("oldPW");
                    String newPW = obj.getString("newPW");
                    //判斷是否有要進行密碼變更 分別處理回應
                    if(oldPW.length()!=0){
                        //要變更密碼
                        result = member.changePassword(oldPW,newPW);
                    }
                }else{
                    result = "error";
                }
            }
            out.print(result);
        }
    }
}

