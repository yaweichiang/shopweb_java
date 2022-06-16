package com.yawei.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/managers/*")
public class ManagerAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String subPath = req.getPathInfo();
//        PrintWriter out = resp.getWriter();
//        out.print(subPath);
        if(subPath == null){
            resp.sendError(404,"此頁面不存在");
        }else if(subPath.equals("/login")){
//            確認管理者登入狀況
            String userid = req.getParameter("user");
            String password = req.getParameter("password");
            if(userid.equals("admin")&&password.equals("a123")){ //這邊要改成 判斷是否登入的方法
                if(req.getSession(false) != null) { // null 表示 session 不存在
                    req.changeSessionId();//若以存在session 變更sessionID
                }
                req.getSession().setAttribute("managerid",userid); // 帳號密碼正確 設定userid Session
            }
            resp.sendRedirect("/managercenter"); //將頁面導入會員中心
        }else if(subPath.equals("/new")){
            //新增管理人員
            resp.sendError(404,"此頁面不存在");
        }
    }
}

