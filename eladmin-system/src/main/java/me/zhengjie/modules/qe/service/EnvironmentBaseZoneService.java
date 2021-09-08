package me.zhengjie.modules.qe.service;


import me.zhengjie.modules.qe.domain.EnvironmentBaseZone;
import me.zhengjie.modules.qe.repository.EnrironmentBaseZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentBaseZoneService {
    @Autowired
    private EnrironmentBaseZoneRepository enrironmentBaseZoneRepository;

    public void insertEnrironmentBaseZone(EnvironmentBaseZone environmentBaseZone){
        enrironmentBaseZoneRepository.save(environmentBaseZone);
    }

    public List<EnvironmentBaseZone> findAllEnvironmentBaseZone(){
        return enrironmentBaseZoneRepository.findAll();
    }
}
