package com.yawei.api;

import com.yawei.util.HashPassWord;
import com.yawei.util.MySqlConnect;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


@WebServlet("/managers/*")
public class ManagerAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String subPath = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        if(subPath == null){
            resp.sendError(404,"此頁面不存在");
        }else if(subPath.equals("/login")){
//            確認管理者登入狀況
            String managerId = req.getParameter("user");
            String password = req.getParameter("password");
            // 對使用者密碼輸入密碼進行雜湊
            String hashed = HashPassWord.getHash(password);
            // 取得資料庫儲存的雜湊馬進行比對
            String saveHashPW = MySqlConnect.getMySql().getManagerHashPW(managerId);
            if(saveHashPW.equals(hashed)){
                if(req.getSession(false) != null) { // null 表示 session 不存在
                    req.changeSessionId();//若以存在session 變更sessionID
                }
                req.getSession().setAttribute("managerid",managerId); // 帳號密碼正確 設定userid Session
            }
            resp.sendRedirect("/managercenter"); //將頁面導入會員中心


        }else if(subPath.equals("/new")){
            //新增管理人員
//            MySqlConnect.getMySql().updateManagerHashPW(result1,managerId); //變更密碼
            resp.sendError(404,"此頁面不存在");

            //將輸入的密碼 雜湊後存入資料庫
        }
    }
}

