package me.zhengjie.modules.qe.rest;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.ContinueDatasource;
import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.polo.ContinuePiechart;
import me.zhengjie.modules.qe.polo.Continuetotaldata;
import me.zhengjie.modules.qe.service.ContinueDatasourceService;
import me.zhengjie.modules.qe.service.ContinueFileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：质量生态持续")
@RequestMapping("/qe/continue")
public class ContinueController {
    @Autowired
    private ContinueFileService continueFileService;
    @Autowired
    private ContinueDatasourceService continueDatasourceService;

    @PostMapping("/upload") //上传文件的方法
    public String upload(MultipartFile aaa,String file_type,String file_date,String zone,String create_by) throws IOException { //Httpsession session

        // 获取文件的原始名称
        System.out.println(aaa);
        String oldFileName = aaa.getOriginalFilename();
        String extension="."+FilenameUtils.getExtension(oldFileName);
        // 生成新的文件名称
        String newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +
                UUID.randomUUID().toString().replace("-", "") + extension;
        // 获取文件的大小
        long size = aaa.getSize();
        // 获取文件类型
        String type = aaa.getContentType();

        // 根据日期生成文件目录
        // ResourceUtils.getURL("classpath:") 是获取当前文件的resources路径
        String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static/files";
        // 日期文件夹
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateDirPath = realPath + "/" + date;
        File dateDir = new File(dateDirPath);
        if (!dateDir.exists()) dateDir.mkdirs();

        // 处理文件上传 这个方法实现文件上传
        aaa.transferTo(new File(dateDir, newFileName));

        // 将文件信息放入数据库
        ContinueFile continueFile = new ContinueFile();
        switch (file_type){
            case"废品损失":
                continueFile.setX("x1");
                break;
            case "物料损耗":
                continueFile.setX("x2");
                break;
            case "质量停线":
                continueFile.setX("x3");
                break;
            case "物料管理":
                continueFile.setX("x4");
                break;
            case "问题拦截":
                continueFile.setX("x5");
                break;
            case "质量资源损失(结果导向，指标完成)":
                continueFile.setX("x6");
                break;
            case "质量资源损失(过程一致性)":
                continueFile.setX("x7");
                break;
            case "体验质量":
                continueFile.setX("x8");
                break;
            case "实物质量":
                continueFile.setX("x9");
                break;
            case "质量策划":
                continueFile.setX("x10");
                break;
            case "方案执行":
                continueFile.setX("x11");
                break;

        }
        continueFile.setFile_name(oldFileName).setFile_type(file_type).setFile_date(file_date).setZone(zone).setCreate_by(create_by).setPath("/files/" + date).setNewfilename(newFileName);
        System.out.println(continueFile);
        continueFileService.saveContinueFile(continueFile);


        return "1";
    }

    @GetMapping("/findAllContinue")
    public Page<ContinueFile> findAllContinue(int page, int size, String sort){
        return continueFileService.findAllContinue(page,size,sort);
    }

    // 文件下载
    @GetMapping("/download")
    public void download(Integer id, String openStyle, HttpServletResponse response) throws IOException {
        // attachement是以附件形式下载, inline是在线打开
        openStyle = "inline".equals(openStyle) ? "inline" : "attachment";
        // 获取文件信息
        ContinueFile continueFile=continueFileService.findByid(id);

        // 根据 文件信息中文件名字 和 文件存储路径 获取文件真实路径
        String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static" + continueFile.getPath();
        // 获取文件输入流
        InputStream is = new FileInputStream(new File(realPath, continueFile.getNewfilename()));
        // 获取响应输出流
        response.setHeader("content-disposition", openStyle + ";fileName=" + URLEncoder.encode(continueFile.getFile_name(), "UTF-8"));
        ServletOutputStream os = response.getOutputStream();
        // 文件拷贝
        IOUtils.copy(is, os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
        System.out.println("执行结束");

    }

    // 数据源查找
    @GetMapping("/findByzoneanddate")
    public ContinueDatasource findByzoneanddate(String zone,String date){
        return continueDatasourceService.findByDateAndZone(zone, date);
    }

    // 更新或者新增数据
    @PostMapping("/updateorsavecontinuedatasource")
    public String updateorsavecontinuedatasource(String zone,String date,double x1,double x2,double x3,double x4,double x5, double x6, double x7, double x8,double x9, double x10, double x11, double x12){
        int conut=continueDatasourceService.findCountByDateAndZone(zone, date);
        if(conut ==0){ //如果这个月份和日期没存在，就新增数据
            ContinueDatasource continueDatasource=new ContinueDatasource();
            ContinueDatasource continueDatasource1=new ContinueDatasource();
            ContinueDatasource continueDatasource2=new ContinueDatasource();
            ContinueDatasource continueDatasource3=new ContinueDatasource();
            ContinueDatasource continueDatasource4=new ContinueDatasource();
            ContinueDatasource continueDatasource5=new ContinueDatasource();

            continueDatasource.setZone(zone).setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(x4).setX5(x5).setX6(x6).setX7(x7).setX8(x8).setX9(x9).setX10(x10).setX11(x11).setX12(x12);
            continueDatasourceService.save(continueDatasource);
            continueDatasourceService.updateContinueDatasourcex1x2(date, x1, x2, x3); //更新该月的所有x1 x2 x3的数据

            if(continueDatasourceService.findCountByDateAndZone("冲压车间",date)==0){ //如果没有冲压车间这条数据  新增一条,但是本来没有 所以除了 x1 x2 x3的数据都是0
                continueDatasource1.setZone("冲压车间").setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(0).setX5(0).setX6(0).setX7(0).setX8(0).setX9(0).setX10(0).setX11(0).setX12(0);
                continueDatasourceService.save(continueDatasource1);
            }
            if(continueDatasourceService.findCountByDateAndZone("车身车间",date)==0){ //如果没有冲压车间这条数据  新增一条,但是本来没有 所以除了 x1 x2 x3的数据都是0
                continueDatasource2.setZone("车身车间").setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(0).setX5(0).setX6(0).setX7(0).setX8(0).setX9(0).setX10(0).setX11(0).setX12(0);
                continueDatasourceService.save(continueDatasource2);
            }
            if(continueDatasourceService.findCountByDateAndZone("涂装车间",date)==0){ //如果没有冲压车间这条数据  新增一条,但是本来没有 所以除了 x1 x2 x3的数据都是0
                continueDatasource3.setZone("涂装车间").setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(0).setX5(0).setX6(0).setX7(0).setX8(0).setX9(0).setX10(0).setX11(0).setX12(0);
                continueDatasourceService.save(continueDatasource3);
            }
            if(continueDatasourceService.findCountByDateAndZone("总装车间",date)==0){ //如果没有冲压车间这条数据  新增一条,但是本来没有 所以除了 x1 x2 x3的数据都是0
                continueDatasource4.setZone("总装车间").setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(0).setX5(0).setX6(0).setX7(0).setX8(0).setX9(0).setX10(0).setX11(0).setX12(0);
                continueDatasourceService.save(continueDatasource4);
            }
            if(continueDatasourceService.findCountByDateAndZone("发动机工厂",date)==0){ //如果没有冲压车间这条数据  新增一条,但是本来没有 所以除了 x1 x2 x3的数据都是0
                continueDatasource5.setZone("发动机工厂").setDate(date).setX1(x1).setX2(x2).setX3(x3).setX4(0).setX5(0).setX6(0).setX7(0).setX8(0).setX9(0).setX10(0).setX11(0).setX12(0);
                continueDatasourceService.save(continueDatasource5);
            }
            return "1"; //前端接收返回的1 新增成功
        }
        else if(conut >0){ //如果这个月份和日期已经存在, 就更新数据  现在 同月的 x1 x2 x3必定是相等的 而且要么都没有 要么都有
            continueDatasourceService.updateContinueDatasource(zone, date, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
            continueDatasourceService.updateContinueDatasourcex1x2(date, x1, x2, x3); //两个updatex1x2x3 确定x1x2x3三个数据只有两种情况、存在的x1 x2 x3 每月的每条都必定一样 要么就都没有
            return "2"; //前端接收返回的2 更新成功
        }
        return "0";
    }

    @GetMapping("/findCountcontinuedatasource")
    public int findCountcontinuedatasource(String zone,String date){
        return continueDatasourceService.findCountByDateAndZone(zone, date);
    }

    @GetMapping("/gettotalcontinueBydate") //所有区域的数据
    public Continuetotaldata gettotalcontinueBydate(String year){
        Continuetotaldata continuetotaldata=new Continuetotaldata();
        ArrayList arrayList1=new ArrayList();
        ArrayList arrayList2=new ArrayList();
        ArrayList arrayList3=new ArrayList();
        ArrayList arrayList4=new ArrayList();
        ArrayList arrayList5=new ArrayList();

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

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",date);
                    arrayList1.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList1.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("车身车间",date);
                    arrayList2.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList2.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("涂装车间",date);
                    arrayList3.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList3.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("总装车间",date);
                    arrayList4.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList4.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("发动机工厂",date);
                    arrayList5.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList5.add(i-1,0);
                }
            }
            continuetotaldata.setChongyadata(arrayList1);
            continuetotaldata.setCheshendata(arrayList2);
            continuetotaldata.setTuzhuangdata(arrayList3);
            continuetotaldata.setZongzhuangdata(arrayList4);
            continuetotaldata.setFadongjidata(arrayList5);
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）//为什么敢这样写 因为数据库所有x的default值是0 所以新增时所有的数据的值都是0
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",date);
                    arrayList1.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList1.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("车身车间",date);
                    arrayList2.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList2.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("涂装车间",date);
                    arrayList3.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList3.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("总装车间",date);
                    arrayList4.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList4.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("发动机工厂",date);
                    arrayList5.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList5.add(i-1,0);
                }

            }

            continuetotaldata.setChongyadata(arrayList1);
            continuetotaldata.setCheshendata(arrayList2);
            continuetotaldata.setTuzhuangdata(arrayList3);
            continuetotaldata.setZongzhuangdata(arrayList4);
            continuetotaldata.setFadongjidata(arrayList5);
        }



        return continuetotaldata;
    };

    @GetMapping("/gettotalcontinueBydateandzone") //自定义区域的数据
    public ArrayList gettotalcontinueBydateandzone(String zone,String year){
        ArrayList arrayList=new ArrayList();
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

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone(zone,date);
                    arrayList.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList.add(i-1,0);
                }


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

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone(zone,date);
                    arrayList.add(i-1,continueDatasource.getX1()*2+continueDatasource.getX2()*6+continueDatasource.getX3()*0.8+continueDatasource.getX4()*1.2+continueDatasource.getX5()*2+continueDatasource.getX6()*0.3+continueDatasource.getX7()*0.3+continueDatasource.getX8()*0.25+continueDatasource.getX9()*0.25+continueDatasource.getX10()*0.2+continueDatasource.getX11()*0.36+continueDatasource.getX12()*0.35);
                }catch(Exception e){
                    arrayList.add(i-1,0);
                }

            }
        }



        return arrayList;
    };

    @GetMapping("/getcontinuezhiliangshuipinBydate") //和上方获取质量生态持续数据代码只有两句不同 其实写在一起 然后传个参数判断是哪种情况就行了 ，没必要写两个接口
    public Continuetotaldata getcontinuezhiliangshuipinBydate(String year){
        Continuetotaldata continuetotaldata=new Continuetotaldata();
        ArrayList arrayList1=new ArrayList();
        ArrayList arrayList2=new ArrayList();
        ArrayList arrayList3=new ArrayList();
        ArrayList arrayList4=new ArrayList();
        ArrayList arrayList5=new ArrayList();

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

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",date);
                    arrayList1.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList1.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("车身车间",date);
                    arrayList2.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList2.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("涂装车间",date);
                    arrayList3.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList3.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("总装车间",date);
                    arrayList4.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList4.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("发动机工厂",date);
                    arrayList5.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList5.add(i-1,0);
                }
            }
            continuetotaldata.setChongyadata(arrayList1);
            continuetotaldata.setCheshendata(arrayList2);
            continuetotaldata.setTuzhuangdata(arrayList3);
            continuetotaldata.setZongzhuangdata(arrayList4);
            continuetotaldata.setFadongjidata(arrayList5);
        }else{ //如果不是本年的数据，默认该年有12个月份
            int nowMonth=12;
            for (int i = 1; i <=nowMonth ; i++) {//循环到当前月份
                if(i<10){
                    date=year+"-0"+i;
                }
                else {
                    date=year+"-"+i;
                }

                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",date);
                    arrayList1.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList1.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("车身车间",date);
                    arrayList2.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList2.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("涂装车间",date);
                    arrayList3.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList3.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("总装车间",date);
                    arrayList4.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList4.add(i-1,0);
                }
                try{ //循环 如果是空指针异常（没查到，就往集合里添加0，查到了就添加真实数据 ）
                    ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("发动机工厂",date);
                    arrayList5.add(i-1,(0.52*(continueDatasource.getX8()+continueDatasource.getX9())+2));
                }catch(Exception e){
                    arrayList5.add(i-1,0);
                }

            }

            continuetotaldata.setChongyadata(arrayList1);
            continuetotaldata.setCheshendata(arrayList2);
            continuetotaldata.setTuzhuangdata(arrayList3);
            continuetotaldata.setZongzhuangdata(arrayList4);
            continuetotaldata.setFadongjidata(arrayList5);
        }



        return continuetotaldata;
    };


    @GetMapping("/getzhiliangtishen")//获取质量提升潜力的数据
    public ArrayList getzhiliangtishen(String date){
        ArrayList arrayList=new ArrayList();


        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",date);
            arrayList.add(0,(1.13*(continueDatasource.getX10()+continueDatasource.getX11()+continueDatasource.getX12())+1.25));
        }catch(Exception e){
            arrayList.add(0,0);
        }

        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("车身车间",date);
            arrayList.add(1,(1.13*(continueDatasource.getX10()+continueDatasource.getX11()+continueDatasource.getX12())+1.25));
        }catch(Exception e){
            arrayList.add(1,0);
        }
        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("涂装车间",date);
            arrayList.add(2,(1.13*(continueDatasource.getX10()+continueDatasource.getX11()+continueDatasource.getX12())+1.25));
        }catch(Exception e){
            arrayList.add(2,0);
        }

        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("总装车间",date);
            arrayList.add(3,(1.13*(continueDatasource.getX10()+continueDatasource.getX11()+continueDatasource.getX12())+1.25));
        }catch(Exception e){
            arrayList.add(3,0);
        }
        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("发动机工厂",date);
            arrayList.add(4,(1.13*(continueDatasource.getX10()+continueDatasource.getX11()+continueDatasource.getX12())+1.25));
        }catch(Exception e){
            arrayList.add(4,0);
        }
        return arrayList;
    }

    @GetMapping("/getContinuePiechartvalue") // 不要这么麻烦 取该月冲压车间的x1 x2 x3即可
    public ArrayList<ContinuePiechart> getContinuePiechartvalue(String month){
        ArrayList<ContinuePiechart> continuePiechartList=new ArrayList<>();

        try{ //从冲压车间开始一个个添加进去
            ContinueDatasource continueDatasource=continueDatasourceService.findByDateAndZone("冲压车间",month);
            ContinuePiechart continuePiechart1=new ContinuePiechart();
            ContinuePiechart continuePiechart2=new ContinuePiechart();
            ContinuePiechart continuePiechart3=new ContinuePiechart();
            continuePiechartList.add(0,continuePiechart1.setName("质量停线").setValue(continueDatasource.getX3()));
            continuePiechartList.add(1,continuePiechart2.setName("返修返工").setValue(continueDatasource.getX2()));
            continuePiechartList.add(2,continuePiechart3.setName("废品损失").setValue(continueDatasource.getX1()));
        }catch(Exception e){ //如果没找到那条数据 那么x1 x2 x3自然是0
            ContinuePiechart continuePiechart1=new ContinuePiechart();
            ContinuePiechart continuePiechart2=new ContinuePiechart();
            ContinuePiechart continuePiechart3=new ContinuePiechart();
            continuePiechartList.add(0,continuePiechart1.setName("质量停线").setValue(0));
            continuePiechartList.add(1,continuePiechart2.setName("返修返工").setValue(0));
            continuePiechartList.add(2,continuePiechart3.setName("废品损失").setValue(0));
        }


        return continuePiechartList;
    };
}
