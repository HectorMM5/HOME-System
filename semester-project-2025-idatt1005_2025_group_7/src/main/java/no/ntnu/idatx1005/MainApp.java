package no.ntnu.idatx1005;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.navigation.ViewFactory;
import no.ntnu.idatx1005.navigation.ViewNavigator;
import no.ntnu.idatx1005.observer.ButtonClickHandler;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.storage.DatabaseRefresher;
import no.ntnu.idatx1005.view.container.LoginView;
import no.ntnu.idatx1005.view.container.MainView;
import no.ntnu.idatx1005.view.container.SignUpView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Main application class for the application.</h3>
 *
 * <p>The main application class extends the {@link Application} class. It is responsible for
 * initializing the database connection and creating the main view, as well as showing the
 * appropriate views based on user input.
 *
 * @see Application
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class MainApp extends Application {
  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
  private Stage primaryStage;
  private BorderPane root;
  private MainView mainView;
  private LoginView loginView;
  private SignUpView signUpView;
  private TaskEventManager taskManager;
  private Map<Class<?>, Object> viewControllers;
  private User loggedInUser;

  /**
   * Main method for the application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Starts the application.
   *
   * @param primaryStage the primary stage
   */
  @Override
  public void start(Stage primaryStage) {
    logger.info("Starting H.O.M.E. Application");
    this.mainView = new MainView();
    this.loginView = new LoginView();
    this.signUpView = new SignUpView();

    this.primaryStage = primaryStage;
    this.taskManager = new TaskEventManager(DatabaseService.getInstance());
    this.viewControllers = new HashMap<>();

    DatabaseRefresher.getInstance().start();

    initializeViews();
    setupStage();
    logger.info("Application startup completed");
  }

  /**
   * Returns the task manager.
   *
   * @return the task manager
   */
  public TaskEventManager getTaskManager() {
    return taskManager;
  }

  /**
   * Returns the root of the application.
   *
   * @return the root of the application
   */
  public BorderPane getRoot() {
    return root;
  }

  /**
   * Returns the main view.
   *
   * @return the main view
   */
  public MainView getMainView() {
    return mainView;
  }

  /**
   * Returns the login view.
   *
   * @return the login view
   */
  public LoginView getLoginView() {
    return loginView;
  }

  /**
   * Returns the sign up view.
   *
   * @return the sign up view
   */
  public SignUpView getSignUpView() {
    return signUpView;
  }

  /**
   * Returns the logged in user.
   *
   * @return the logged in user
   */
  public User getUser() {
    return loggedInUser;
  }

  /**
   * Sets the logged in user.
   *
   * @param user the user to set
   */
  public void setUser(User user) {
    loggedInUser = user;
    taskManager.setLoggedInUser(user);
  }

  /**
   * Initializes the factory and navigator for navigation between views.
   */
  private void initializeViews() {
    logger.debug("Initializing views and navigation");
    ViewFactory viewFactory = new ViewFactory(this);

    loginView = (LoginView) viewFactory.createLoginView();
    signUpView = (SignUpView) viewFactory.createSignUpView();

    ViewNavigator viewNavigator = new ViewNavigator(mainView.getContentView(), viewFactory);
    ButtonClickHandler buttonClickHandler = new ButtonClickHandler(viewNavigator, this);
    viewFactory.addButtonClickObservers(List.of(buttonClickHandler));
    initializeButtonClickObserver(buttonClickHandler);
    logger.debug("Views initialization completed");
  }

  /**
   * Initializes the given button click observer.
   *
   * @param observer the observer to initialize
   */
  public void initializeButtonClickObserver(ButtonClickObserver observer) {
    // Remove old controllers (observers) if they exist, so they don't stick around
    // and get notified of events that new controllers should be notified of instead.
    if (viewControllers.containsKey(observer.getClass())) {
      logger.debug("Removing old controller: {}", observer.getClass().getSimpleName());
      ButtonClickObserver oldObserver = (ButtonClickObserver) viewControllers.get(
          observer.getClass());
      mainView.getContentView().getHeaderView().removeObserver(oldObserver);
      mainView.getSidebarView().removeObserver(oldObserver);
      loginView.removeObserver(oldObserver);
      signUpView.removeObserver(oldObserver);
    }
    logger.debug("Adding new controller: {}", observer.getClass().getSimpleName());
    viewControllers.put(observer.getClass(), observer);

    mainView.getContentView().getHeaderView().addObserver(observer);
    mainView.getSidebarView().addObserver(observer);
    loginView.addObserver(observer);
    signUpView.addObserver(observer);
  }

  /**
   * Sets up the primary stage and the root of the application. Also sets the title of the
   * primary stage, the scene and the scene's stylesheet.
   */
  private void setupStage() {
    logger.debug("Setting up primary stage");
    root = new BorderPane();
    root.setCenter(getLoginView());

    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add("stylesheets/styles.css");

    primaryStage.setTitle("H.O.M.E. Application");
    primaryStage.setScene(scene);
    primaryStage.show();
    logger.debug("Primary stage setup completed");
  }
}