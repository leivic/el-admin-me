package me.zhengjie.modules.qe.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.*;
import me.zhengjie.modules.qe.polo.EnvironmentHealthZone;
import me.zhengjie.modules.qe.polo.EnvironmentItem;
import me.zhengjie.modules.qe.polo.EnvironmentSystem;
import me.zhengjie.modules.qe.polo.GongWeiFuHeLastData;
import me.zhengjie.modules.qe.service.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：质量生态环境")
@RequestMapping("/qe")
public class EnvironmentController {

    @Autowired
    private GongWeiFuHeService gongWeiFuHeService;

    @Autowired
    private EnvironmentBaseStationService environmentBaseStationService;

    @Autowired
    private EnvironmentBaseGroupService environmentBaseGroupService;
    @Autowired
    private EnvironmentBaseWorkShopService environmentBaseWorkShopService;
    @Autowired
    private EnvironmentBaseZoneService environmentBaseZoneService;

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

    @ApiOperation("工位: 增加基础数据")
    @PostMapping(value = "/addEnvironmentBaseStation")
    public void addEnvironmentBaseStation(@RequestParam("file") MultipartFile file,String nickName) throws IOException{ //即便是传一个文件，也是可以正常传递变量，文件一个一个参数嘛
        FileInputStream fns=(FileInputStream)file.getInputStream();
        XSSFWorkbook wb=new XSSFWorkbook(fns);//xssWorkbook少了hssworkbook的解析成 POIFSFileSystem数据类型这一步
        XSSFSheet sheetAt = wb.getSheetAt(0);
        if(sheetAt==null) {
            return;
        }

        String written_by= sheetAt.getRow(0).getCell(2).toString();//第一行第三个单元格 :编写
        String date="20"+sheetAt.getRow(0).getCell(3).toString().substring(3,5)+"-"+sheetAt.getRow(0).getCell(3).toString().substring(6,8);
        String zone=sheetAt.getRow(0).getCell(5).toString();//第一行第五个单元格 :区域

        for (int i = 5; i <sheetAt.getRow(1).getLastCellNum() ; i++) {
            EnvironmentBaseStation environmentBaseStation = new EnvironmentBaseStation();
            environmentBaseStation.setStation(sheetAt.getRow(1).getCell(i).toString()); //第一行，第i列，全是工位
            environmentBaseStation.setZone(zone);
            environmentBaseStation.setDate(date);
            environmentBaseStation.setWritten_by(written_by);
            environmentBaseStation.setCreate_by(nickName);
            environmentBaseStation.setPeopleiscapable(sheetAt.getRow(2).getCell(i).getNumericCellValue());
            environmentBaseStation.setMatteriscorrect(sheetAt.getRow(3).getCell(i).getNumericCellValue());
            environmentBaseStation.setWokerisstandard(sheetAt.getRow(4).getCell(i).getNumericCellValue());
            environmentBaseStation.setWokerstability(sheetAt.getRow(5).getCell(i).getNumericCellValue());
            environmentBaseStation.setStationshutdown(sheetAt.getRow(6).getCell(i).getNumericCellValue());
            environmentBaseStation.setMattershutdown(sheetAt.getRow(7).getCell(i).getNumericCellValue());
            environmentBaseStation.setX1(sheetAt.getRow(8).getCell(i).getNumericCellValue());
            environmentBaseStation.setLow_carbon_1(sheetAt.getRow(9).getCell(i).getNumericCellValue());
            environmentBaseStation.setIso(sheetAt.getRow(10).getCell(i).getNumericCellValue());
            environmentBaseStation.setX2(sheetAt.getRow(11).getCell(i).getNumericCellValue());
            environmentBaseStationService.insertEnvironmentBaseStation(environmentBaseStation); //将对象添加进数据库
        }

    }

    @ApiOperation("工位: 查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseStation")
    public Page<EnvironmentBaseStation> findAllEnvironmentBaseStation(int page,int size,String sort){
        return environmentBaseStationService.findAllEnvironmentBaseStation(page,size,sort);
    }

    @ApiOperation("工位: 按部门查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseStationByZone")
    public Page<EnvironmentBaseStation> findAllEnvironmentBaseStationByZone(String zone,int page,int size,String sort){
        return environmentBaseStationService.findAllEnvironmentBaseStationByZone(zone, page, size, sort);
    }

    @ApiOperation("工位: 按年份和区域查找工位的健康水平数据") /*这个写法出了之前的各式不能出问题外，*/
    @GetMapping(value = "/findEnvironmentBaseStationHealthy")
    public ArrayList findEnvironmentBaseStationHealthy(String year,String zone){ //这里确实是传入年
        ArrayList result =new ArrayList(); //不返回一个对象，仅返回一个数据库中x1的数组，最多12个 是今年内每个月x1的平均值
        Calendar now=Calendar.getInstance();
        String date;

        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                double x1=getStationX1(zone,date); //这里传入的是月份 2021-07这样 每个月获得啦就填入数组，为0也填入
                result.add(i-1,x1);
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                double x1=getStationX1(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x1);
            }}

        return result;
    }
    /*  "工位: 按年份查找工位的健康水平数据 " 获取列表平均值后的x1*/
    public Double getStationX1(String zone,String date){ //几月的某区域的健康水平的平均值
        return environmentBaseStationService.findAllByZoneAnddate(zone,date).stream().mapToDouble(EnvironmentBaseStation::getX1).average().orElse(0D);
    }

    @ApiOperation("班组: 增加基础数据")
    @PostMapping(value = "/addEnvironmentBaseGroup")
    public void addEnvironmentBaseGroup(@RequestParam("file") MultipartFile file,String nickName) throws IOException{
        FileInputStream fns=(FileInputStream)file.getInputStream();
        XSSFWorkbook wb=new XSSFWorkbook(fns);//xssWorkbook少了hssworkbook的解析成 POIFSFileSystem数据类型这一步
        XSSFSheet sheetAt = wb.getSheetAt(0);
        if(sheetAt==null) {
            return;
        }

        String written_by= sheetAt.getRow(0).getCell(2).toString();//第一行第三个单元格 :编写
        String date="20"+sheetAt.getRow(0).getCell(3).toString().substring(3,5)+"-"+sheetAt.getRow(0).getCell(3).toString().substring(6,8);
        String zone=sheetAt.getRow(0).getCell(5).toString();//第一行第五个单元格 :区域

        for (int i = 5; i <sheetAt.getRow(1).getLastCellNum() ; i++) {
            EnvironmentBaseGroup environmentBaseGroup=new EnvironmentBaseGroup();
            environmentBaseGroup.setGroup1(sheetAt.getRow(1).getCell(i).toString());
            environmentBaseGroup.setCreate_by(nickName);
            environmentBaseGroup.setDate(date);
            environmentBaseGroup.setZone(zone);
            environmentBaseGroup.setWritten_by(written_by);
            environmentBaseGroup.setGroupstability(sheetAt.getRow(2).getCell(i).getNumericCellValue());
            environmentBaseGroup.setGrouprotation(sheetAt.getRow(3).getCell(i).getNumericCellValue());
            environmentBaseGroup.setExternalaudit(sheetAt.getRow(4).getCell(i).getNumericCellValue());
            environmentBaseGroup.setBookkeepingmanagement(sheetAt.getRow(5).getCell(i).getNumericCellValue());
            environmentBaseGroup.setLossgroupstability(sheetAt.getRow(6).getCell(i).getNumericCellValue());
            environmentBaseGroup.setGroupbusiness(sheetAt.getRow(7).getCell(i).getNumericCellValue());
            environmentBaseGroup.setX3(sheetAt.getRow(8).getCell(i).getNumericCellValue());
            environmentBaseGroup.setFlowpath(sheetAt.getRow(9).getCell(i).getNumericCellValue());
            environmentBaseGroup.setConsistency(sheetAt.getRow(10).getCell(i).getNumericCellValue());
            environmentBaseGroup.setX4(sheetAt.getRow(11).getCell(i).getNumericCellValue());
            environmentBaseGroup.setHealthquthority(sheetAt.getRow(12).getCell(i).getNumericCellValue());
            environmentBaseGroup.setLosshealthquthority(sheetAt.getRow(13).getCell(i).getNumericCellValue());
            environmentBaseGroup.setX5(sheetAt.getRow(14).getCell(i).getNumericCellValue());

            environmentBaseGroupService.insertEnvironmentBaseGroup(environmentBaseGroup);
        }
    }

    @ApiOperation("班组: 查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseGroup")
    public Page<EnvironmentBaseGroup> findAllEnvironmentBaseGroup(int page,int size,String sort){
        return environmentBaseGroupService.findAllEnvironmentBaseGroup(page,size,sort);
    }

    @ApiOperation("班组: 按部门查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseGroupByZone")
    public Page<EnvironmentBaseGroup> findAllEnvironmentBaseGroupByZone(String zone,int page,int size,String sort){
        return environmentBaseGroupService.findAllEnvironmentBaseGroupByZone(zone, page, size, sort);
    }

    @ApiOperation("班组: 按年份和区域查找班组的健康水平数据")
    @GetMapping(value = "/findEnvironmentBaseGroupHealthy")
    public ArrayList findEnvironmentBaseGroupHealthy(String year,String zone){ //这里确实是传入年
        ArrayList result =new ArrayList(); //不返回一个对象，仅返回一个数据库中x1的数组，最多12个 是今年内每个月x1的平均值
        Calendar now=Calendar.getInstance();
        String date;

        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                double x3=getGroupX3(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x3);
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                double x3=getGroupX3(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x3);
            }}

        return result;
    }

    /*  "班组: 按年份查找班组的健康水平数据 " 获取列表平均值后的x3*/
    public Double getGroupX3(String zone,String date){ //几月的某区域的健康水平的平均值
        return environmentBaseGroupService.findAllByZoneAnddate(zone,date).stream().mapToDouble(EnvironmentBaseGroup::getX3).average().orElse(0D);
    }


    @ApiOperation("班组: 按年份和区域查找班组的均衡发展平均数据")
    @GetMapping(value = "/findEnvironmentBaseGroupDelevop")
    public ArrayList findEnvironmentBaseGroupDelevop(String year,String zone){ //这里确实是传入年
        ArrayList result =new ArrayList(); //不返回一个对象，仅返回一个数据库中x1的数组，最多12个 是今年内每个月x1的平均值
        Calendar now=Calendar.getInstance();
        String date;

        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                double x3=getGroupX5(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x3);
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                double x3=getGroupX5(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x3);
            }}

        return result;
    }

    /*  "班组: 按年份查找班组的健康水平数据 " 获取列表平均值后的x3*/
    public Double getGroupX5(String zone,String date){ //几月的某区域的健康水平的平均值
        return environmentBaseGroupService.findAllByZoneAnddate(zone,date).stream().mapToDouble(EnvironmentBaseGroup::getX5).average().orElse(0D);
    }

    /*完成，目前无bug*/
    @ApiOperation("工段: 增加基础数据")
    @PostMapping(value = "/addEnvironmentBaseWorkShop")
    public void addEnvironmentBaseWorkShop(@RequestParam("file") MultipartFile file,String nickName) throws IOException{
        FileInputStream fns=(FileInputStream)file.getInputStream();
        XSSFWorkbook wb=new XSSFWorkbook(fns);//xssWorkbook少了hssworkbook的解析成 POIFSFileSystem数据类型这一步
        XSSFSheet sheetAt = wb.getSheetAt(0);
        if(sheetAt==null) {
            return;
        }

        String written_by= sheetAt.getRow(0).getCell(2).toString();//第一行第三个单元格 :编写
        String date="20"+sheetAt.getRow(0).getCell(3).toString().substring(3,5)+"-"+sheetAt.getRow(0).getCell(3).toString().substring(6,8);
        String zone=sheetAt.getRow(0).getCell(5).toString();//第一行第五个单元格 :区域

        for (int i = 5; i <sheetAt.getRow(1).getLastCellNum() ; i++) {
            EnvironmentBaseWorkShop environmentBaseWorkShop=new EnvironmentBaseWorkShop();
            environmentBaseWorkShop.setWorkshop(sheetAt.getRow(1).getCell(i).toString());
            environmentBaseWorkShop.setCreate_by(nickName);
            environmentBaseWorkShop.setZone(zone);
            environmentBaseWorkShop.setDate(date);
            environmentBaseWorkShop.setWritten_by(written_by);
            environmentBaseWorkShop.setWorkshopstability(sheetAt.getRow(2).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setSubstitute(sheetAt.getRow(3).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setExternalaudit(sheetAt.getRow(4).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setBookkeepingmanagement(sheetAt.getRow(5).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setStudyplan(sheetAt.getRow(6).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setLossworkshopstability(sheetAt.getRow(7).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setWorkshopbusiness(sheetAt.getRow(8).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setX6(sheetAt.getRow(9).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setWorkshopsection(sheetAt.getRow(10).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setProgramfiles(sheetAt.getRow(11).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setEcologicalquality(sheetAt.getRow(12).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setLossprogramfiles(sheetAt.getRow(13).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setLossecologicalquality(sheetAt.getRow(14).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setX7(sheetAt.getRow(15).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setFlowpath(sheetAt.getRow(16).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setConsistency(sheetAt.getRow(17).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setX8(sheetAt.getRow(18).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setDai(sheetAt.getRow(19).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setConsistency2(sheetAt.getRow(20).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setX9(sheetAt.getRow(21).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setHealthquthoritygroup(sheetAt.getRow(22).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setHealthquthoritystation(sheetAt.getRow(23).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setLosshealthquthoritygroup(sheetAt.getRow(24).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setLosshealthquthoritystation(sheetAt.getRow(25).getCell(i).getNumericCellValue());
            environmentBaseWorkShop.setX10(sheetAt.getRow(26).getCell(i).getNumericCellValue());
            environmentBaseWorkShopService.insertEnvironmentBaseWorkShop(environmentBaseWorkShop);

        }
    }

    @ApiOperation("工段: 查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseWorkshop")
    public Page<EnvironmentBaseWorkShop> findAllEnvironmentBaseWorkshop(int page,int size,String sort){
        return environmentBaseWorkShopService.findAllEnvironmentBaseWorkshop(page, size, sort);
    }

    @ApiOperation("工段: 按部门查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseWorkShopByZone")
    public Page<EnvironmentBaseWorkShop> findAllEnvironmentBaseWorkShopByZone(String zone,int page,int size,String sort){
        return environmentBaseWorkShopService.findAllEnvironmentBaseWorkShopByZone(zone, page, size, sort);
    }

    @ApiOperation("工段: 按年份和区域查找工段的健康水平数据")
    @GetMapping(value = "/findEnvironmentBaseWorkshopHealthy")
    public ArrayList findEnvironmentBaseWorkshopHealthy(String year,String zone){ //这里确实是传入年
        ArrayList result =new ArrayList(); //不返回一个对象，仅返回一个数据库中x1的数组，最多12个 是今年内每个月x1的平均值
        Calendar now=Calendar.getInstance();
        String date;

        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                double x6=getWorkshopX6(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x6);
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                double x6=getWorkshopX6(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x6);
            }}

        return result;
    }
    /*  "工段: 按年份和区域查找工段的健康水平数据 " 获取列表平均值后的x6*/
    public Double getWorkshopX6(String zone,String date){ //几年几月的某工段的健康水平的平均值
        return environmentBaseWorkShopService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseWorkShop::getX6).average().orElse(0D);
    }

    @ApiOperation("工段: 按年份和区域查找工段的均衡发展数据")
    @GetMapping(value = "/findEnvironmentBaseWorkshopDelevop")
    public ArrayList findEnvironmentBaseWorkshopDelevop(String year,String zone){ //这里确实是传入年
        ArrayList result =new ArrayList(); //不返回一个对象，仅返回一个数据库中x1的数组，最多12个 是今年内每个月x1的平均值
        Calendar now=Calendar.getInstance();
        String date;

        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                double x6=getWorkshopX10(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x6);
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                double x6=getWorkshopX10(zone,date); //这里传入的是月份 2021-07这样
                result.add(i-1,x6);
            }}

        return result;
    }

    /*  "工段: 按年份和区域查找工段的健康水平数据 " 获取列表平均值后的x6*/
    public Double getWorkshopX10(String zone,String date){ //几年几月的某工段的健康水平的平均值
        return environmentBaseWorkShopService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseWorkShop::getX10).average().orElse(0D);
    }

    @ApiOperation("区域: 增加基础数据")
    @PostMapping(value = "/addEnvironmentBaseZone")
    public void addEnvironmentBaseZone(@RequestParam("file") MultipartFile file,String nickName) throws IOException{
        FileInputStream fns=(FileInputStream)file.getInputStream();
        XSSFWorkbook wb=new XSSFWorkbook(fns);//xssWorkbook少了hssworkbook的解析成 POIFSFileSystem数据类型这一步
        XSSFSheet sheetAt = wb.getSheetAt(0);
        if(sheetAt==null) {
            return;
        }

        String written_by= sheetAt.getRow(0).getCell(2).toString();//第一行第三个单元格 :编写
        String date="20"+sheetAt.getRow(0).getCell(3).toString().substring(3,5)+"-"+sheetAt.getRow(0).getCell(3).toString().substring(6,8);

        for (int i = 5; i <sheetAt.getRow(1).getLastCellNum() ; i++) {
            EnvironmentBaseZone environmentBaseZone=new EnvironmentBaseZone();
            environmentBaseZone.setZone(sheetAt.getRow(1).getCell(i).toString());
            environmentBaseZone.setCreate_by(nickName);
            environmentBaseZone.setDate(date);
            environmentBaseZone.setWritten_by(written_by);
            environmentBaseZone.setGraft(sheetAt.getRow(2).getCell(i).getNumericCellValue());
            environmentBaseZone.setBookkeepingmanagement(sheetAt.getRow(3).getCell(i).getNumericCellValue());
            environmentBaseZone.setStudyplan(sheetAt.getRow(4).getCell(i).getNumericCellValue());
            environmentBaseZone.setExternalaudit(sheetAt.getRow(5).getCell(i).getNumericCellValue());
            environmentBaseZone.setSubstitute(sheetAt.getRow(6).getCell(i).getNumericCellValue());
            environmentBaseZone.setLosszonestability(sheetAt.getRow(7).getCell(i).getNumericCellValue());
            environmentBaseZone.setX11(sheetAt.getRow(8).getCell(i).getNumericCellValue());
            environmentBaseZone.setZonesection(sheetAt.getRow(9).getCell(i).getNumericCellValue());
            environmentBaseZone.setConsistency(sheetAt.getRow(10).getCell(i).getNumericCellValue());
            environmentBaseZone.setProgramfiles(sheetAt.getRow(11).getCell(i).getNumericCellValue());
            environmentBaseZone.setEcologicalquality(sheetAt.getRow(12).getCell(i).getNumericCellValue());
            environmentBaseZone.setLossprogramfiles(sheetAt.getRow(13).getCell(i).getNumericCellValue());
            environmentBaseZone.setLossecologicalquality(sheetAt.getRow(14).getCell(i).getNumericCellValue());
            environmentBaseZone.setX12(sheetAt.getRow(15).getCell(i).getNumericCellValue());
            environmentBaseZone.setDai(sheetAt.getRow(16).getCell(i).getNumericCellValue());
            environmentBaseZone.setConsistency2(sheetAt.getRow(17).getCell(i).getNumericCellValue());
            environmentBaseZone.setX13(sheetAt.getRow(18).getCell(i).getNumericCellValue());
            environmentBaseZone.setHealthquthorityworkshop(sheetAt.getRow(19).getCell(i).getNumericCellValue());
            environmentBaseZone.setHealthquthoritygroup(sheetAt.getRow(20).getCell(i).getNumericCellValue());
            environmentBaseZone.setLosshealthquthorityworkshop(sheetAt.getRow(21).getCell(i).getNumericCellValue());
            environmentBaseZone.setLosshealthquthoritygroup(sheetAt.getRow(22).getCell(i).getNumericCellValue());
            environmentBaseZone.setX14(sheetAt.getRow(23).getCell(i).getNumericCellValue());

            environmentBaseZoneService.insertEnrironmentBaseZone(environmentBaseZone);
        }
    }


    @ApiOperation("区域: 查询基础数据")
    @GetMapping(value = "/findAllEnvironmentBaseZone")
    public Page<EnvironmentBaseZone> findAllEnvironmentBaseZone(int page,int size,String sort){
        return environmentBaseZoneService.findAllEnvironmentBaseZone(page, size, sort);
    }

    @ApiOperation("区域: 按部门查询基础数据")
    @GetMapping(value = "/findAllEnvironmentZoneByZone")
    public Page<EnvironmentBaseZone> findAllEnvironmentZoneByZone(String zone,int page,int size,String sort){
        return environmentBaseZoneService.findAllEnvironmentZoneByZone(zone, page, size, sort);
    }

    @ApiOperation("区域/工段: 获取系统完整图表数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getthisYearEnvironmentSystem")
    public ArrayList<EnvironmentSystem> getthisYearEnvironmentSystem(String year,String workshoporzone) {
        ArrayList<EnvironmentSystem> result=new ArrayList<>();

        Calendar now=Calendar.getInstance();
        String date;
        /*
        int nowMonth=(now.get(Calendar.MONTH))+1;//获取当前月份
        int nowYear=now.get(Calendar.YEAR);//获取当前年

        System.out.println(environmentBaseWorkShopService.findAllByZoneAndYear("车身车间",nowYear+"-0"+nowMonth));
        */
        if(workshoporzone.equals("工段")){ //如果是区域就采用区域的方法
        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据
            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                environmentSystem.setDate(date);
                environmentSystem.setChognya(getWorkshopX7("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentSystem.setCheshen(getWorkshopX7("车身车间",date));
                environmentSystem.setTuzhuang(getWorkshopX7("涂装车间",date));
                environmentSystem.setZongzhuang(getWorkshopX7("总装车间",date));
                environmentSystem.setJijia(getWorkshopX7("机加车间",date));
                environmentSystem.setZhuangpei(getWorkshopX7("装配车间",date));
                environmentSystem.setTotal((getWorkshopX7("冲压车间",date)+getWorkshopX7("车身车间",date)+getWorkshopX7("涂装车间",date)+getWorkshopX7("总装车间",date)+getWorkshopX7("机加车间",date)+getWorkshopX7("装配车间",date))/6);
                result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                environmentSystem.setDate(date);
                environmentSystem.setChognya(getWorkshopX7("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentSystem.setCheshen(getWorkshopX7("车身车间",date));
                environmentSystem.setTuzhuang(getWorkshopX7("涂装车间",date));
                environmentSystem.setZongzhuang(getWorkshopX7("总装车间",date));
                environmentSystem.setJijia(getWorkshopX7("机加车间",date));
                environmentSystem.setZhuangpei(getWorkshopX7("装配车间",date));
                environmentSystem.setTotal((getWorkshopX7("冲压车间",date)+getWorkshopX7("车身车间",date)+getWorkshopX7("涂装车间",date)+getWorkshopX7("总装车间",date)+getWorkshopX7("机加车间",date)+getWorkshopX7("装配车间",date))/6);
                result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
            }
        }
       }
       else{ //如果不是工段，就采用区域的方法
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据
                System.out.println("当前是本年区域的数据");
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getZoneX12("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getZoneX12("车身车间",date));
                    environmentSystem.setTuzhuang(getZoneX12("涂装车间",date));
                    environmentSystem.setZongzhuang(getZoneX12("总装车间",date));
                    environmentSystem.setJijia(getZoneX12("机加车间",date));
                    environmentSystem.setZhuangpei(getZoneX12("装配车间",date));
                    environmentSystem.setTotal((getZoneX12("冲压车间",date)+getZoneX12("车身车间",date)+getZoneX12("涂装车间",date)+getZoneX12("总装车间",date)+getZoneX12("机加车间",date)+getZoneX12("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getZoneX12("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getZoneX12("车身车间",date));
                    environmentSystem.setTuzhuang(getZoneX12("涂装车间",date));
                    environmentSystem.setZongzhuang(getZoneX12("总装车间",date));
                    environmentSystem.setJijia(getZoneX12("机加车间",date));
                    environmentSystem.setZhuangpei(getZoneX12("装配车间",date));
                    environmentSystem.setTotal((getZoneX12("冲压车间",date)+getWorkshopX7("车身车间",date)+getWorkshopX7("涂装车间",date)+getWorkshopX7("总装车间",date)+getWorkshopX7("机加车间",date)+getWorkshopX7("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        return result;
    }

    @ApiOperation("区域: 获取区域健康水平图表数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getthisYearEnvironmentHealthZone")
    public ArrayList<EnvironmentHealthZone> getthisYearEnvironmentHealthZone(String year){
        ArrayList<EnvironmentHealthZone> result=new ArrayList<>();
        Calendar now=Calendar.getInstance();
        String date;
        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                EnvironmentHealthZone environmentHealthZone=new EnvironmentHealthZone();
                environmentHealthZone.setDate(date);
                environmentHealthZone.setChongya(getZoneX11("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentHealthZone.setCheshen(getZoneX11("车身车间",date));
                environmentHealthZone.setTuzhuang(getZoneX11("涂装车间",date));
                environmentHealthZone.setZongzhuang(getZoneX11("总装车间",date));
                environmentHealthZone.setJijia(getZoneX11("机加车间",date));
                environmentHealthZone.setZhuangpei(getZoneX11("装配车间",date));
                result.add(i-1,environmentHealthZone);//往最后要返回的List集里面添加这一条list
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                EnvironmentHealthZone environmentHealthZone=new EnvironmentHealthZone();
                environmentHealthZone.setDate(date);
                environmentHealthZone.setChongya(getZoneX11("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentHealthZone.setCheshen(getZoneX11("车身车间",date));
                environmentHealthZone.setTuzhuang(getZoneX11("涂装车间",date));
                environmentHealthZone.setZongzhuang(getZoneX11("总装车间",date));
                environmentHealthZone.setJijia(getZoneX11("机加车间",date));
                environmentHealthZone.setZhuangpei(getZoneX11("装配车间",date));
                result.add(i-1,environmentHealthZone);//往最后要返回的List集里面添加这一条list
            }}


        return result;
    }




    /*  "区域/工段: 获取系统完整图表数据 " 获取列表平均值后的x7*/
    public Double getWorkshopX7(String zone,String date){
        return environmentBaseWorkShopService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseWorkShop::getX7).average().orElse(0D); //车间名和日期格式不能错，x7不能为空
    }

    /*  "区域/工段: 获取系统完整图表数据 " 获取列表平均值后的x12*/
    public Double getZoneX12(String zone,String date){
        return environmentBaseZoneService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseZone::getX12).average().orElse(0D); //车间名和日期格式不能错，x12不能为空
    }

    /*  "区域: 获取区域健康水平完整图表数据 " 获取列表平均值后的x11*/
    public Double getZoneX11(String zone,String date){ //几月的某区域的健康水平的平均值
        return environmentBaseZoneService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseZone::getX11).average().orElse(0D);
    }


    @ApiOperation("区域/工段: 获取数智互联图表数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getthisYearEnvironmentIntelligence")
    public ArrayList<EnvironmentSystem> getthisYearEnvironmentIntelligence(String year,String workshoporzone) {
        ArrayList<EnvironmentSystem> result=new ArrayList<>();

        Calendar now=Calendar.getInstance();
        String date;
        /*
        int nowMonth=(now.get(Calendar.MONTH))+1;//获取当前月份
        int nowYear=now.get(Calendar.YEAR);//获取当前年

        System.out.println(environmentBaseWorkShopService.findAllByZoneAndYear("车身车间",nowYear+"-0"+nowMonth));
        */
        if(workshoporzone.equals("工段")){ //如果是区域就采用区域的方法
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getWorkshopX9("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getWorkshopX9("车身车间",date));
                    environmentSystem.setTuzhuang(getWorkshopX9("涂装车间",date));
                    environmentSystem.setZongzhuang(getWorkshopX9("总装车间",date));
                    environmentSystem.setJijia(getWorkshopX9("机加车间",date));
                    environmentSystem.setZhuangpei(getWorkshopX9("装配车间",date));
                    environmentSystem.setTotal((getWorkshopX9("冲压车间",date)+getWorkshopX9("车身车间",date)+getWorkshopX9("涂装车间",date)+getWorkshopX9("总装车间",date)+getWorkshopX9("机加车间",date)+getWorkshopX9("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getWorkshopX9("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getWorkshopX9("车身车间",date));
                    environmentSystem.setTuzhuang(getWorkshopX9("涂装车间",date));
                    environmentSystem.setZongzhuang(getWorkshopX9("总装车间",date));
                    environmentSystem.setJijia(getWorkshopX9("机加车间",date));
                    environmentSystem.setZhuangpei(getWorkshopX9("装配车间",date));
                    environmentSystem.setTotal((getWorkshopX9("冲压车间",date)+getWorkshopX9("车身车间",date)+getWorkshopX9("涂装车间",date)+getWorkshopX9("总装车间",date)+getWorkshopX9("机加车间",date)+getWorkshopX9("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        else{ //如果不是工段，就采用区域的方法
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据
                System.out.println("当前是本年区域的数据");
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getZoneX13("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getZoneX13("车身车间",date));
                    environmentSystem.setTuzhuang(getZoneX13("涂装车间",date));
                    environmentSystem.setZongzhuang(getZoneX13("总装车间",date));
                    environmentSystem.setJijia(getZoneX13("机加车间",date));
                    environmentSystem.setZhuangpei(getZoneX13("装配车间",date));
                    environmentSystem.setTotal((getZoneX13("冲压车间",date)+getZoneX13("车身车间",date)+getZoneX13("涂装车间",date)+getZoneX13("总装车间",date)+getZoneX13("机加车间",date)+getZoneX13("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getZoneX13("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getZoneX13("车身车间",date));
                    environmentSystem.setTuzhuang(getZoneX13("涂装车间",date));
                    environmentSystem.setZongzhuang(getZoneX13("总装车间",date));
                    environmentSystem.setJijia(getZoneX13("机加车间",date));
                    environmentSystem.setZhuangpei(getZoneX13("装配车间",date));
                    environmentSystem.setTotal((getZoneX13("冲压车间",date)+getZoneX13("车身车间",date)+getZoneX13("涂装车间",date)+getZoneX13("总装车间",date)+getZoneX13("机加车间",date)+getZoneX13("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        return result;
    }

    /*  "区域/工段: 获取数智互联图表数据 " 获取列表平均值后的x9*/
    public Double getWorkshopX9(String zone,String date){
        return environmentBaseWorkShopService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseWorkShop::getX9).average().orElse(0D); //车间名和日期格式不能错，x7不能为空
    }

    /*  "区域/工段: 获取数智互联图表数据 " 获取列表平均值后的x13*/
    public Double getZoneX13(String zone,String date){
        return environmentBaseZoneService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseZone::getX13).average().orElse(0D); //车间名和日期格式不能错，x12不能为空
    }


    @ApiOperation("工段/班组/工位: 获取低碳精益图表数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getthisYearEnvironmentLowcarbon")
    public ArrayList<EnvironmentSystem> getthisYearEnvironmentLowcarbon(String year,String workshoporzone) {
        ArrayList<EnvironmentSystem> result=new ArrayList<>();

        Calendar now=Calendar.getInstance();
        String date;
        /*
        int nowMonth=(now.get(Calendar.MONTH))+1;//获取当前月份
        int nowYear=now.get(Calendar.YEAR);//获取当前年

        System.out.println(environmentBaseWorkShopService.findAllByZoneAndYear("车身车间",nowYear+"-0"+nowMonth));
        */
        if(workshoporzone.equals("工段")){ //如果是区域就采用区域的方法
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getWorkshopX8("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getWorkshopX8("车身车间",date));
                    environmentSystem.setTuzhuang(getWorkshopX8("涂装车间",date));
                    environmentSystem.setZongzhuang(getWorkshopX8("总装车间",date));
                    environmentSystem.setJijia(getWorkshopX8("机加车间",date));
                    environmentSystem.setZhuangpei(getWorkshopX8("装配车间",date));
                    environmentSystem.setTotal((getWorkshopX8("冲压车间",date)+getWorkshopX8("车身车间",date)+getWorkshopX8("涂装车间",date)+getWorkshopX8("总装车间",date)+getWorkshopX8("机加车间",date)+getWorkshopX8("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getWorkshopX8("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getWorkshopX8("车身车间",date));
                    environmentSystem.setTuzhuang(getWorkshopX8("涂装车间",date));
                    environmentSystem.setZongzhuang(getWorkshopX8("总装车间",date));
                    environmentSystem.setJijia(getWorkshopX8("机加车间",date));
                    environmentSystem.setZhuangpei(getWorkshopX8("装配车间",date));
                    environmentSystem.setTotal((getWorkshopX8("冲压车间",date)+getWorkshopX8("车身车间",date)+getWorkshopX8("涂装车间",date)+getWorkshopX8("总装车间",date)+getWorkshopX8("机加车间",date)+getWorkshopX8("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        else if (workshoporzone.equals("班组")){ //如果不是工段，是班组，就采用班组的方法
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getGroupX4("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getGroupX4("车身车间",date));
                    environmentSystem.setTuzhuang(getGroupX4("涂装车间",date));
                    environmentSystem.setZongzhuang(getGroupX4("总装车间",date));
                    environmentSystem.setJijia(getGroupX4("机加车间",date));
                    environmentSystem.setZhuangpei(getGroupX4("装配车间",date));
                    environmentSystem.setTotal((getGroupX4("冲压车间",date)+getGroupX4("车身车间",date)+getGroupX4("涂装车间",date)+getGroupX4("总装车间",date)+getGroupX4("机加车间",date)+getGroupX4("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getGroupX4("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getGroupX4("车身车间",date));
                    environmentSystem.setTuzhuang(getGroupX4("涂装车间",date));
                    environmentSystem.setZongzhuang(getGroupX4("总装车间",date));
                    environmentSystem.setJijia(getGroupX4("机加车间",date));
                    environmentSystem.setZhuangpei(getGroupX4("装配车间",date));
                    environmentSystem.setTotal((getGroupX4("冲压车间",date)+getGroupX4("车身车间",date)+getGroupX4("涂装车间",date)+getGroupX4("总装车间",date)+getGroupX4("机加车间",date)+getGroupX4("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        else if (workshoporzone.equals("工位")){ //如果是工位
            if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数
                int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=now.get(Calendar.YEAR)+"-0"+i;
                    }
                    else {
                        date=now.get(Calendar.YEAR)+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getStationX2("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getStationX2("车身车间",date));
                    environmentSystem.setTuzhuang(getStationX2("涂装车间",date));
                    environmentSystem.setZongzhuang(getStationX2("总装车间",date));
                    environmentSystem.setJijia(getStationX2("机加车间",date));
                    environmentSystem.setZhuangpei(getStationX2("装配车间",date));
                    environmentSystem.setTotal((getStationX2("冲压车间",date)+getStationX2("车身车间",date)+getStationX2("涂装车间",date)+getStationX2("总装车间",date)+getStationX2("机加车间",date)+getStationX2("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }else{ //如果不是本年的数据，默认该年有12个月份
                int nowMonth=12;
                for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                    if(i<10){
                        date=year+"-0"+i;
                    }
                    else {
                        date=year+"-"+i;
                    }

                    EnvironmentSystem environmentSystem=new EnvironmentSystem();//一条数据就是一个月的数据
                    environmentSystem.setDate(date);
                    environmentSystem.setChognya(getStationX2("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                    environmentSystem.setCheshen(getStationX2("车身车间",date));
                    environmentSystem.setTuzhuang(getStationX2("涂装车间",date));
                    environmentSystem.setZongzhuang(getStationX2("总装车间",date));
                    environmentSystem.setJijia(getStationX2("机加车间",date));
                    environmentSystem.setZhuangpei(getStationX2("装配车间",date));
                    environmentSystem.setTotal((getStationX2("冲压车间",date)+getStationX2("车身车间",date)+getStationX2("涂装车间",date)+getStationX2("总装车间",date)+getStationX2("机加车间",date)+getStationX2("装配车间",date))/6);
                    result.add(i-1,environmentSystem);//往最后要返回的List集里面添加这一条list
                }
            }
        }
        return result;
    }

    /*  "工段/班组/工位: 获取低碳精益图表数据 " 获取列表平均值后的x9*/
    public Double getWorkshopX8(String zone,String date){
        return environmentBaseWorkShopService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseWorkShop::getX8).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    /*  "工段/班组/工位: 获取低碳精益图表数据 " 获取列表平均值后的x4*/
    public Double getGroupX4(String zone,String date){
        return environmentBaseGroupService.findAllByZoneAnddate(zone,date).stream().mapToDouble(EnvironmentBaseGroup::getX4).average().orElse(0D);
    }

    /*  "工段/班组/工位: 获取低碳精益图表数据 " 获取列表平均值后的x4*/
    public Double getStationX2(String zone,String date){
        return environmentBaseStationService.findAllByZoneAnddate(zone,date).stream().mapToDouble(EnvironmentBaseStation::getX2).average().orElse(0D);
    }

    @ApiOperation("工位: 获取每个工位健康水平数据 ")
    @GetMapping(value = "/getStationByZoneAndDate")
    public List<EnvironmentItem> getStationByZoneAndDate(String zone,String date){
        List<EnvironmentItem> environmentItemList=new ArrayList<>(); //先新建Environment类型的列表，后面往这个列表里一项项的添加

            List<EnvironmentBaseStation> environmentBaseStationList= environmentBaseStationService.findAllByZoneAnddate(zone, date);
            for (int i = 0; i <environmentBaseStationList.size(); i++) {
                EnvironmentItem environmentItem=new EnvironmentItem();
                environmentItem.setItem(environmentBaseStationList.get(i).getStation());
                environmentItem.setFraction(environmentBaseStationList.get(i).getX1());
                environmentItemList.add(i,environmentItem);
            }


        return environmentItemList;
    }

    @ApiOperation("班组: 获取每个班组健康水平数据 ")
    @GetMapping(value = "/getGroupByZoneAndDate")
    public List<EnvironmentItem> getGroupByZoneAndDate(String zone,String date){
        List<EnvironmentItem> environmentItemList=new ArrayList<>(); //先新建Environment类型的列表，后面往这个列表里一项项的添加

        List<EnvironmentBaseGroup> environmentBaseGroupList= environmentBaseGroupService.findAllByZoneAnddate(zone, date);
        for (int i = 0; i <environmentBaseGroupList.size(); i++) {
            EnvironmentItem environmentItem=new EnvironmentItem();
            environmentItem.setItem(environmentBaseGroupList.get(i).getGroup1());
            environmentItem.setFraction(environmentBaseGroupList.get(i).getX3());
            environmentItemList.add(i,environmentItem);
        }


        return environmentItemList;
    }

    @ApiOperation("工段: 获取每个工段健康水平数据 ")
    @GetMapping(value = "/getWorkShopByZoneAndDate")
    public List<EnvironmentItem> getWorkShopByZoneAndDate(String zone,String date){
        List<EnvironmentItem> environmentItemList=new ArrayList<>(); //先新建Environment类型的列表，后面往这个列表里一项项的添加

        List<EnvironmentBaseWorkShop> environmentBaseWorkShopList= environmentBaseWorkShopService.findAllByZoneAndYear(zone, date);
        for (int i = 0; i <environmentBaseWorkShopList.size(); i++) {
            EnvironmentItem environmentItem=new EnvironmentItem();
            environmentItem.setItem(environmentBaseWorkShopList.get(i).getWorkshop());
            environmentItem.setFraction(environmentBaseWorkShopList.get(i).getX6());
            environmentItemList.add(i,environmentItem);
        }


        return environmentItemList;
    }



    @ApiOperation("区域: 获取区域均衡发展图表数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getthisYearEnvironmentDevelopZone")
    public ArrayList<EnvironmentHealthZone> getthisYearEnvironmentDevelopZone(String year){
        ArrayList<EnvironmentHealthZone> result=new ArrayList<>();
        Calendar now=Calendar.getInstance();
        String date;
        if(String.valueOf(now.get(Calendar.YEAR)).equals(year)){ //如果是本年的数据

            int nowMonth=(now.get(Calendar.MONTH))+1;//取到当前月份
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=now.get(Calendar.YEAR)+"-0"+i;
                }
                else {
                    date=now.get(Calendar.YEAR)+"-"+i;
                }

                EnvironmentHealthZone environmentHealthZone=new EnvironmentHealthZone();
                environmentHealthZone.setDate(date);
                environmentHealthZone.setChongya(getZoneX14("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentHealthZone.setCheshen(getZoneX14("车身车间",date));
                environmentHealthZone.setTuzhuang(getZoneX14("涂装车间",date));
                environmentHealthZone.setZongzhuang(getZoneX14("总装车间",date));
                environmentHealthZone.setJijia(getZoneX14("机加车间",date));
                environmentHealthZone.setZhuangpei(getZoneX14("装配车间",date));
                result.add(i-1,environmentHealthZone);//往最后要返回的List集里面添加这一条list
            }
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                EnvironmentHealthZone environmentHealthZone=new EnvironmentHealthZone();
                environmentHealthZone.setDate(date);
                environmentHealthZone.setChongya(getZoneX14("冲压车间",date));//车间名和日期格式不能错，否则出现bug
                environmentHealthZone.setCheshen(getZoneX14("车身车间",date));
                environmentHealthZone.setTuzhuang(getZoneX14("涂装车间",date));
                environmentHealthZone.setZongzhuang(getZoneX14("总装车间",date));
                environmentHealthZone.setJijia(getZoneX14("机加车间",date));
                environmentHealthZone.setZhuangpei(getZoneX14("装配车间",date));
                result.add(i-1,environmentHealthZone);//往最后要返回的List集里面添加这一条list
            }}


        return result;
    }

    /*  "工段/班组/工位: 获取均衡发展图表数据 " 获取列表平均值后的x14*/
    public Double getZoneX14(String zone,String date){
        return environmentBaseZoneService.findAllByZoneAndYear(zone,date).stream().mapToDouble(EnvironmentBaseZone::getX14).average().orElse(0D);
    }

    @ApiOperation("班组: 按id删除数据库数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/deleteEnvironmentBaseGroupByid")
    public void deleteEnvironmentBaseGroupByid(int id){
        environmentBaseGroupService.deleteEnvironmentBaseGroupByid(id);
    }

    @ApiOperation("工位: 按id删除数据库数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/deleteEnvironmentBaseStationByid")
    public void deleteEnvironmentBaseStationByid(int id){
        environmentBaseStationService.deleteEnvironmentBaseStationByid(id);
    }

    @ApiOperation("工段: 按id删除数据库数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/deleteEnvironmentBaseWorkshopByid")
    public void deleteEnvironmentBaseWorkshopByid(int id){
        environmentBaseWorkShopService.deleteEnvironmentBaseWorkshopByid(id);
    }

    @ApiOperation("区域: 按id删除数据库数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/deleteEnvironmentBaseZoneByid")
    public void deleteEnvironmentBaseZoneByid(int id){
        environmentBaseZoneService.deleteEnvironmentBaseZoneByid(id);
    }


    /*  "工段/班组/工位: 获取低碳精益图表数据 " 获取列表平均值后的x1*/
    public Double getStationX1onlyBydate(String date){
        return environmentBaseStationService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseStation::getX1).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getStationX2onlyBydate(String date){
        return environmentBaseStationService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseStation::getX2).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getGroupX3onlyBydate(String date){
        return environmentBaseGroupService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseGroup::getX3).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getGroupX4onlyBydate(String date){
        return environmentBaseGroupService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseGroup::getX4).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getGroupX5onlyBydate(String date){
        return environmentBaseGroupService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseGroup::getX5).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getWorkshopX6onlyBydate(String date){
        return environmentBaseWorkShopService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseWorkShop::getX6).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getWorkshopX7onlyBydate(String date){
        return environmentBaseWorkShopService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseWorkShop::getX7).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getWorkshopX8onlyBydate(String date){
        return environmentBaseWorkShopService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseWorkShop::getX8).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getWorkshopX9onlyBydate(String date){
        return environmentBaseWorkShopService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseWorkShop::getX9).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getWorkshopX10onlyBydate(String date){
        return environmentBaseWorkShopService.findAllBydate(date).stream().mapToDouble(EnvironmentBaseWorkShop::getX10).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getZoneX11onlyBydate(String date){
        return environmentBaseZoneService.findAllByDate(date).stream().mapToDouble(EnvironmentBaseZone::getX11).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getZoneX12onlyBydate(String date){
        return environmentBaseZoneService.findAllByDate(date).stream().mapToDouble(EnvironmentBaseZone::getX12).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getZoneX13onlyBydate(String date){
        return environmentBaseZoneService.findAllByDate(date).stream().mapToDouble(EnvironmentBaseZone::getX13).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    public Double getZoneX14onlyBydate(String date){
        return environmentBaseZoneService.findAllByDate(date).stream().mapToDouble(EnvironmentBaseZone::getX14).average().orElse(0D); //车间名和日期格式不能错，x8不能为空
    }

    @ApiOperation("获取总揽数据 ") //一定不能出错的地方 数据库中的日期格式，车间格
    @GetMapping(value = "/getenvironmenttotal")
    public ArrayList getenvironmenttotal(String date){
        ArrayList result=new ArrayList();
        double y1=getStationX1onlyBydate(date)*1.261+getStationX2onlyBydate(date)*0.28;
        double y2=getGroupX3onlyBydate(date)*1.17+getGroupX4onlyBydate(date)*0.22+getGroupX5onlyBydate(date)*1.2;
        double y3=getWorkshopX6onlyBydate(date)*1.4+1.22*getWorkshopX7onlyBydate(date)+1.1*getWorkshopX8onlyBydate(date)+0.3*getWorkshopX9onlyBydate(date)+0.28*getWorkshopX10onlyBydate(date);
        double y4=getZoneX11onlyBydate(date)*1.3+getZoneX12onlyBydate(date)*1.2+getZoneX13onlyBydate(date)*0.29+getZoneX14onlyBydate(date)*0.98;
        double y5=y1+y2+y3+y4;
        result.add(0,y1);
        result.add(1,y2);
        result.add(2,y3);
        result.add(3,y4);
        result.add(4,y5);
        return result;
    }
}
