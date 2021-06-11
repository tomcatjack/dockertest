package com.luqi.dockertest.service;

import com.alibaba.fastjson.JSON;
import com.luqi.dockertest.dto.Content;
import com.luqi.dockertest.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author changanKing
 * @date 2021/2/26 20:42
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 1、解析数据放入 es
    public Boolean analyticalData(String key) throws Exception {
        List<Content> contents = new HtmlParseUtil().parseJd(key);

        // 判断索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest("jd_index");
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!exists) {
            // 索引不存在创建索引
            CreateIndexRequest request = new CreateIndexRequest("jd_index");
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices()
                .create(request, RequestOptions.DEFAULT);
            if (!createIndexResponse.isAcknowledged()) {
                return false;
            }
        }
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (Content content : contents) {
            bulkRequest.add(
                    new IndexRequest("jd_index")
                            .source(JSON.toJSONString(content), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    // 2、获取数据之后实现搜索功能
    public List<Map<String, Object>> searchPage(String key,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        // 精确匹配
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("name", key);
        searchSourceBuilder.query(termsQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        ArrayList<Map<String, Object>> objects = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            objects.add(hit.getSourceAsMap());
        }
        return objects;
    }

    // 3、获取数据之后实现高亮搜索功能
    public List<Map<String, Object>> searchHighlightedPage(String key,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        // 精确匹配
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("name", key);
        searchSourceBuilder.query(termsQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(true); // 多个高亮显示
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);


        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        ArrayList<Map<String, Object>> objects = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果
            // 解析高亮字段,将以前的字段替换成我们的新字段
            if (name!=null) {
                Text[] fragment = name.fragments();
                String n_ame = "";
                for (Text text : fragment) {
                    n_ame += text;
                }
                sourceAsMap.put("name",n_ame); //替换字段
            }
            objects.add(sourceAsMap);
        }
        return objects;
    }

}
