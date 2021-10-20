package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.ContinueFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContinueFileRepository extends JpaRepository<ContinueFile,Integer> {

    @Query(value = "select * from continue_importfile  where " +" id = :id",nativeQuery = true)
    ContinueFile findById(@Param("id") int id);
}
