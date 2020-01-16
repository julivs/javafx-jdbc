package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dd = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll() {
		
		return dd.findAll();
		
	}
	
	public void saveOrUpdate(Department department) {
		if (department.getId() == null) {
			dd.insert(department);
		}
		else {
			dd.update(department);
		}
	}
	
	public void delete(Department department) {
		if (department.getId() != null) {
			dd.deleteById(department.getId());
		}
	}

}
