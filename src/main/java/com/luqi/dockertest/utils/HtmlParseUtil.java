package com.luqi.dockertest.utils;

import com.luqi.dockertest.dto.Content;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * @author changanKing
 * @date 2021/2/26 19:19
 */
@Component
public class HtmlParseUtil {

//
//    public static void main(String[] args)  {
//        try {
//            new HtmlParseUtil().parseJd("vue").forEach(System.out::println);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public List<Content> parseJd(String keywords) throws Exception {
        String url = "https://search.jd.com/Search?keyword=" + keywords;
        Document parse = Jsoup.parse(new URL(url), 30000);
        Element jGoodsList = parse.getElementById("J_goodsList");
        Elements li = parse.getElementsByTag("li");
        ArrayList<Content> list = new ArrayList<>();
        for (Element el : li) {
            if ("gl-item".equalsIgnoreCase(el.attr("class"))) {
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                String price = el.getElementsByClass("p-price").eq(0).text();
                String name = el.getElementsByClass("p-name").eq(0).text();
                Content content = new Content();
                content.setImg(img);
                content.setPrice(price);
                content.setName(name);
                list.add(content);
            }
        }
        return list;
    }

}
