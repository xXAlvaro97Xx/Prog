
package vistacontrolador;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.AlumnoNoExistenteExcepcion;
import modelo.CorrectorProyectos;
import modelo.Proyecto;

public class GuiCorrectorProyectos extends Application {

	private MenuItem itemLeer;
	private MenuItem itemGuardar;
	private MenuItem itemSalir;

	private TextField txtAlumno;
	private Button btnVerProyecto;

	private RadioButton rbtAprobados;
	private RadioButton rbtOrdenados;
	private Button btnMostrar;

	private TextArea areaTexto;

	private Button btnClear;
	private Button btnSalir;

	private CorrectorProyectos corrector;

	@Override
	public void start(Stage stage) {

		corrector = new CorrectorProyectos();

		BorderPane root = crearGui();

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("- Corrector de proyectos -");
		scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		stage.show();
	}

	private BorderPane crearGui() {

		BorderPane panel = new BorderPane();
		MenuBar barraMenu = crearBarraMenu();
		panel.setTop(barraMenu);

		VBox panelPrincipal = crearPanelPrincipal();
		panel.setCenter(panelPrincipal);

		HBox panelBotones = crearPanelBotones();
		panel.setBottom(panelBotones);

		return panel;
	}

	private MenuBar crearBarraMenu() {

		MenuBar barraMenu = new MenuBar();

		Menu menu = new Menu("Archivo");

		itemLeer = new MenuItem("_Leer de fichero");
		itemLeer.setAccelerator(KeyCombination.keyCombination("CTRL+L"));
		itemLeer.setOnAction(e -> leerDeFichero());

		itemGuardar = new MenuItem("_Guardar en fichero");
		itemGuardar.setDisable(true);
		itemGuardar.setAccelerator(KeyCombination.keyCombination("CTRL+G"));
		itemGuardar.setOnAction(e -> salvarEnFichero());

		itemSalir = new MenuItem("_Salir");
		itemSalir.setAccelerator(KeyCombination.keyCombination("CTRL+S"));
		menu.getItems().addAll(itemLeer, itemGuardar, new SeparatorMenuItem(), itemSalir);
		itemSalir.setOnAction(e -> salir());

		barraMenu.getMenus().add(menu);
		return barraMenu;
	}

	private VBox crearPanelPrincipal() {

		VBox panel = new VBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);

		Label lblOpciones = new Label("Panel de opciones");
		lblOpciones.setAlignment(Pos.CENTER_LEFT);
		lblOpciones.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		lblOpciones.getStyleClass().add("titulo-panel");

		Label lblEntrada = new Label("Panel de entrada");
		lblEntrada.setAlignment(Pos.CENTER_LEFT);
		lblEntrada.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		lblEntrada.getStyleClass().add("titulo-panel");

		areaTexto = new TextArea();
		areaTexto.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		panel.getChildren().addAll(lblEntrada, crearPanelEntrada(), lblOpciones, crearPanelOpciones(), areaTexto);

		return panel;
	}

	private HBox crearPanelEntrada() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);

		Label lblAlumno = new Label("Alumno");
		txtAlumno = new TextField();
		txtAlumno.setPrefColumnCount(30);
		txtAlumno.setOnAction(e -> verProyecto());

		btnVerProyecto = new Button("Ver proyecto");
		btnVerProyecto.setOnAction(e -> verProyecto());

		panel.getChildren().addAll(lblAlumno, txtAlumno, btnVerProyecto);

		return panel;
	}

	private HBox crearPanelOpciones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(50);
		panel.setAlignment(Pos.CENTER);
		rbtAprobados = new RadioButton("Mostrar aprobados");
		rbtAprobados.setSelected(true);

		rbtOrdenados = new RadioButton("Mostrar ordenados");
		ToggleGroup tg = new ToggleGroup();
		rbtAprobados.setToggleGroup(tg);
		rbtOrdenados.setToggleGroup(tg);
		btnMostrar = new Button("Mostrar");
		btnMostrar.setOnAction(e -> mostrar());

		panel.getChildren().addAll(rbtAprobados, rbtOrdenados, btnMostrar);
		return panel;
	}

	private HBox crearPanelBotones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		panel.setAlignment(Pos.BOTTOM_RIGHT);
		btnClear = new Button("Clear");
		btnClear.setOnAction(e -> clear());
		btnClear.setPrefWidth(90);

		btnSalir = new Button("Salir");
		btnSalir.setOnAction(e -> salir());
		btnSalir.setPrefWidth(90);

		panel.getChildren().addAll(btnClear, btnSalir);

		return panel;
	}

	private void salvarEnFichero() {
		try {
			corrector.guardarOrdenadosPorNota();
		} catch (IOException e) {
			areaTexto.setText(e.getMessage());
		}
	}

	private void leerDeFichero() {
		corrector.leerDatosProyectos();
		itemLeer.setDisable(true);
		itemGuardar.setDisable(false);
		areaTexto.setText("Leído fichero de texto\n\n" + corrector.getErrores()
				+ "\n\nYa están guardados en memoria los datos de los proyectos");
	}

	private void verProyecto() {
		if (itemLeer.isDisable()) {
			if (txtAlumno.getText().equals("")) {
				areaTexto.setText("No se ha escrito ningun alumno");
			} else {
				try {
					areaTexto.setText(corrector.proyectoDe(txtAlumno.getText()).toString());
				} catch (AlumnoNoExistenteExcepcion e) {
					areaTexto.setText(e.getMessage());
				}
			}
		} else {
			areaTexto.setText("No se han leído todavía los datos del fichero\nVaya a la opción leer del menú");
		}
		cogerFoco();
	}

	private void mostrar() {

		clear();
		if (itemLeer.isDisable()) {
			if (rbtAprobados.isSelected()) {
				areaTexto.setText("Han aprobado el proyecto " + String.valueOf(corrector.aprobados()) + " alumnos/as");
			} else {
//				areaTexto.setText(corrector.ordenadosPorNota().toString());
				List<Entry<String, Proyecto>> lista = corrector.ordenadosPorNota();
				for (Entry<String, Proyecto> entrada : lista) {
					String alu = String.format("%20s:\n", entrada.getKey());
					Proyecto pro = entrada.getValue();
					String a = areaTexto.getText();
					areaTexto.setText(a + alu + pro.toString());
				}
			}
		} else {
			areaTexto.setText("No se han leído todavía los datos del fichero\nVaya a la opción leer del menú");
		}
	}

	private void cogerFoco() {

		txtAlumno.requestFocus();
		txtAlumno.selectAll();

	}

	private void salir() {

		System.exit(0);
	}

	private void clear() {

		areaTexto.clear();
		cogerFoco();
	}

	public static void main(String[] args) {

		launch(args);
	}
}
