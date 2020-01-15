package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService ds = null;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;

	@FXML
	public void onBtnewAction(ActionEvent event) {
		
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event));
	}

	public void setDepartmentService(DepartmentService ds) {
		this.ds = ds;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {

		if (ds != null)
			tableViewDepartment.setItems(FXCollections.observableArrayList(ds.findAll()));

	}
	
	private void createDialogForm(Department obj, String path, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setEntity(obj);
			controller.setDs(new DepartmentService());
			controller.updateFormData();
			controller.subscribeDCL(this);
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch(Exception e) {
			Alerts.showAlert("Error creating dialog", null, e.getMessage(), AlertType.ERROR);
		}
		
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

}
