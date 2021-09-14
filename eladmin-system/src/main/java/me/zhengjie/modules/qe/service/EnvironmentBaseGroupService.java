package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseGroup;
import me.zhengjie.modules.qe.domain.EnvironmentBaseStation;
import me.zhengjie.modules.qe.repository.EnvironmentBaseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnvironmentBaseGroupService {

    @Autowired
    private EnvironmentBaseGroupRepository environmentBaseGroupRepository;

    public List<EnvironmentBaseGroup> findAllEnvironmentbasegroup(){
        return environmentBaseGroupRepository.findAll();
    }

    /*添加一条数据进数据库，以对象为参数*/
    public void insertEnvironmentBaseGroup(EnvironmentBaseGroup environmentBaseGroup){
        environmentBaseGroupRepository.save(environmentBaseGroup);
    }
    public Page<EnvironmentBaseGroup> findAllEnvironmentBaseGroup(int page,int size,String sort){
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);

        return environmentBaseGroupRepository.findAll(pageable);
    }

    /*按区域查询所有*/
    public Page<EnvironmentBaseGroup> findAllEnvironmentBaseGroupByZone(String zone, int page, int size, String sort){ //按部门取数据 实现数据控制
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);



        return environmentBaseGroupRepository.findAllByZone(zone,pageable);
    }

    /*按区域和月份查找所有*/
    public List<EnvironmentBaseGroup> findAllByZoneAnddate(String zone, String date){
        return environmentBaseGroupRepository.findAllByZoneAndYear(zone, date);
    }
}
