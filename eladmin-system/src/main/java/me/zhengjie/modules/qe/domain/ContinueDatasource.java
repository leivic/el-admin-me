package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Table(name = "continue_datasource")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true) //开启链式调用注解
public class ContinueDatasource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String zone;
    private String date;
    private double x1;
    private double x2;
    private double x3;
    private double x4;
    private double x5;
    private double x6;
    private double x7;
    private double x8;
    private double x9;
    private double x10;
    private double x11;
    private double x12;
}
