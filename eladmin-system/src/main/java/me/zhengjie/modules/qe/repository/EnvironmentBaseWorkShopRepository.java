package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import me.zhengjie.modules.qe.domain.EnvironmentBaseWorkShop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EnvironmentBaseWorkShopRepository extends JpaRepository<EnvironmentBaseWorkShop,Integer> {

    @Query(value = "select * from environment_base_workshop  where " +"zone = :zone",nativeQuery = true) //nativeQuery代表开启原生sql写法 ：date是传入下方名为date的参数
    Page<EnvironmentBaseWorkShop> findAllByZone(@Param("zone") String zone, Pageable pageable);

    @Query(value = "select * from environment_base_workshop  where " +"zone = :zone and"+" date = :date",nativeQuery = true)
    List<EnvironmentBaseWorkShop> findAllByZoneAndYear(@Param("zone") String zone,@Param("date") String date);//按区域和年获取数据

    @Query(value = "select * from environment_base_workshop  where " +" date = :date",nativeQuery = true)
    List<EnvironmentBaseWorkShop> findAllByDate(@Param("date") String date);//纯粹按月份查找数据 所以excel的数据格式绝对不能错
}
