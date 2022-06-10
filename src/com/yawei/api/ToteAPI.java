package com.yawei.api;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/tote")
public class ToteAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonArray result;
        result = MySqlConnect.getMySql().getTote();
        out.print(result);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession().getAttribute("managerid")!=null){
            String json="";
            BufferedReader br = new BufferedReader(new InputStreamReader( req.getInputStream()));
            if(br!=null){
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);

                MySqlConnect.getMySql().updateTote(obj);

        }
    }

}
