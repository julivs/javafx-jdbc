package gui;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.exceptions.ValidationException;
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
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving department", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDCL() {
		for (DataChangeListener dcl : this.DCLList) {
			dcl.onDataChanged();
		}
	}

	private Department getFormData() {
		
		ValidationException ve = new ValidationException("Validation exception");
		
		Integer id = Utils.tryToParseInt(textId.getText());
		
		if (textName.getText() == null || textName.getText().trim().equals("")) {
			ve.addError("name", "Name can't be empty.");
		}
		String name = textName.getText();
		
		if (ve.getErrors().size() > 0) throw ve;
		
		return new Department(id, name);
	}

	@FXML
	public void onbtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
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
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> errorsKey = errors.keySet();
		
		if (errorsKey.contains("name")) {
			labelInform.setText(errors.get("name"));
		}
		
	}


}
