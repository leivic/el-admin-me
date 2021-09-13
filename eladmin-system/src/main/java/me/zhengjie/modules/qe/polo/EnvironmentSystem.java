package me.zhengjie.modules.qe.polo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*质量生态环境系统完整取数polo，每一条数据都是如下格式，每一条数据代表一个月*/


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnvironmentSystem {
    private String date;
    private double total;//总体
    private double chognya;
    private double cheshen;
    private double tuzhuang;
    private double zongzhuang;
    private double jijia;
    private double zhuangpei;
}
