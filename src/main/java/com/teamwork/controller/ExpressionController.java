package com.teamwork.controller;

import com.teamwork.model.Expression;
import com.teamwork.model.Fraction;
import com.teamwork.model.ResultInfo;
import com.teamwork.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @Author: Lsutin
 * @Date: 2021/10/21 19:44
 * @describe:
 */
@RestController
@CrossOrigin
@RequestMapping("/expressions")
public class ExpressionController {
    @Value("${exercises.txt}")
    String exercises;
    @Value("${answers.txt}")
    String answer;

    @PostMapping("/generateExpressions")
    public ResultInfo generateExpressions(@RequestParam("num") @Min(value = 1,message = "至少创建一个") int num, @RequestParam("maxValue") @Min(value = 1,message = "最大值必须为正数") int maxValue){
        ArrayList<String> expressions = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        for (int i=0;i<num;i++){
            Expression expression = new Expression(maxValue);
            expressions.add(expression.getExpression());
            result.add(expression.getValue().toString());
        }
        FileUtil.writeFile(exercises,expressions);
        FileUtil.writeFile(answer,result);
        return ResultInfo.ok(new String[]{exercises,answer});
    }

    @PostMapping("/download")
    public ResultInfo download(@RequestParam("fileName") @NotNull(message = "文件名不能为空") String fileName,HttpServletResponse response){
        try {
            if (!new File(fileName).exists()) {
                return ResultInfo.error("文件："+fileName+" 不存在");
            }
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName.substring(fileName.lastIndexOf(":")+1));
            response.setContentType("multipart/form-data");
            FileUtil.writeFile(fileName,response.getOutputStream());
            return ResultInfo.ok("下载成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResultInfo.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/check")
    public ResultInfo check(@RequestParam("files") @Size(min = 2,max = 2) MultipartFile[] files,HttpServletResponse response){
        ArrayList<String> expressions=null;
        ArrayList<String> result=null;
        ArrayList<Integer> pass=null,fail=null;
        try {
            if (null==files||files.length<2){
                return ResultInfo.error("上传的文件不符合规格");
            }
            expressions = FileUtil.readFile(files[0].getInputStream());
            result = FileUtil.readFile(files[1].getInputStream());
            if (null==expressions||null==result||expressions.size()!=result.size()){
                return ResultInfo.error("两个文件的题目和答案无法一一对应");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pass = new ArrayList<>();
        fail = new ArrayList<>();
        try {
            for (int i=0;i<expressions.size();i++){
                String s = expressions.get(i);//四则运算表达式
                String postfix = Expression.expressionToPostfix(s.substring(0, s.indexOf("=")));//生成后缀表达式
                String postfixResult = Expression.getPostfixResult(postfix);//由后缀表达式生成结果
                if (new Fraction(postfixResult).equals(new Fraction(result.get(i)))) {
                    pass.add(i+1);
                }else {
                    fail.add(i+1);
                }
            }
        }catch (Exception e){
            return ResultInfo.error("上传的文件内容格式错误");
        }
        String sPass = pass.toString();
        sPass=sPass.substring(1,sPass.length()-1);
        String sFail = fail.toString();
        sFail=sFail.substring(1,sFail.length()-1);
        StringBuilder builder = new StringBuilder();
        builder.append("Correct: ").append(pass.size()).append("(").append(sPass).append(")").append("\n\r");
        builder.append("Wrong: ").append(fail.size()).append("(").append(sFail).append(")").append("\n\r");
        response.addHeader("Content-Disposition", "attachment;filename=" + "Grade.txt");
        response.setContentType("multipart/form-data");
        PrintWriter writer = null;
        try {
            writer=response.getWriter();
            writer.write(builder.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            FileUtil.close(writer);
        }
        return ResultInfo.ok(null);
    }

}
