package me.zhengjie.modules.qe.service;


import me.zhengjie.modules.qe.domain.EnvironmentBaseWorkShop;
import me.zhengjie.modules.qe.domain.EnvironmentBaseZone;
import me.zhengjie.modules.qe.repository.EnrironmentBaseZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentBaseZoneService {
    @Autowired
    private EnrironmentBaseZoneRepository enrironmentBaseZoneRepository;

    public void insertEnrironmentBaseZone(EnvironmentBaseZone environmentBaseZone){
        enrironmentBaseZoneRepository.save(environmentBaseZone);
    }

    /*查询所有数据*/
    public Page<EnvironmentBaseZone> findAllEnvironmentBaseZone(int page, int size, String sort){ //jpa封装的分页工具 返回EnvironmentBaseStation泛型的page类型
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);// jpa封装的分页工具 第几页 每页有多少 按什么排序



        return enrironmentBaseZoneRepository.findAll(pageable);
    }
}
