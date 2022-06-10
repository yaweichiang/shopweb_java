package com.yawei.api;

import com.yawei.util.MySqlConnect;

import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String json = "";
        if (br != null) {
            json = br.readLine();
        }
        JSONObject obj = new JSONObject(json);
        System.out.println("取得物件"+obj);

        int id = MySqlConnect.getMySql().checkMail(obj.getString("email"));
        System.out.println("查詢是否存在此會員id:"+id);
        if(id==0){//帳號不存在 建立帳號
            System.out.println("建立新帳號");
            MySqlConnect.getMySql().createUser(obj);
            id =  MySqlConnect.getMySql().checkMail(obj.getString("email"));
        }
        System.out.println("id:"+id);
        if(req.getSession(false) != null) { // null 表示 session 不存在
            req.changeSessionId();//若以存在session 變更sessionID
        }
        req.getSession().setAttribute("userid",id); // 帳號密碼正確 設定userid Session
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonArray result;
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String json = "";
        if (br != null) {
            json = br.readLine();
        }
        JSONObject obj = new JSONObject(json);
        System.out.println("put取得物件"+obj);
        System.out.println( req.getSession().getAttribute("userid")!=null);
        if(req.getSession().getAttribute("userid")!=null){
            MySqlConnect.getMySql().updateUser(obj);
        }
    }
}

