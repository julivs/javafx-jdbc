package gui;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Set<DataChangeListener> DCLList = new HashSet<>();
	
	private Department entity;
	
	private DepartmentService ds;

	@FXML
	private TextField textId;

	@FXML
	private TextField textName;

	@FXML
	private Button btInclude;

	@FXML
	private Button btCancel;

	@FXML
	private Label labelInform;

	public void setEntity(Department entity) {
		this.entity = entity;
	}
	
	public void setDs(DepartmentService ds) {
		this.ds = ds;
	}

	public void subscribeDCL(DataChangeListener dcl) {
		this.DCLList.add(dcl);
	}

	@FXML
	public void onbtIncludeAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Department is null.");
		}
		if (ds == null) {
			throw new IllegalStateException("Service is null.");
		}
		try {
			entity = getFormData();
			ds.saveOrUpdate(entity);
			notifyDCL();
			Utils.currentStage(event).close();
		}
		catch(Exception e) {
			throw new DbException(e.getMessage());
		}
	}

	private void notifyDCL() {
		for (DataChangeListener dcl : this.DCLList) {
			dcl.onDataChanged();
		}
	}

	private Department getFormData() {
		Integer id = Utils.tryToParseInt(textId.getText());
		String name = textName.getText();
		
		return new Department(id, name);
	}

	@FXML
	public void onbtCancelAction() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(textName, 30);
		Constraints.setTextFieldInteger(textId);
	}

	public void updateFormData() {
		if (entity != null) {
			textId.setText(String.valueOf(entity.getId()));
			textName.setText(entity.getName());
		}
		
	}


}
