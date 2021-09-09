package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.EnvironmentBaseGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvironmentBaseGroupRepository extends JpaRepository<EnvironmentBaseGroup,Integer> {

    @Query(value = "select * from environment_base_group  where " +"zone = :zone",nativeQuery = true) //nativeQuery代表开启原生sql写法 ：date是传入下方名为date的参数
    Page<EnvironmentBaseGroup> findAllByZone(@Param("zone") String zone, Pageable pageable);
}
