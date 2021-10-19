package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.repository.ContinueFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class ContinueFileService {

    @Autowired
    private ContinueFileRepository continueFileRepository;

    public void saveContinueFile(ContinueFile continueFile){
        continueFile.setStatus("1");
        continueFile.setDownloadcounts(0);
        continueFileRepository.save(continueFile); //调用JPA封装的储存服务
    }

}
