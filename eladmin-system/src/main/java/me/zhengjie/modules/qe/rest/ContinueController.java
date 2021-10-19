package me.zhengjie.modules.qe.rest;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.service.ContinueFileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：质量生态持续")
@RequestMapping("/qe")
public class ContinueController {
    @Autowired
    private ContinueFileService continueFileService;

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
        continueFile.setFile_name(oldFileName).setFile_type(file_type).setFile_date(file_date).setZone(zone).setCreate_by(create_by).setPath(realPath);
        System.out.println(continueFile);
        continueFileService.saveContinueFile(continueFile);


        return "redirect:/file/showAll";
    }


}
