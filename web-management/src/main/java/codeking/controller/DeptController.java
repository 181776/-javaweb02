package codeking.controller;


import codeking.pojo.Dept;
import codeking.pojo.Result;
import codeking.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {

    @Autowired
    DeptService deptService;

    @GetMapping
    public Result list() {
        log.info("查询部门列表");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }
    /**
     * 根据id删除部门 - delete http://localhost:8080/depts?id=1
     */
    @DeleteMapping
    public Result delete(Integer id) {
        log.info("根据id删除部门, id: {}", id);
        deptService.deleteById(id);
        return Result.success();
    }
    /**
     * 根据ID查询 - GET http://localhost:8080/depts/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据ID查询, id={}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 修改部门 - PUT http://localhost:8080/depts  请求参数：{"id":1,"name":"研发部"}
     */
    @PutMapping
    public Result update(@RequestBody Dept dept) {
        log.info("修改部门, dept: {}" , dept);
        deptService.update(dept);
        return Result.success();
    }

    /**
     * 新增部门 - POST http://localhost:8080/depts   请求参数：{"name":"研发部"}
     */
    @PostMapping
    public Result save(@RequestBody Dept dept) {
        log.info("新增部门, dept: {}" , dept);
        deptService.save(dept);
        return Result.success();
    }
}
