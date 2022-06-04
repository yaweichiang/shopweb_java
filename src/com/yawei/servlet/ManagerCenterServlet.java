package com.yawei.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@WebServlet("/managercenter")
public class ManagerCenterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Cache-Control","no-store");
       if(req.getSession().getAttribute("managerid")!=null){
            RequestDispatcher page = req.getRequestDispatcher("views/ManagerCenter.html");
            page.forward(req,resp);
        }else{
            RequestDispatcher page = req.getRequestDispatcher("views/ManagerLoginPage.html");
            page.forward(req,resp);
        }

    }
}
