package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseWorkShop;
import me.zhengjie.modules.qe.repository.EnvironmentBaseWorkShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnvironmentBaseWorkShopService {

    @Autowired
    private EnvironmentBaseWorkShopRepository environmentBaseWorkShopRepository;

    public List<EnvironmentBaseWorkShop> findAllEnvironmentbaseworkshop(){
        return environmentBaseWorkShopRepository.findAll();
    }

    public void insertEnvironmentBaseWorkShop(EnvironmentBaseWorkShop environmentBaseWorkShop){
        environmentBaseWorkShopRepository.save(environmentBaseWorkShop);
    }

    /*查询所有数据*/
    public Page<EnvironmentBaseWorkShop> findAllEnvironmentBaseWorkshop(int page, int size, String sort){ //jpa封装的分页工具 返回EnvironmentBaseStation泛型的page类型
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);// jpa封装的分页工具 第几页 每页有多少 按什么排序



        return environmentBaseWorkShopRepository.findAll(pageable);
    }

    /*按区域查询所有*/
    public Page<EnvironmentBaseWorkShop> findAllEnvironmentBaseWorkShopByZone(String zone, int page, int size, String sort){ //按部门取数据 实现数据控制
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);



        return environmentBaseWorkShopRepository.findAllByZone(zone,pageable);
    }

    /*按区域和月份查找所有*/
    public List<EnvironmentBaseWorkShop> findAllByZoneAndYear(String zone,String date){
        return environmentBaseWorkShopRepository.findAllByZoneAndYear(zone, date);
    }

    /*按ID删除对象*/
    public void deleteEnvironmentBaseWorkshopByid(int id){
        environmentBaseWorkShopRepository.deleteById(id);
    }

    /*按区域和月份查找所有*/
    public List<EnvironmentBaseWorkShop> findAllBydate(String date){
        return environmentBaseWorkShopRepository.findAllByDate(date);
    }
}
