package com.luqi.dockertest;

import com.luqi.dockertest.service.ContentService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author changanKing
 * @date 2021/2/26 20:42
 */
@RestController
public class ContentController {

    @Autowired
    ContentService contentService;

    @GetMapping("/parse/{key}")
    public Boolean parse(@PathVariable String key) throws Exception {
        return contentService.analyticalData(key);
    }

    @GetMapping("/search/{key}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable String key,
        @PathVariable int pageNo,
        @PathVariable int pageSize) throws IOException {
        return contentService.searchHighlightedPage(key, pageNo, pageSize);
    }


}
