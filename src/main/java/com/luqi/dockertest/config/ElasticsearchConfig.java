package com.luqi.dockertest.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
/** 
 * ElasticsearchConfig
 *
 * @author xw
 * @since 2021/6/4
 */ 
@Configurable
public class ElasticsearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("127.0.0.1", 9200, "http")));
    }
}