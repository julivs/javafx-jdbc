package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService ds = null;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, String> tableColumnEmail;

	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnDELETE;

	@FXML
	private Button btNew;

	@FXML
	public void onBtnewAction(ActionEvent event) {

		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event));
	}

	public void setSellerService(SellerService ds) {
		this.ds = ds;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {

		if (ds == null)
			throw new IllegalStateException("Server is null.");

		tableViewSeller.setItems(FXCollections.observableArrayList(ds.findAll()));
		initEditButtons();
		initDeleteButtons();

	}

	
	private void createDialogForm(Seller obj, String path, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setEntity(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.updateFormData();
			controller.subscribeDCL(this);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Error creating dialog", null, e.getMessage(), AlertType.ERROR);
		}

	}
	

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {

		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
					button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initDeleteButtons() {

		tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDELETE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("delete");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> deleteSeller(obj));
			}

		});
	}

	private Object deleteSeller(Seller obj) {
		
		boolean result = Alerts.showConfirm("Delete confirmation", null, "Delete department " + obj.getName() + "?");
		
		if (result) {
			try {
				
				ds.delete(obj);
				updateTableView();
				
			} catch (DbIntegrityException e) {
				
				Alerts.showAlert("Error deletion", null, "This department can't be deleted.", AlertType.ERROR);

			}

		}
		return null;
	}

}
