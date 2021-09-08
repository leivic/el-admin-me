package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import me.zhengjie.modules.qe.repository.EnvironmentBaseStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



@Service
public class EnvironmentBaseStationService {
    @Autowired
    private EnvironmentBaseStationRepository environmentBaseStationRepository;

    /*查询所有*/
    public Page<EnvironmentBaseStation> findAllEnvironmentBaseStation(int page, int size, String sort){ //jpa封装的分页工具 返回EnvironmentBaseStation泛型的page类型
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort);// jpa封装的分页工具 第几页 每页有多少 按什么排序



        return environmentBaseStationRepository.findAll(pageable);
    }
    /*参数为一条数据对象，增加数据 .save()*/
    public void insertEnvironmentBaseStation(EnvironmentBaseStation environmentBaseStation){
        environmentBaseStationRepository.save(environmentBaseStation);

    }
    /*按照id删除对象*/
    public void deleteEnvironmentBaseStationByid(int id){
        environmentBaseStationRepository.deleteById(id);
    }

    /*重新排序id*/

}
