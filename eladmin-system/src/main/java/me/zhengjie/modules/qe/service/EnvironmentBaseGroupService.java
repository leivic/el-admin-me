package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.EnvironmentBaseGroup;
import me.zhengjie.modules.qe.repository.EnvironmentBaseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
