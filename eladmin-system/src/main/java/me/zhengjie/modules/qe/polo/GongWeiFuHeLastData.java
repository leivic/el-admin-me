package me.zhengjie.modules.qe.polo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GongWeiFuHeLastData { //传到前端Echarts需要的数据
    private String stationName;//工位名称
    private Double stationPercentage;//工位符合率
}
