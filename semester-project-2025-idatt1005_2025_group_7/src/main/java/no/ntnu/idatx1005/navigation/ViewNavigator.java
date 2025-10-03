package no.ntnu.idatx1005.navigation;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import javafx.scene.Node;
import no.ntnu.idatx1005.view.container.ContentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>ViewNavigator class</h3>
 *
 * <p>This class handles navigation between different views in the application. It also maintains
 * navigation history to allow for features like a "back" button.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class ViewNavigator {
  private static final Logger logger = LoggerFactory.getLogger(ViewNavigator.class);
  private final Stack<NavigationState> navigationStack = new Stack<>();
  private final ContentView contentView;
  private final ViewFactory viewFactory;

  /**
   * Constructs a new ViewNavigator.
   *
   * @param contentView the content view
   * @param viewFactory the view factory
   */
  public ViewNavigator(ContentView contentView, ViewFactory viewFactory) {
    this.contentView = contentView;
    this.viewFactory = viewFactory;
    logger.debug("ViewNavigator initialized");
  }

  /**
   * Navigate to a new view.
   *
   * @param viewType the type of view to navigate to
   * @param params parameters needed for the view
   */
  public void navigateTo(ViewType viewType, Map<String, Object> params) {
    Node view = viewFactory.createView(viewType, params);
    navigationStack.push(new NavigationState(viewType, params));
    contentView.showView(view);
    logger.info("Navigated to view: {} with params: {}", viewType, params);
    logger.debug("Navigation stack size: {}", navigationStack.size());
  }

  /**
   * Navigate to a new view with no parameters.
   *
   * @param viewType the type of view to navigate to
   */
  public void navigateTo(ViewType viewType) {
    navigateTo(viewType, Collections.emptyMap());
  }

  /**
   * Go back to the previous view.
   */
  public void goBack() {
    if (navigationStack.size() > 1) {
      logger.info("Going back to previous view");
      navigationStack.pop();
      NavigationState previous = navigationStack.peek();
      logger.debug("Previous view: {} with params: {}", previous.viewType, previous.params);
      Node view = viewFactory.createView(previous.viewType, previous.params);
      contentView.showView(view);
    } else {
      logger.debug("Cannot go back, stack size is {}", navigationStack.size());
    }
  }

  /**
   * Clear navigation history and set the current view.
   *
   * @param viewType the type of view to set as current
   * @param params parameters needed for the view
   */
  public void resetToView(ViewType viewType, Map<String, Object> params) {
    resetNavHistory();
    navigateTo(viewType, params);
    logger.info("Reset to view: {}", viewType);
  }

  /**
   * Clear navigation history.
   */
  public void resetNavHistory() {
    logger.debug("Clearing navigation history");
    navigationStack.clear();
  }

  /**
   * Get the current navigation state.
   *
   * @return the current NavigationState
   */
  public NavigationState getCurrentState() {
    return navigationStack.isEmpty() ? null : navigationStack.peek();
  }

  /**
   * <h3>NavigationState record</h3>
   *
   * <p>Represents a navigation state with view type and parameters.
   *
   * @author William Holtsdalen
   * @since V1.1.0
   */
  public record NavigationState(ViewType viewType, Map<String, Object> params) {
    
    /**
     * Constructs a new NavigationState.
     *
     * @param viewType the view type
     * @param params the parameters
     */
    public NavigationState(ViewType viewType, Map<String, Object> params) {
      this.viewType = viewType;
      this.params = params != null ? params : Collections.emptyMap();
    }
  }
} 