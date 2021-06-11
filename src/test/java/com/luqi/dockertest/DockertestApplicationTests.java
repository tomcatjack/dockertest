package com.luqi.dockertest;

import com.alibaba.fastjson.JSON;
import com.luqi.dockertest.dto.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DockertestApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    @Test
    void testCreateIndex() {
        // 1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("luqi_index");
        // 2、客户端执行请求 IndicesClient , 请求后获得响应
        try {
            CreateIndexResponse indexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            System.out.println(indexResponse.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询索引是否存在
     */
    @Test
    void getIndex() throws IOException {
        GetIndexRequest indexRequest = new GetIndexRequest("luqi_index");
        boolean exists = restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 删除索引
     */
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("luqi_index");
        AcknowledgedResponse delete = restHighLevelClient.indices()
            .delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 添加文档
     */
    @Test
    void createDoc() throws IOException {
        User user = new User("luqi",18);
        IndexRequest request = new IndexRequest("luqi_index");
        request.id("1");
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(index));
    }

    /**
     * 获取文档,判断文档是否存在
     */
    @Test
    void judgeDocument() throws IOException {
        GetRequest request = new GetRequest("luqi_index","1");
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 获取文档
     */
    @Test
    void getDocument() throws IOException {
        GetRequest request = new GetRequest("luqi_index","1");
        GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSource());  // 获取文档内容 返回 Map
        System.out.println(documentFields);  // 获取文档所有信息
    }

    /**
     * 更新文档信息
     */
    @Test
    void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("luqi_index","1");
        // 创建新对象
        User user = new User("luqi_update",18);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
        System.out.println(update.status());
    }

    /**
     * 删除文档
     */
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("luqi_index","1");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    /**
     * 批量插入数据
     */
    @Test
    void bulkInsert() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User("changanKing",22));
        arrayList.add(new User("changan",21));
        arrayList.add(new User("Changan",23));
        arrayList.add(new User("长安",22));
        arrayList.add(new User("长安King",22));
        arrayList.add(new User("King",22));
        for (int i = 0; i < arrayList.size() ; i++) {
            bulkRequest.add(
                new IndexRequest("luqi_index")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(arrayList.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());  // 如果是false则说明插入成功
    }

    /**
     * 查询数据
     */
    @Test
    void queryData() throws IOException {
        SearchRequest request = new SearchRequest("luqi_index");
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /*
         * 查询条件, 我们可以使用 QueryBuilders 工具类来实现
         * QueryBuilders.termQuery()  精确查询
         * QueryBuilders.matchAllQuery() 查询所有数据
         */
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "changan");
        searchSourceBuilder.query(termQueryBuilder);
        // 设置搜索时长
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(search.getHits()));
        System.out.println("===========");
        for (SearchHit searchHit: search.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsMap());
        }
    }
}
