package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.repository.ContinueFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
@Service
public class ContinueFileService {

    @Autowired
    private ContinueFileRepository continueFileRepository;

    public void saveContinueFile(ContinueFile continueFile){ //service层的正确用法
        continueFile.setStatus("1");
        continueFile.setDownloadcounts(0);
        continueFile.setCreate_time(new Date());
        continueFileRepository.save(continueFile); //调用JPA封装的储存服务
    }

    public Page<ContinueFile> findAllContinue(int page, int size, String sort){
        PageRequest pageable = PageRequest.of(page-1, size, Sort.Direction.ASC, sort);
        return continueFileRepository.findAllfile(pageable);
    }

    public ContinueFile findByid(int id){
        return continueFileRepository.findById(id);
    }
}
