package com.yawei.api;

import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/search/*")
public class SearchAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if(req.getSession().getAttribute("managerid")!=null){
            if(!req.getPathInfo().equals("/")) {
                String subPath = req.getPathInfo();
                //查詢會員名單
                //假資料
                out.print(new JSONArray("[{\"no\":8,\"name\":\"fff\",\"phone\":\"0936485932\",\"email\":\"ddd@dddd\",url:\"https://lh3.googleusercontent.com/a-/AOh14Gj_FmeO8F1BsZEGbkQD-Rovv1rWcBkzM_KBHzCg=s96-c\"},{\"no\":2,\"name\":\"dddf\",\"phone\":\"0939985932\",\"email\":\"rewd@dddd\",url:\"https://lh3.googleusercontent.com/a-/AOh14Gj_FmeO8F1BsZEGbkQD-Rovv1rWcBkzM_KBHzCg=s96-c\"}]"));
            }else{
                out.print("null");
            }
        }else{
            out.print("error");
        }



    }
}
