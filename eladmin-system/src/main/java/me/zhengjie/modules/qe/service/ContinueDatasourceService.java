package me.zhengjie.modules.qe.service;

import me.zhengjie.modules.qe.domain.ContinueDatasource;
import me.zhengjie.modules.qe.repository.ContinueDatasourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContinueDatasourceService {
    @Autowired
    private ContinueDatasourceRepository continueDatasourceRepository;

    public ContinueDatasource findByDateAndZone( String zone,  String date){ //查找一个月和区域的数据
        return continueDatasourceRepository.findByDateAndZone(zone,date);
    };

    public void save(ContinueDatasource continueDatasource){ //存储一个月的数据
        continueDatasourceRepository.save(continueDatasource);
    }

    public int findCountByDateAndZone(String zone,String date){ //按月份和区域查找是否有这条数据的数量
        return continueDatasourceRepository.findCountByDateAndZone(zone,date);
    };

    public void updateContinueDatasource(String zone,String date,double x1,double x2,double x3,double x4,double x5,double x6,double x7,double x8,double x9,double x10,double x11,double x12){
        continueDatasourceRepository.updateContinueDatasource(zone, date, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
    };//按输入的内容更新所有数据
}
