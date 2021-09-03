package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.GongWeiFuHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface GongWeiFuHeRepository extends JpaRepository<GongWeiFuHe,Integer> {

    @Query(value = "select * from gongweifuhelv  where " + "left(pinshenshijian,7) = :date and "+"zipingorchoucha = :pingShengXingZhi",nativeQuery = true) //nativeQuery代表开启原生sql写法 ：date是传入下方名为date的参数
    List<GongWeiFuHe> selectGongWeiFuHeListByDate(@Param("date") String date,@Param("pingShengXingZhi") String pingShengXingZhi);//按时间和性质查找所有工位符合清单

}
