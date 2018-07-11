package com.os.crossdomain;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CrossDomainMainServlet extends HttpServlet {

    private String resJs = null;
    private String resHtml = null;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            this.resJs = IOUtils.getResourceAsString("/main.js");
            this.resHtml = IOUtils.getResourceAsString("/main.html");
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/javascript");
        resp.setCharacterEncoding("UTF-8");


        String path = req.getContextPath();
        String basePath = req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + path + "/";

        Map<String, String> map = new HashMap<>();
        map.put("path", path);
        map.put("basePath", basePath);
        String content = IOUtils.replace(this.resJs, map);

        resp.getWriter().write(content);
        resp.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");


        String sub_name = req.getParameter("sub_name");
        String action = req.getParameter("action");
        String data = req.getParameter("data");
        String cb_action = req.getParameter("cb_action");


        Map<String, String> map = new HashMap<>();
        map.put("sub_name", sub_name);
        map.put("action", action);
        map.put("data", data);
        map.put("cb_action", cb_action);
        String content = IOUtils.replace(this.resHtml, map);

        resp.getWriter().write(content);
        resp.flushBuffer();
    }
}
