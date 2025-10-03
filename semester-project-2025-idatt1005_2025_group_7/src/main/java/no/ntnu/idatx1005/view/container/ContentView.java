package no.ntnu.idatx1005.view.container;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx1005.view.content.DistributionView;
import no.ntnu.idatx1005.view.content.EditTaskView;
import no.ntnu.idatx1005.view.content.HeaderView;
import no.ntnu.idatx1005.view.content.NewTaskView;
import no.ntnu.idatx1005.view.content.SettingsView;

/**
 * <h3>View for the content area.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link MainView} class.
 *
 * @see VBox
 * @see MainView
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class ContentView extends VBox {
  private final HeaderView headerView;

  /**
   * Constructs a new content view.
   */
  public ContentView() {
    headerView = new HeaderView();
    initialize();
  }

  /**
   * Initializes the content view.
   */
  private void initialize() {
    this.setSpacing(20);
    VBox.setVgrow(this, Priority.ALWAYS);
  }

  /**
   * Sets the view to be displayed in the content view.
   *
   * @param view the view to show
   */
  public void showView(Node view) {
    updateHeaderView(view);
    this.getChildren().setAll(headerView, view);
  }

  /**
   * Updates the header view based on the view type.
   *
   * @param view the view to update the header view for
   */
  public void updateHeaderView(Node view) {
    switch (view) {
      case NewTaskView ignored -> headerView.showNewTaskViewHeader();
      case EditTaskView ignored -> headerView.showEditTaskViewHeader();
      case DistributionView ignored -> headerView.showDistributionViewHeader();
      case SettingsView ignored -> headerView.showSettingsViewHeader();
      default -> headerView.showMainViewHeader();
    }
  }

  /**
   * Returns the content view.
   *
   * @return the content view
   */
  public ContentView getView() {
    return this;
  }

  /**
   * Returns the header view.
   *
   * @return the header view
   */
  public HeaderView getHeaderView() {
    return headerView;
  }
}
