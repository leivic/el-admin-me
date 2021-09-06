package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*环境模块工位基础采集数据*/

@Table(name = "environment_base_station")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentBaseStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String station;
    private String zone;
    private String date;
    private String written_by;
    private String create_by;
    private double peopleiscapable;
    private double matteriscorrect;
    private double wokerisstandard;
    private double wokerstability;
    private double stationshutdown;
    private double mattershutdown;
    private double x1;
    private double low_carbon_1;
    private double iso;
    private double x2;
}
