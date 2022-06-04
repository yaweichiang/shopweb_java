package com.yawei.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/user/*")
public class UserAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subPath = req.getPathInfo();
        String jsonResult = "{path:"+subPath+",data:'mydata',}";
        if(subPath == null){
//            依照session回傳 對應會員個資 需要驗證是會員
            //回傳格式需要是json檔案

        }else{
//            依照 subPath 電話回傳 指定會員資料 需要驗證是管理員
            //回傳格式需要是json檔案
        }
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out  =  resp.getWriter();
        out.write(jsonResult);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html;charset=UTF-8");
        String subPath = req.getPathInfo();
        String jsonResult = "{path:"+subPath+",data:'mydata',}";
        if(subPath == null){
//            註冊會員

        }else if(subPath.equals("/login")){
//            確認會員登入狀況
            String userid = req.getParameter("user");
            String password = req.getParameter("password");
            if(userid.equals("0937513541")&&password.equals("a123456")){ //這邊要改成 判斷是否登入的方法
                if(req.getSession(false) != null) { // null 表示 session 不存在
                    req.changeSessionId();//若以存在session 變更sessionID
                }
                req.getSession().setAttribute("userid",userid); // 帳號密碼正確 設定userid Session
            }
            resp.sendRedirect("/usercenter"); //將頁面導入會員中心
        }
    }
}

