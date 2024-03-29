package io.github.fvarrui.reviser.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.model.Criterion;
import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.ui.utils.Dialogs;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;

public class DesignerController implements Initializable {

	// model

	private StringProperty name = new SimpleStringProperty();
	private StringProperty weight = new SimpleStringProperty();
	private ObjectProperty<Exercise> exercise = new SimpleObjectProperty<>();

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TableView<Criterion> formTable;

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

	public DesignerController() {
		try { 
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DesignerView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		exercise.addListener((o, ov, nv) -> onExerciseChanged(o, ov, nv));

		nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
		weightColumn.setCellValueFactory(v -> v.getValue().weightProperty());

		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));

		nameColumn.prefWidthProperty().bind(formTable.widthProperty().multiply(0.80));
		weightColumn.prefWidthProperty().bind(formTable.widthProperty().multiply(0.15));

		formTable.getSelectionModel().setCellSelectionEnabled(true);

		addButton.disableProperty().bind(name.isEmpty().or(weight.isEmpty()));
		removeButton.disableProperty().bind(formTable.getSelectionModel().selectedItemProperty().isNull());

		name.bindBidirectional(nameText.textProperty());
		weight.bindBidirectional(weightText.textProperty());
	}

	private void onExerciseChanged(ObservableValue<? extends Exercise> o, Exercise ov, Exercise nv) {
		if (ov != null) {
			formTable.itemsProperty().unbind();
			formTable.itemsProperty().get().clear();
		}
		if (nv != null) {
			formTable.itemsProperty().bind(nv.getForm().criteriaProperty());
		}
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
		exercise.get().getForm().getCriteria().add(criterion);

		this.name.set("");
		this.weight.set("");

	}

	@FXML
	private void onRemoveCriterion(ActionEvent e) {

		exercise.get().getForm().getCriteria().remove(formTable.getSelectionModel().getSelectedItem());

	}

	private Long getNewId() {
		Optional<Long> max = exercise.get().getForm().getCriteria().stream().map(c -> c.getId()).max(Long::compare);
		return max.isPresent() ? max.get() + 1 : 1;
	}

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<Exercise> exerciseProperty() {
		return this.exercise;
	}
	

	public final Exercise getExercise() {
		return this.exerciseProperty().get();
	}
	

	public final void setExercise(final Exercise exercise) {
		this.exerciseProperty().set(exercise);
	}

}
