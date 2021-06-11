package com.luqi.dockertest;

import java.io.IOException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryJdHtml {
    public static void main(String[] args) throws IOException {
        String url = "https://search.jd.com/Search?keyword=java";
        // 解析网页 (Jsoup 返回的 Document 就是 浏览器Document 对象)
        Document parse = Jsoup.parse(new URL(url), 30000000);
        // 找到需要的div节点 用js知识获取节点内容
        Element jGoodsList = parse.getElementById("J_goodsList");
        System.out.println(jGoodsList.html());
        /* System.out.println(jGoodsList.html()); */
        // 获取所有的 li 元素
        Elements li = parse.getElementsByTag("li");
        // 获取元素中的内容
        for (Element el : li) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String name = el.getElementsByClass("p-name").eq(0).text();
            System.out.println(img);
            System.out.println(price);
            System.out.println(name);
        }
    }
}
