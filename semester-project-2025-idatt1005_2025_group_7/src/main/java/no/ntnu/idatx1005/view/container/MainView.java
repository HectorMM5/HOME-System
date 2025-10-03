package no.ntnu.idatx1005.view.container;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx1005.MainApp;
import no.ntnu.idatx1005.view.content.SidebarView;

/**
 * <h3>View for the main view.</h3>
 *
 * <p>The view extends the {@link HBox} class. The view is meant to be used as a child of the
 * {@link MainApp} class.
 *
 * @see HBox
 * @see MainApp
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class MainView extends HBox {
  private final ContentView contentView;
  private final SidebarView sidebarView;

  /**
   * Constructs a new main view. 
   */
  public MainView() {
    this.contentView = new ContentView();
    this.sidebarView = new SidebarView();

    this.setPadding(new Insets(20));
    this.setSpacing(20);

    initialize();
  }

  /**
   * Initializes the main view.
   */
  private void initialize() {
    VBox mainContent = new VBox(contentView.getView());
    mainContent.setSpacing(20);
    HBox.setHgrow(mainContent, Priority.ALWAYS);

    this.getChildren().setAll(sidebarView.getView(), mainContent);
  }

  /**
   * Returns the view of the main view.
   *
   * @return the view of the main view
   */
  public MainView getView() {
    return this;
  }

  /**
   * Returns the content view of the main view.
   *
   * @return the content view of the main view
   */
  public ContentView getContentView() {
    return contentView;
  }

  /**
   * Returns the sidebar view of the main view.
   *
   * @return the sidebar view of the main view
   */
  public SidebarView getSidebarView() {
    return sidebarView;
  }
}
