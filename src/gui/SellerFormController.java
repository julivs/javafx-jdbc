package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Set<DataChangeListener> DCLList = new HashSet<>();

	private Seller entity;

	private SellerService sellerService;

	private DepartmentService departmentService;

	@FXML
	private TextField textId;

	@FXML
	private TextField textName;

	@FXML
	private TextField textEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField textBaseSalary;

	@FXML
	ComboBox<Department> comboBoxDepartment;

	@FXML
	private Button btInclude;

	@FXML
	private Button btCancel;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	private ObservableList<Department> obsList;

	public void setEntity(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	public void subscribeDCL(DataChangeListener dcl) {
		this.DCLList.add(dcl);
	}

	@FXML
	public void onbtIncludeAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Seller is null.");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Service is null.");
		}
		try {
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			notifyDCL();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving department", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDCL() {
		for (DataChangeListener dcl : this.DCLList) {
			dcl.onDataChanged();
		}
	}

	private Seller getFormData() {
		
		Seller seller = new Seller();
		

		ValidationException ve = new ValidationException("Validation exception");
		
		seller.setId(Utils.tryToParseInt(textId.getText()));

		if (textName.getText() == null || textName.getText().trim().equals("")) {
			ve.addError("name", "Name can't be empty.");
		}
		
		seller.setName(textName.getText());
		
		if (textEmail.getText() == null || textEmail.getText().trim().equals("")) {
			ve.addError("email", "Email can't be empty.");
		}
		
		seller.setEmail(textEmail.getText());
		
		if (dpBirthDate.getValue() != null) {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			seller.setBirthDate(Date.from(instant));
		}
		else {
			ve.addError("birthDate", "Birth date can't be empty.");
		}
		
		
		if (textBaseSalary.getText() == null || textBaseSalary.getText().trim().equals("")) {
			ve.addError("baseSalary", "Base salary can't be empty.");
		}
		
		seller.setBaseSalary(Utils.tryToParseDouble(textBaseSalary.getText()));
		
		seller.setDepartment(comboBoxDepartment.getValue());
	
		
		System.out.println(ve.getErrors().size());

		if (ve.getErrors().size() > 0)
			throw ve;

		return seller;
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
		Constraints.setTextFieldDouble(textBaseSalary);
		Constraints.setTextFieldMaxLength(textEmail, 70);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity != null) {
			textId.setText(String.valueOf(entity.getId()));
			textName.setText(entity.getName());
			textEmail.setText(entity.getEmail());
			Locale.setDefault(Locale.US);
			textBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
			if (entity.getBirthDate() != null) {
				dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
			}
			if (entity.getDepartment() == null) {
				comboBoxDepartment.getSelectionModel().selectFirst();
			}
			else {
				comboBoxDepartment.setValue(entity.getDepartment());				
			}
			
		}

	}

	public void loadAssociatedObjects() {
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> errorsKey = errors.keySet();

		labelErrorName.setText(errorsKey.contains("name")?errors.get("name"):"");
		labelErrorEmail.setText(errorsKey.contains("email")?errors.get("email"):"");
		labelErrorBaseSalary.setText(errorsKey.contains("baseSalary")?errors.get("baseSalary"):"");
		labelErrorBirthDate.setText(errorsKey.contains("birthDate")?errors.get("birthDate"):"");
		
	}

}
