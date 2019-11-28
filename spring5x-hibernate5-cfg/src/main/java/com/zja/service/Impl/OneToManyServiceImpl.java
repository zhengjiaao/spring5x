package com.zja.service.Impl;

import com.zja.dto.oneToMany.DeptDTO;
import com.zja.dto.oneToMany.EmployeeDTO;
import com.zja.entity.oneToMany.Dept;
import com.zja.entity.oneToMany.Employee;
import com.zja.service.OneToManyService;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-27 14:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class OneToManyServiceImpl implements OneToManyService {

    @Resource
    private HibernateTemplate hibernateTemplate;

    @Override
    public Object save(){
        // 实例化部门和员工
        Dept dept1 = new Dept();
        dept1.setDeptName("人事部");
        Dept dept2 = new Dept();
        dept2.setDeptName("技术部");

        Employee employee1 = new Employee();
        employee1.setEmpName("员工1");
        employee1.setSalary(1000);
        Employee employee2 = new Employee();
        employee2.setEmpName("员工2");
        employee2.setSalary(2000);

        Employee employee3 = new Employee();
        employee3.setEmpName("员工3");
        employee3.setSalary(3000);
        Employee employee4 = new Employee();
        employee4.setEmpName("员工4");
        employee4.setSalary(4000);

        // 通过部门（一）设置员工（多）【不推荐】　
        Set<Employee> employees = new HashSet<>();
        employees.add(employee1);
        employees.add(employee2);
        dept1.setEmps(employees);
        // 通过员工（多）设置部门（一）【推介】
        employee3.setDept(dept2);
        employee4.setDept(dept2);

        // 数据最好通过多的一方维护，这样可以减少update sql
        // 保存的顺序最好是先保存一的一方再保存多的一方，提高效率，少执行update sql语句
        // 【不推介】
        this.hibernateTemplate.save(employee1);
        this.hibernateTemplate.save(employee2);
        this.hibernateTemplate.save(dept1);
        // 【推介】
        this.hibernateTemplate.save(dept2);
        this.hibernateTemplate.save(employee3);
        this.hibernateTemplate.save(employee4);

        return "成功";
    }


    //获取部门
    @Override
    public Object getDept(int deptId){
        Dept dept = this.hibernateTemplate.get(Dept.class, deptId);
        System.out.println("部门名称："+dept.getDeptName());
        Set<Employee> emps = dept.getEmps();
        Iterator<Employee> iterator = emps.iterator();
        Set<EmployeeDTO> employeeDTOSet = new HashSet<>();
        while (iterator.hasNext()){
            Employee employee = iterator.next();
            System.out.println("员工名称："+employee.getEmpName());

            EmployeeDTO employeeDTO =new EmployeeDTO();
            employeeDTO.setEmpId(employee.getEmpId());
            employeeDTO.setEmpName(employee.getEmpName());
            employeeDTO.setSalary(employee.getSalary());
            employeeDTOSet.add(employeeDTO);
        }

        DeptDTO deptDTO = new DeptDTO();
        deptDTO.setDeptId(dept.getDeptId());
        deptDTO.setDeptName(dept.getDeptName());
        deptDTO.setEmployeeDTOSet(employeeDTOSet);

        return deptDTO;
        //return dept;
    }
    //获取所有部门
    @Override
    public Object getAllDept(){
        return this.hibernateTemplate.loadAll(Dept.class);
    }

    //获取员工
    @Override
    public Object getEmployee(int empId){
        Employee employee = this.hibernateTemplate.get(Employee.class, empId);
        System.out.println("员工名称："+employee.getEmpName());
        Dept dept = employee.getDept();
        System.out.println("员工【 "+employee.getEmpName()+" 】属于 部门："+dept.getDeptName());


        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmpId(employee.getEmpId());
        employeeDTO.setEmpName(employee.getEmpName());
        employeeDTO.setSalary(employee.getSalary());

        DeptDTO deptDTO = new DeptDTO();
        deptDTO.setDeptId(employee.getDept().getDeptId());
        deptDTO.setDeptName(employee.getDept().getDeptName());
        employeeDTO.setDeptDTO(deptDTO);

        return employeeDTO;
        //return employee;
    }
    //获取所有员工
    @Override
    public Object getAllEmployee(){
        return this.hibernateTemplate.loadAll(Employee.class);
    }

}
