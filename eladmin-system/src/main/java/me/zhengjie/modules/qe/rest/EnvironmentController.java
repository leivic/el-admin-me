package me.zhengjie.modules.qe.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.GongWeiFuHe;
import me.zhengjie.modules.qe.polo.GongWeiFuHeLastData;
import me.zhengjie.modules.qe.service.GongWeiFuHeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：质量生态环境")
@RequestMapping("/qe")
public class EnvironmentController {

    @Autowired
    private GongWeiFuHeService gongWeiFuHeService;


    @ApiOperation("查询所有工位数据")
    @GetMapping(value = "/findAllGongWeiFuHe")
    public List<GongWeiFuHe> findAll(

    ){
        return gongWeiFuHeService.findAllGongWeiFuHe();
    }

    @ApiOperation("查询二阶工位数据")
    @GetMapping(value = "/getLastGongWeiData")
    GongWeiFuHeLastData[] getLastGongWeiData(String date, String pingShengXingZhi){ //返回一个对象数组
        System.out.println(gongWeiFuHeService.selectGongWeiFuHeListByDate(date,pingShengXingZhi));
        List<GongWeiFuHe> test=gongWeiFuHeService.selectGongWeiFuHeListByDate(date,pingShengXingZhi);
        //解决思路 1.先取出每一项工位＋区域 然后去重得到一个数组 2.以工位＋区域循环数组每一项  取出相等时的所有条目  算平均值 然后与把平均值加入一个list

        List<String> stationName=test.stream().map(GongWeiFuHe::getStationname).distinct().collect(Collectors.toList());//java8 stream特性 操作集合取出StationName 并去重
        System.out.println(stationName);

        GongWeiFuHeLastData[] gongWeiFuHeLastData=new GongWeiFuHeLastData[stationName.size()];//新建一个对象数组 这是返回给echarts的对象数组

        for(int i=0,n=stationName.size();i<n;i++){   //循环每一个工位
            final int d=i;
            List<GongWeiFuHe> filterStation=test.stream().filter(gongWeiFuHe -> gongWeiFuHe.getStationname().equals(stationName.get(d))).collect(Collectors.toList());//过滤出和工位名称相等的list集合
            System.out.println(filterStation);
            Double getStationPercentage=(filterStation.stream().filter(gongWeiFuHe ->
                    gongWeiFuHe.getStationpercentage()!=null).mapToDouble((GongWeiFuHe::getStationpercentage)))
                    .average().orElse(0D);
            System.out.println(getStationPercentage);//得到循环出来分别有每个工位的集合 再分别算平均值

            GongWeiFuHeLastData gongWeiFuHeLastData1=new GongWeiFuHeLastData();//新建对象 现在已经得到了每个工位和对应的 平均值 要做的是把他们加入对象数组内 返回给前端
            gongWeiFuHeLastData1.setStationName(stationName.get(i));
            gongWeiFuHeLastData1.setStationPercentage(getStationPercentage);//
            gongWeiFuHeLastData[i]=gongWeiFuHeLastData1;
            System.out.println(gongWeiFuHeLastData1);
        }

        return gongWeiFuHeLastData;

    }
}
