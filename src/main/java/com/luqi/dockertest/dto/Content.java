package com.luqi.dockertest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author changanKing
 * @date 2021/2/26 19:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {

    private String img;
    private String price;
    private String name;

}
