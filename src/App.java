import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private Donnees donnees;

    @Override
    public void start(Stage primaryStage) {
        donnees = new Donnees();
        try {
            // Charger la scène de connexion et passer l'instance de Donnees au contrôleur de vue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConnexionScene.fxml"));
            loader.setControllerFactory(controller -> new ConnexionController(donnees));
            Parent root = loader.load();

            // Créer la scène et définir sur la fenêtre principale
            Scene scene = new Scene(root);
            primaryStage.setTitle("Début");
            primaryStage.setScene(scene);
            primaryStage.show();

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
