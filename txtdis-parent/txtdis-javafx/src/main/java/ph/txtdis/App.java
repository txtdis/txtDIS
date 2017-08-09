package ph.txtdis;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import ph.txtdis.fx.dialog.LoginDialog;
import ph.txtdis.util.FontIcon;

@SpringBootApplication
@PropertySource("server.properties")
@PropertySource("application.properties")
public class App
	extends Application {

	private HBox splash;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void init() throws Exception {
		createSplash();
	}

	private void createSplash() {
		splash = new HBox(phoneInsideSpinningBallLogo(), textPane());
		splash.setAlignment(Pos.CENTER);
		splash.setPadding(new Insets(35, 35, 35, 35));
		splash.setStyle("-fx-base: slateblue; -fx-background-radius: 0.5em; ");
	}

	private Node phoneInsideSpinningBallLogo() {
		return new StackPane(phoneLogo(), spinningBalls());
	}

	private Parent textPane() {
		VBox hb = new VBox(trademark(), message());
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(0, 0, 0, 50));
		hb.setStyle("-fx-background: transparent;");
		return hb;
	}

	private Node phoneLogo() {
		Label p = new Label("\ue945");
		p.setStyle("-fx-font: 72 'txtdis'; -fx-text-fill: midnightblue;");
		p.setPadding(new Insets(10));
		return p;
	}

	private Node spinningBalls() {
		ProgressIndicator pi = new ProgressIndicator();
		pi.setScaleX(1.5);
		pi.setScaleY(1.5);
		pi.setStyle(" -fx-accent: white;");
		return pi;
	}

	private Node trademark() {
		Font.loadFont(this.getClass().getResourceAsStream("/font/Ubuntu-BI.ttf"), 24);
		Label tm = new Label("txtDIS");
		tm.setStyle("-fx-font: 48pt 'ubuntu'; -fx-text-fill: midnightblue;");
		tm.setAlignment(Pos.TOP_CENTER);
		return tm;
	}

	private Node message() {
		Label l = new Label("Please wait...");
		l.setStyle("-fx-font: 12pt 'ubuntu'; ");
		return l;
	}

	@Override
	public void start(Stage stage) throws Exception {
		Task<ApplicationContext> task = initApp();
		setSplashToFadeOnInitCompletion(stage, task, () -> showLoginDialog(task.getValue()));
		showSplash(stage);
		new Thread(task).start();
	}

	private Task<ApplicationContext> initApp() {
		return new Task<ApplicationContext>() {
			@Override
			protected ApplicationContext call() throws InterruptedException {
				return SpringApplication.run(App.class);
			}
		};
	}

	private void setSplashToFadeOnInitCompletion(Stage stage, Task<?> task, Init init) {
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				fadeSplash(stage);
				init.complete();
			}
		});
	}

	private void showLoginDialog(ApplicationContext context) {
		LoginDialog ld = context.getBean(LoginDialog.class);
		ld.showAndWait();
	}

	private void showSplash(Stage stage) {
		stage.setTitle("Starting txtDIS...");
		stage.getIcons().add(new FontIcon("\ue945"));
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene());
		stage.show();
	}

	private void fadeSplash(Stage stage) {
		FadeTransition ft = new FadeTransition(Duration.seconds(1.2), splash);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setOnFinished(e -> stage.hide());
		ft.play();
	}

	private Scene scene() {
		Scene s = new Scene(splash);
		s.setFill(Color.TRANSPARENT);
		return s;
	}

	private interface Init {
		void complete();
	}
}
