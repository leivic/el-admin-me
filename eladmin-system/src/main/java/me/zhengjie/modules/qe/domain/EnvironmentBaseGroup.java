package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/*环境模块班组采集的基础数据*/
@Table(name = "environment_base_group")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentBaseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String group1; //group是关键字
    private String zone;
    private String date;
    private String written_by;
    private String create_by;
    private double groupstability;
    private double grouprotation;
    private double externalaudit;
    private double bookkeepingmanagement;
    private double lossgroupstability;
    private double groupbusiness;
    private double x3;
    private double flowpath;
    private double consistency;
    private double x4;
    private double healthquthority;
    private double losshealthquthority;
    private double x5;
}
