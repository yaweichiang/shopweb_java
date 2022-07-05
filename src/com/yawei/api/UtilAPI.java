package com.yawei.api;


import com.yawei.bean.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

@WebServlet("/check/*")
public class UtilAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo();
        String idString = "^/09[0-9]{8}$";
        if(subPath == null){
            //檢查會員登入狀況 登入 且已有登記電話 回傳true  //登入未登記電話回傳nophone //未登入 回傳false

            if(req.getSession().getAttribute("userid")!=null){
                String id = req.getSession().getAttribute("userid").toString();
                User user = new User(id);
                if(user.getPhone()!=null){
                    out.print("true");
                }else{
                    out.print("nophone");
                }
            }else{
                out.print("false");
            }
        }else if(Pattern.matches(idString, subPath)){
            //  檢查使用者欲註冊電話是否已註冊過
            //不存在 false  存在true
            String result = User.checkPhone(subPath.substring(1))?"Exist":"NotExist";
            out.print(result);
        }
    }

}
