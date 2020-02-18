package fvarrui.reviser.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import fvarrui.reviser.model.Criterion;
import fvarrui.reviser.model.GradingForm;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class FormDesignerController implements Initializable {

	// model

	private StringProperty name = new SimpleStringProperty();
	private StringProperty weight = new SimpleStringProperty();
	private ObjectProperty<GradingForm> form = new SimpleObjectProperty<>();

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TableView<Criterion> formTable;

	@FXML
	private TableColumn<Criterion, Number> idColumn;

	@FXML
	private TableColumn<Criterion, String> nameColumn;

	@FXML
	private TableColumn<Criterion, Number> weightColumn;

	@FXML
	private TextField nameText;

	@FXML
	private TextField weightText;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	public FormDesignerController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormDesignerView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		form.addListener((o, ov, nv) -> onFormChanged(o, ov, nv));

		idColumn.setCellValueFactory(v -> v.getValue().idProperty());
		nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
		weightColumn.setCellValueFactory(v -> v.getValue().weightProperty());

		idColumn.prefWidthProperty().bind(formTable.widthProperty().multiply(0.10));
		nameColumn.prefWidthProperty().bind(formTable.widthProperty().multiply(0.80));
		weightColumn.prefWidthProperty().bind(formTable.widthProperty().multiply(0.10));

		addButton.disableProperty().bind(name.isEmpty().or(weight.isEmpty()));
		removeButton.disableProperty().bind(formTable.getSelectionModel().selectedItemProperty().isNull());
		
		name.bindBidirectional(nameText.textProperty());
		weight.bindBidirectional(weightText.textProperty());
	}

	private void onFormChanged(ObservableValue<? extends GradingForm> o, GradingForm ov, GradingForm nv) {
		formTable.itemsProperty().bind(form.get().criteriaProperty());		
	}

	@FXML
	private void onAddCriterion(ActionEvent e) {
		
		if (name.get().trim().isEmpty()) {
			Dialogs.error("Campo vacío", "Debe especificar el nombre");
			Platform.runLater(nameText::requestFocus);
			return;
		}

		if (this.weight.get().trim().isEmpty()) {
			Dialogs.error("Campo vacío", "Debe especificar el peso");
			Platform.runLater(weightText::requestFocus);
			return;
		}

		double weight = 0.0;
		try { 
			weight = Double.parseDouble(this.weight.get());
		} catch (NumberFormatException e1) {
			Dialogs.error("Valor no válido", "El peso debe ser un número");
			Platform.runLater(weightText::requestFocus);
			return;
		}
		
		Criterion criterion = new Criterion();
		criterion.setId(getNewId());
		criterion.setName(name.get());
		criterion.setWeight(weight);
		form.get().getCriteria().add(criterion);
		
		this.name.set("");
		this.weight.set("");
		
	}

	@FXML
	private void onRemoveCriterion(ActionEvent e) {
		
		form.get().getCriteria().remove(formTable.getSelectionModel().getSelectedItem());
		
	}
	
	private Long getNewId() {
		Optional<Long> max = form.get().getCriteria().stream().map(c -> c.getId()).max(Long::compare);
		return max.isPresent() ? max.get() + 1 : 1;
	}

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<GradingForm> formProperty() {
		return this.form;
	}

	public final GradingForm getForm() {
		return this.formProperty().get();
	}

	public final void setForm(final GradingForm form) {
		this.formProperty().set(form);
	}

}
