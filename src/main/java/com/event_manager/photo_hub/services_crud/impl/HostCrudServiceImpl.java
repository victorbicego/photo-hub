package com.event_manager.photo_hub.services_crud.impl;

import com.white_label.core.exceptions.NotFoundException;
import com.white_label.core.models.entities.Employee;
import com.white_label.core.repositories.EmployeeRepository;
import com.white_label.core.sevices_crud.EmployeeCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostCrudServiceImpl implements EmployeeCrudService {

  private final EmployeeRepository employeeRepository;

  @Override
  public Page<Employee> findAllByFilter(String search, Pageable pageable) {
    return employeeRepository.findBySearchTerm(search, pageable);
  }

  @Override
  public Employee findById(Long id) throws NotFoundException {
    return employeeRepository
        .findEmployeeById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("No employee found with id: '%d'.", id)));
  }

  @Override
  public Employee findByUsername(String username) throws NotFoundException {
    return employeeRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("No employee found with username: '%s'.", username)));
  }

  @Override
  public Employee save(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Override
  public Employee updatePassword(Long id, String encodedPassword) throws NotFoundException {
    Employee foundEmployee = findById(id);
    foundEmployee.setPassword(encodedPassword);
    return employeeRepository.save(foundEmployee);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    if (!employeeRepository.existsById(id)) {
      throw new NotFoundException(String.format("No employee found with id: '%d'.", id));
    }
    employeeRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return employeeRepository.existsById(id);
  }

  @Override
  public int countAdmins() {
    return employeeRepository.countAdmins();
  }
}
