package com.didu.web;

import com.didu.domain.*;
import com.didu.service.ActivityService;
import com.didu.service.EncyclopediasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */
@Controller
public class EncyclopediasControl {
    @Autowired
    private EncyclopediasService encyclopediasService;
    @ModelAttribute("dir")
    public File pre(HttpServletRequest request) {
        File dir = new File(new File(request.getServletContext().getRealPath("/")), "encyclopedias");
        System.out.println(request.getServletContext().getRealPath("/"));
        // 验证文件夹是否存在，不存在就新建
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    @RequestMapping("/encyclopediasUp")
    @ResponseBody
    public String addEncyclopedias(@RequestParam(name = "files", required = false) CommonsMultipartFile[] files, @RequestParam(name = "files1", required = false) CommonsMultipartFile[] files1,@ModelAttribute(value = "dir") File dir
            ,Encyclopedias encyclopedias,Encyclopediasdetail encyclopediasdetail) throws IOException {
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isEmpty() && files[i].getSize() > 0) {
                // 获取上传的文件的名称
                String fileName = files[i].getOriginalFilename();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                    // 设置文件存储位置--->当前项目的平级目录下
                    // 限制文件名称最长位50，若超出截取后面部分

                    if (fileName.length() > 50) {
                        fileName = fileName.substring(fileName.length() - 51);
                    }
                    File file1 = new File(dir,System.currentTimeMillis() +"_"+ fileName);
                    files[i].transferTo(file1);
                    encyclopedias.setUrl(file1.getName());
                    boolean b1 = encyclopediasService.addImage(encyclopedias);
                    int i1 = encyclopediasService.queryMax();
                    for (int j=0;j<files1.length;j++){
                        if (!files1[j].isEmpty() && files1[j].getSize() > 0) {
                            String fileName1 = files1[j].getOriginalFilename();
                            if (fileName1.endsWith(".jpg")||fileName1.endsWith(".png")){
                                if (fileName1.length() > 50) {
                                    fileName1 = fileName1.substring(fileName1.length() - 51);
                                }
                                File file2 = new File(dir,System.currentTimeMillis() +"_"+ fileName1);
                                files1[j].transferTo(file2);
                                encyclopediasdetail.setUrl(file2.getName());
                                encyclopediasdetail.setEid(i1);
                                boolean b = encyclopediasService.addActivitytext(encyclopediasdetail);
                            }
                        }
                    }
                    return "true";
                }
                return "false";
            }
        }
        return "false";
    }
    //删除活动
    @RequestMapping("/deEncyclopedias")
    @ResponseBody
    public String deActivity(Encyclopedias showactivity,HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("/");
        List<Encyclopedias> showactivities = encyclopediasService.queryShowactivityByid(showactivity);
        for (Encyclopedias shac : showactivities) {
            String url = shac.getUrl();
            String ru = realPath + "encyclopedias" + File.separator + url;
            File file = new File(ru);
            file.delete();
            int id = shac.getId();
            List<Encyclopediasdetail> activitytexts = encyclopediasService.queryActivity(id);
            if (activitytexts.size() > 0) {
                boolean b = false;
                for (Encyclopediasdetail ac : activitytexts) {
                    String url1 = ac.getUrl();
                    String s = realPath + "encyclopedias" + File.separator + url1;
                    File f = new File(s);
                    f.delete();
                    ac.setId(id);
                    b = encyclopediasService.deActivity(ac);
                }
                boolean d = encyclopediasService.deShowactivity(id);
                if (d) {
                    return "true";
                } else {
                    return "false";
                }
            }else{
                return "false";
            }
        }
        return "flase";
    }
    //上传详情
    @RequestMapping("/admin/addActivitydetail")
    @ResponseBody
    public String addCoachtable(Encyclopediasdetail combotable,@RequestParam(name = "files", required = false) CommonsMultipartFile[] files, @ModelAttribute(value = "dir") File dir
    ) throws IOException {
        int id = combotable.getId();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isEmpty() && files[i].getSize() > 0) {
                // 获取上传的文件的名称
                String fileName = files[i].getOriginalFilename();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                    // 设置文件存储位置--->当前项目的平级目录下
                    // 限制文件名称最长位50，若超出截取后面部分

                    if (fileName.length() > 50) {
                        fileName = fileName.substring(fileName.length() - 51);
                    }
                    File file1 = new File(dir,System.currentTimeMillis() +"_"+ fileName);
                    files[i].transferTo(file1);
                    combotable.setUrl(file1.getName());
                    combotable.setEid(id);
                    boolean b = encyclopediasService.addActivitytext(combotable);
                    if (b){
                        return "true";
                    }else {
                        return "false";
                    }
                }
                return "false";
            }
        }
        return "false";
    }
    //删除单个详情
    @RequestMapping("/admin/deEncyclopediasdetail")
    @ResponseBody
    public String deEncyclopediasdetail(Encyclopediasdetail coachdetail,@ModelAttribute(value = "dir")File dir){
        Encyclopediasdetail encyclopediasdetail = encyclopediasService.queryEncyclopediasdetailI(coachdetail.getId());
        String realpath = dir.toString();
            String s = realpath + File.separator + encyclopediasdetail.getUrl();
            File f= new File(s);
            f.delete();
        boolean a= encyclopediasService.deEncyclopediasdetail(encyclopediasdetail.getId());

        if (a){
            return "true";
        }else{
            return "false";
        }
    }
    @RequestMapping("/queryEncyclopedias")
    @ResponseBody
    public List<Encyclopedias> queryEncyclopedias(){
        return encyclopediasService.queryEncyclopedias();
    }
    @RequestMapping("/queryEncyclopediasdetail")
    @ResponseBody
    public Encyclopedias queryEncyclopediasdetail(int id){
        Encyclopedias encyclopedias = encyclopediasService.queryEncyclopediasByid(id);
        List<Encyclopediasdetail> encyclopediasdetailList =encyclopediasService.queryEncyclopediasdetail(id);
        encyclopedias.setEncyclopediasdetails(encyclopediasdetailList);
        return encyclopedias;
    }
}
