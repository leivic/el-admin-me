package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContinueFileRepository extends JpaRepository<ContinueFile,Integer> {

    @Query(value = "select * from continue_importfile  where " +" id = :id",nativeQuery = true)
    ContinueFile findById(@Param("id") int id);

    @Query(value = "select * from continue_importfile " ,nativeQuery = true) //nativeQuery代表开启原生sql写法 ：date是传入下方名为date的参数
    Page<ContinueFile> findAllfile(Pageable pageable);//按部门查找所有符合的工位清单
}
