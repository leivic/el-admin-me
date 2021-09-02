package me.zhengjie.modules.qe.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.qe.domain.GongWeiFuHe;
import me.zhengjie.modules.qe.service.GongWeiFuHeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "质量：qe管理")
@RequestMapping("/qe")
public class Controller {

    @Autowired
    private GongWeiFuHeService gongWeiFuHeService;


    @ApiOperation("查询所有工位数据")
    @GetMapping(value = "/findAllGongWeiFuHe")
    public List<GongWeiFuHe> findAll(

    ){
        return gongWeiFuHeService.findAllGongWeiFuHe();
    }
}
