package com.luqi.dockertest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * cainiao.bms.order.consign.confirm( BMS出库通知 ) request
 *
 * @author xw
 * @since 2021/5/31
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "content")
@Data
public class BmsOrderConfirmRequest {


    private List<TmsOrders> tmsOrders;



    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tmsOrders")
    @Data
    public static class TmsOrders {

        private String packageCode;
        private Long packageHeight;
        private Long packageLength;
        private Long packageWeight;
        private Long packageWidth;
        private String tmsCode;

        private String tmsOrderCode;
    }

}
