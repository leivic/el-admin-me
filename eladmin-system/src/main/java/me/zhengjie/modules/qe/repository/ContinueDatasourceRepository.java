package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.ContinueDatasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
public interface ContinueDatasourceRepository extends JpaRepository<ContinueDatasource,Integer> {

    @Query(value = "select * from continue_datasource  where " +" zone = :zone "+"and"+" date = :date",nativeQuery = true)
    ContinueDatasource findByDateAndZone(@Param("zone") String zone,@Param("date") String date);//按月份和日期查找这一条数据

    @Transactional
    @Modifying //jpa的更新/删除操作必须要有这个注解 且写法和select不同
    @Query(value = "update ContinueDatasource c set c.x1=?3,c.x2=?4,c.x3=?5,c.x4=?6,c.x5=?7,c.x6=?8,c.x7=?9,c.x8=?10,c.x9=?11,c.x10=?12,c.x11=?13,c.x12=?14 where c.zone=?1 and c.date=?2")
    void updateContinueDatasource( String zone,String date,double x1,double x2,double x3,double x4, double x5,double x6,double x7,double x8, double x9, double x10, double x11,double x12);//按输入的内容更新所有数据

    @Query(value = "select count(*) from continue_datasource  where " +" zone = :zone"+" and "+"date = :date",nativeQuery = true)
    int findCountByDateAndZone(@Param("zone") String zone,@Param("date") String date);//查找这个月份和日期是否已经存在数据

    @Transactional
    @Modifying //更新x1 x2 x3 更新这个月的所有X1 x2 x3
    @Query(value = "update ContinueDatasource c set c.x1=?2,c.x2=?3,c.x3=?4 where c.date=?1")
    void updateContinueDatasourcex1x3( String date,double x1,double x2,double x3);

    @Query(value = "select count(*) from continue_datasource  where " +" date = :date",nativeQuery = true)
    int findCountByDate(@Param("date") String date);//按每个月查找是否有这条数据  数据的数量是多少


}