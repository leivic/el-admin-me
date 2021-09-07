package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import me.zhengjie.modules.qe.repository.EnvironmentBaseStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentBaseStationService {
    @Autowired
    private EnvironmentBaseStationRepository environmentBaseStationRepository;

    /*查询所有*/
    public List<EnvironmentBaseStation> findAllEnvironmentBaseStation(){
        return environmentBaseStationRepository.findAll();
    }
    /*参数为一条数据对象，增加数据 .save()*/
    public void insertEnvironmentBaseStation(EnvironmentBaseStation environmentBaseStation){
        environmentBaseStationRepository.save(environmentBaseStation);

    }
    /*按照id删除对象*/
    public void deleteEnvironmentBaseStation(int id){
        environmentBaseStationRepository.deleteById(id);
    }

    /*重新排序id*/

}
