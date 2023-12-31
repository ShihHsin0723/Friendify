package interface_adapter.logged_in;

import interface_adapter.ViewModel;
import interface_adapter.login.LoginState;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Represents the state and functionality of being logged in, with corresponding buttons to other features that a user can
 */
public class LoggedInViewModel extends ViewModel {
    public final String TITLE_LABEL = "Logged In View";

    private LoggedInState state = new LoggedInState();

    public static final String LOGOUT_BUTTON_LABEL = "Log out";
    public static final String REQUESTS_BUTTON_LABEL = "My Requests";
    public static final String FRIENDS_BUTTON_LABEL = "My Friends";
    public static final String MATCH_BUTTON_LABEL = "Match";
    public static final String EDIT_PROFILE_BUTTON_LABEL = "Edit Profile";

    public LoggedInViewModel() {
        super("logged in");
    }

    public void setState(LoggedInState state) {
        this.state = state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // This is what the Login Presenter will call to let the ViewModel know
    // to alert the View
    public void firePropertyChanged() {
        support.firePropertyChange("loggedInState", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public LoggedInState getState() {
        return state;
    }
}
