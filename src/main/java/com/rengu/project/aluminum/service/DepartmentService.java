package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.DepartmentException;
import com.rengu.project.aluminum.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // 保存部门
    @CachePut(value = "department_cache", key = "#departmentEntity.getId()")
    public DepartmentEntity saveDepartment(DepartmentEntity departmentEntity) {
        if (StringUtils.isEmpty(departmentEntity.getName())) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_FOUND);
        }
        if (hasDepartmentByName(departmentEntity.getName())) {
            updateDepartmentById(departmentEntity.getId(), departmentEntity);
//            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_EXISTS);
        }
        return departmentRepository.save(departmentEntity);
    }

    // 根据id删除部门
    @CacheEvict(value = "department_cache", key = "#departmentId")
    public DepartmentEntity deleteDepartmentById(String departmentId) {
        DepartmentEntity departmentEntity = getDepartmentById(departmentId);
        departmentRepository.deleteById(departmentId);
        return departmentEntity;
    }

    // 根据id修改部门属性
    @CachePut(value = "department_cache", key = "#departmentId")
    public DepartmentEntity updateDepartmentById(String departmentId, DepartmentEntity departmentArgs) {
        DepartmentEntity departmentEntity = getDepartmentById(departmentId);
        if (!StringUtils.isEmpty(departmentArgs.getName()) && !departmentArgs.getName().equals(departmentEntity.getName())) {
            if (hasDepartmentByName(departmentArgs.getName())) {
                throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_EXISTS);
            } else {
                departmentEntity.setName(departmentArgs.getName());
            }
        }
        departmentEntity.setDescription(departmentArgs.getDescription());
        return departmentRepository.save(departmentEntity);
    }

    // 根据id查询部门
    @Cacheable(value = "department_cache", key = "#departmentId")
    public DepartmentEntity getDepartmentById(String departmentId) {
        if (StringUtils.isEmpty(departmentId)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_ID_NOT_FOUND);
        }
        Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findById(departmentId);
        if (!departmentEntityOptional.isPresent()) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_EXISTS);
        }
        return departmentEntityOptional.get();
    }

    // 根据部门名称查询部门
    @Cacheable(value = "department_cache", key = "#departmenName")
    public DepartmentEntity getDepartmentByName(String departmenName) {
        if (StringUtils.isEmpty(departmenName)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_EXISTS);
        }
        Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findByName(departmenName);
        if (!departmentEntityOptional.isPresent()) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_EXISTS);
        }
        return departmentEntityOptional.get();
    }

    // 分页查询所有
    public Page<DepartmentEntity> getDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    // 根据名称判断部门是否存在
    public boolean hasDepartmentByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return departmentRepository.existsByName(name);
    }

    // 根据ID判断部门是否存在
    boolean hasDepartmentById(String id) {
        if (StringUtils.isEmpty(id)) {
            return false;
        }
        return departmentRepository.existsById(id);
    }

}
