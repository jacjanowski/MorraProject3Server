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






import java.awt.*;
import java.util.HashMap;

import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TheGameOfMorra extends Application{


	TextField s1,s2,s3,s4, c1, portNum;
	int numberOfClients = 0;
	int portNumber;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	MorraInfo gameInfo;


	ListView<String> serverOutput;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");

		gameInfo = new MorraInfo();

		this.serverChoice = new Button("Server");
		this.serverChoice.setMinSize(300,150);

		this.serverChoice.setOnAction(e->{

			if(this.portNum.getText().isEmpty())
			{
				Label warning = new Label("Please enter a port number");
				warning.setTextFill(Color.RED);
				warning.setFont(new Font("Arial", 30));
				HBox warningBox = new HBox(100, warning);
				//warning.setPadding(new Insets(0,0,0,200));
				//warning.setTextFill(Color.RED);
				warningBox.setAlignment(Pos.CENTER);
				startPane.setBottom(warningBox);
				return;//FIXME
			}
			this.portNumber = Integer.parseInt(this.portNum.getText());
//			System.out.println("Port number: " + this.portNumber);

			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> {
				Platform.runLater(()->{
					serverOutput.getItems().add(data.toString());
				});

			});

		});



		//Create label and textbox for entering port number to listen to
		Label label1 = new Label("Port Number:");
		label1.setFont(new Font("Arial", 20));
		label1.setTextFill(Color.RED);
		this.portNum = new TextField();
		HBox portBox = new HBox();
		portBox.getChildren().addAll(label1, this.portNum);
		portBox.setSpacing(10);

		//Create hbox for our server ON button
		this.buttonBox = new HBox(400, serverChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		startPane.setRight(portBox);

		Image serverMainBG = new Image("serverMainBG.jpg");

		startPane.setBackground(new Background(new BackgroundImage(serverMainBG, BackgroundRepeat.REPEAT
				,BackgroundRepeat.REPEAT
				,BackgroundPosition.DEFAULT
				,BackgroundSize.DEFAULT)));

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
		pane.setCenter(serverOutput);

		Image serverBG = new Image("serverBG.jpg");

		pane.setBackground(new Background(new BackgroundImage(serverBG, BackgroundRepeat.REPEAT
																	,BackgroundRepeat.REPEAT
																	,BackgroundPosition.DEFAULT
																	,BackgroundSize.DEFAULT)));
		return new Scene(pane, 500, 400);

	}



}
