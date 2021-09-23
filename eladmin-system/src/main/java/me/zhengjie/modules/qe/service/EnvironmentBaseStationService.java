package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import me.zhengjie.modules.qe.repository.EnvironmentBaseStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EnvironmentBaseStationService {
    @Autowired
    private EnvironmentBaseStationRepository environmentBaseStationRepository;

    /*查询所有*/
    public Page<EnvironmentBaseStation> findAllEnvironmentBaseStation(int page, int size, String sort){ //jpa封装的分页工具 返回EnvironmentBaseStation泛型的page类型
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);// jpa封装的分页工具 第几页 每页有多少 按什么排序 //PAGE-1 是因为jpa从0开始，前端从1开始



        return environmentBaseStationRepository.findAll(pageable);
    }

    /*按区域查询所有*/
    public Page<EnvironmentBaseStation> findAllEnvironmentBaseStationByZone(String zone,int page, int size, String sort){ //按部门取数据 实现数据控制
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);



        return environmentBaseStationRepository.findAllByZone(zone,pageable); //findAllByZone 返回的是 Page<EnvironmentBaseStation>类型的,在Dao层的接口就是如此定义
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

    /*按区域和月份查找所有*/
    public List<EnvironmentBaseStation> findAllByZoneAnddate(String zone, String date){
        return environmentBaseStationRepository.findAllByZoneAndYear(zone, date);
    }

    public List<EnvironmentBaseStation> findAllBydate( String date){
        return environmentBaseStationRepository.findAllByDate(date);
    }
}
