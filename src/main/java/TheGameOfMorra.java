//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.FontPosture;
//import javafx.scene.text.FontWeight;
//import javafx.stage.Stage;
//import  javafx.scene.control.Label;
//import javafx.scene.control.Button;
//import	javafx.scene.image.Image;
//import javafx.geometry.Insets;
//import javafx.scene.control.TextField;
//import javafx.scene.text.Font;
//import java.awt.*;
//
//public class TheGameOfMorra extends Application {
//
//	Server serverConnection;
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		launch(args);
//	}
//
//	//feel free to remove the starter code from this method
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		// TODO Auto-generated method stub
//		primaryStage.setTitle("(Server) Let's Play Morra!!!");
//		HBox hBox = new HBox();
//		BorderPane pane = new BorderPane();
//		Label port = new Label("Port");
////		TextField ipNum = new TextField();
////		Label ip = new Label("IP Address");
//		TextField portNum = new TextField();
//		Button connectBtn = new Button("Connect");
//
//
//		hBox.getChildren().addAll(port, portNum, connectBtn);
//		hBox.setSpacing(10);
//		hBox.setPadding(new Insets(20,0,0,0));
//		port.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 20));
////		ip.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 20));
////		ip.setTextFill(Color.WHITESMOKE);
//		port.setTextFill(Color.WHITESMOKE);
//
//		connectBtn.setOnAction(event -> System.out.println("we are in port " + portNum.getText()));
//
//
//		hBox.setAlignment(Pos.TOP_CENTER);
//		pane.setStyle("-fx-background-color: green;");
//		pane.setCenter(hBox);
//
//
//
//		Scene scene = new Scene(pane,600,600);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
//
//}






import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TheGameOfMorra extends Application{


	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;

	ListView<String> serverOutput;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");

		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");

		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> {
				Platform.runLater(()->{
					serverOutput.getItems().add(data.toString());
				});

			});

		});




		this.buttonBox = new HBox(400, serverChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);

		startScene = new Scene(startPane, 800,800);

		serverOutput = new ListView<String>();


		c1 = new TextField();
		b1 = new Button("Send");


		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});



		primaryStage.setScene(startScene);
		primaryStage.show();

	}

	public Scene createServerGui() {

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(serverOutput);

		return new Scene(pane, 500, 400);


	}



}
