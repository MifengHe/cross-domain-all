package com.os.crossdomain;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 父页面 main.js
 */
public class CrossDomainSubServlet extends HttpServlet {

    private String resSubJs = null;
    private String resSubHtml = null;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            this.resSubJs = IOUtils.getResourceAsString("/sub.js");
            this.resSubHtml = IOUtils.getResourceAsString("/sub.html");
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getContextPath();
        String basePath = req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + path;

        String[] strings = req.getRequestURI().split("\\?");
        if (!strings[0].endsWith("cross-domain-sub.js")) {
            throw new ServletException(MessageFormat.format("please usage format \"{0}/**/cross-domain-sub.js\"", basePath));
        }

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/javascript");
        resp.setCharacterEncoding("UTF-8");


        Map<String, String> map = new HashMap<>();
        map.put("basePath", basePath);
        String content = IOUtils.replace(this.resSubJs, map);

        resp.getWriter().write(content);
        resp.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getContextPath();
        String basePath = req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + path;

        String[] strings = req.getRequestURI().split("\\?");
        if (!strings[0].endsWith("cross-domain-sub.html")) {
            throw new ServletException(MessageFormat.format("please usage formate \"{0}/**/cross-domain-sub.html?url=*\"", basePath));
        }


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
        String content = IOUtils.replace(this.resSubHtml, map);

        resp.getWriter().write(content);
        resp.flushBuffer();
    }
}
