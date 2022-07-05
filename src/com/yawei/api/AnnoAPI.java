package com.yawei.api;

import com.yawei.bean.Anno;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/anno/*")
public class AnnoAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo();
        if(subPath == null){
            out.print(Anno.getAllAnno());
        }else if(subPath.equals("/new")){
            out.print(Anno.getNewAnno());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession().getAttribute("managerid")!=null) {
            String json = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            if (br != null) {
                json = br.readLine();
            }
            Anno anno = new Anno(json);
            anno.update();

        }
    }
}
