package me.zhengjie.modules.qe.repository;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EnvironmentBaseStationRepository extends JpaRepository<EnvironmentBaseStation,Integer> {



}
