package com.yawei.api;

import com.yawei.bean.Pay;
import com.yawei.util.MySqlConnect;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/pay")
public class PayTypeAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
//        JsonArray result;
//        result = MySqlConnect.getMySql().getPay();
//        out.print(result);
        out.print(Pay.all());
    }
}
