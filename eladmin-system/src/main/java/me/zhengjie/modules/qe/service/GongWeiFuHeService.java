package me.zhengjie.modules.qe.service;


import me.zhengjie.modules.qe.domain.GongWeiFuHe;
import me.zhengjie.modules.qe.repository.GongWeiFuHeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GongWeiFuHeService {

    @Autowired
    private GongWeiFuHeRepository gongWeiFuHeRepository;


    public List<GongWeiFuHe> findAllGongWeiFuHe(){

        return gongWeiFuHeRepository.findAll();

    }
}
