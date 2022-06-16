package com.yawei.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@WebServlet("/singup")
public class SingupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
//        resp.setHeader("Cache-Control","no-store");
        if(req.getSession().getAttribute("userid")!=null){
            resp.sendRedirect("/usercenter");
        }else{
            RequestDispatcher page = req.getRequestDispatcher("views/SingupPage.html");
            page.forward(req,resp);
        }

    }
}
