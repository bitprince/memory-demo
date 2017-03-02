package cn.ffcs.demo;

import cn.ffcs.memory.JSONArrayHandler;
import cn.ffcs.memory.Memory;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2017/3/2.
 */
@WebServlet("/demo")
public class DemoServlet extends HttpServlet {
    public Memory memory = MemoryFactory.getInstance();
    public Logger logger = LoggerFactory.getLogger(this.getClass()
            .getSimpleName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 分页、IN语句、JSONArrayHandler、JSONObjectHandler的演示
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        sql.append("select * from product");

        // IN语句，挑选芒果和桃子
        memory.in(sql, params, "where", "name", Arrays.asList("Mango", "Peach"));

        // 分页，每页2条，取1页的数据
        int pageSize = 2;
        int pageNo = 1;
        memory.pager(sql, params, pageSize, pageNo);

        // 以JSONArray格式打印，挑选的水果
        JSONArray ja = memory.query(sql, new JSONArrayHandler(), params);
        System.out.println(ja);
        logger.info(ja.toJSONString());
        response.getWriter().print(ja);
    }
}
