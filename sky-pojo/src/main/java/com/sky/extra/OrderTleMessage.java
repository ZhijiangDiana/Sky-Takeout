package com.sky.extra;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderTleMessage implements Serializable {
    public static final Integer PAY_ORDER_TLE = 0;
    public static final Integer FINISH_ORDER_TLE = 1;

    public static final String PAY_TIME_MS = "900000";
    public static final String DELIVERY_FINISH_TIME_MS = "3600000";

    private Integer tleType;
    private Long orderId;
}
