package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*环境模块工段基础数据采集*/
@Table(name = "environment_base_workshop")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentBaseWorkShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String workshop;
    private String zone;
    private String date;
    private String written_by;
    private String create_by;
    private double workshopstability;
    private double substitute;
    private double externalaudit;
    private double bookkeepingmanagement;
    private double studyplan;
    private double lossworkshopstability;
    private double workshopbusiness;
    private double x6;
    private double workshopsection;
    private double programfiles;
    private double ecologicalquality;
    private double lossprogramfiles;
    private double lossecologicalquality;
    private double x7;
    private double flowpath;
    private double consistency;
    private double x8;
    private double dai;
    private double consistency2;
    private double x9;
    private double healthquthoritygroup;
    private double healthquthoritystation;
    private double losshealthquthoritygroup;
    private double losshealthquthoritystation;
    private double x10;
}
