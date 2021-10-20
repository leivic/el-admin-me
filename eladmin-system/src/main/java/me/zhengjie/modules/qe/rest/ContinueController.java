package me.zhengjie.modules.qe.rest;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.ContinueFile;
import me.zhengjie.modules.qe.service.ContinueFileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：质量生态持续")
@RequestMapping("/qe/continue")
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
        continueFile.setFile_name(oldFileName).setFile_type(file_type).setFile_date(file_date).setZone(zone).setCreate_by(create_by).setPath("/files/" + date).setNewfilename(newFileName);
        System.out.println(continueFile);
        continueFileService.saveContinueFile(continueFile);


        return "1";
    }

    @GetMapping("/findAllContinue")
    public List<ContinueFile> findAllContinue(){
        return continueFileService.findAllContinue();
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

}
