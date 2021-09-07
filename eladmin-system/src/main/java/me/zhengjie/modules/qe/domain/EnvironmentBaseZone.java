package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*环境模块区域基础数据采集*/
@Table(name = "environment_base_zone")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentBaseZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String zone;
    private String date;
    private String written_by;
    private String create_by;
    private double graft;
    private double bookkeepingmanagement;
    private double studyplan;
    private double externalaudit;
    private double substitute;
    private double losszonestability;
    private double x11;
    private double zonesection;
    private double consistency;
    private double programfiles;
    private double ecologicalquality;
    private double lossprogramfiles;
    private double lossecologicalquality;
    private double x12;
    private double dai;
    private double consistency2;
    private double x13;
    private double healthquthorityworkshop;
    private double healthquthoritygroup;
    private double losshealthquthorityworkshop;
    private double losshealthquthoritygroup;
    private double x14;
}
