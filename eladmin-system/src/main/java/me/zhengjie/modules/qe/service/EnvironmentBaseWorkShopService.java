package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseWorkShop;
import me.zhengjie.modules.qe.repository.EnvironmentBaseWorkShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
