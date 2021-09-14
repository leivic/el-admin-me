package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnvironmentBaseStationRepository extends JpaRepository<EnvironmentBaseStation,Integer> {


    @Query(value = "select * from environment_base_station  where " +"zone = :zone",nativeQuery = true) //nativeQuery代表开启原生sql写法 ：date是传入下方名为date的参数
    Page<EnvironmentBaseStation> findAllByZone(@Param("zone") String zone,Pageable pageable);//按部门查找所有符合的工位清单

    @Query(value = "select * from environment_base_station  where " +"zone = :zone and"+" date = :date",nativeQuery = true)
    List<EnvironmentBaseStation> findAllByZoneAndYear(@Param("zone") String zone, @Param("date") String date);

}
