package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="gongweifuhelv")
public class GongWeiFuHe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String assignmentid;// 任务书编号  同时根据此字段得出时间 字段名与数据库中的名字相同，则不用
    private String workmodel;//工作模块
    private String itemdescription;//项目描述
    private String zipingorchoucha;//根据此字段判断是自评还是抽查
    private String Review;//评审内容
    private String stationname;//工位名称
    private String applicableterms;//适用条款数
    private String meettheterms;//符合条款数
    private Double stationpercentage;//工位符合率
    private String auditquestions;//工位审核问题数
    private String pinshenquyu;//评审区域
    private String pinshenshijian;//评审时间
    private String yinshenrenyuan;//迎审人员
}
