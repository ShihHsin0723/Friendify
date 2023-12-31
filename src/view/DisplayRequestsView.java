package view;

import interface_adapter.accept_request.AcceptRequestController;
import interface_adapter.accept_request.AcceptRequestState;
import interface_adapter.accept_request.AcceptRequestViewModel;
import interface_adapter.display_profile.DisplayProfileController;
import interface_adapter.display_profile.DisplayProfileViewModel;
import interface_adapter.display_requests.DisplayRequestsController;
import interface_adapter.display_requests.DisplayRequestsState;
import interface_adapter.display_requests.DisplayRequestsViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import interface_adapter.go_back.GoBackController;
import interface_adapter.go_back.GoBackViewModel;
import interface_adapter.logged_in.LoggedInViewModel;

/**
 * The DisplayRequestsView class represents the view that displays a user's list of requests.
 *  This view includes labels and buttons so a user can view a request's profile, accept the request
 *  or go back to the loggedInView.
 */
public class DisplayRequestsView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewname = "display requests";
    private final DisplayRequestsController displayRequestsController;
    private final DisplayRequestsViewModel displayRequestsViewModel;
    private final DisplayProfileController displayProfileController;
    private final DisplayProfileViewModel displayProfileViewModel;
    private final AcceptRequestController acceptRequestController;
    private final AcceptRequestViewModel acceptRequestViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final GoBackController goBackController;
    private final GoBackViewModel goBackViewModel;
    private JPanel buttons;
    private JPanel requestComponents;

    public DisplayRequestsView(DisplayRequestsViewModel displayRequestsViewModel,
                               DisplayRequestsController displayRequestsController,
                               DisplayProfileController displayProfileController,
                               DisplayProfileViewModel displayProfileViewModel,
                               AcceptRequestController acceptRequestController,
                               AcceptRequestViewModel acceptRequestViewModel,
                               LoggedInViewModel loggedInViewModel,
                               GoBackController goBackController,
                               GoBackViewModel goBackViewModel) {
        this.displayRequestsController = displayRequestsController;
        this.displayRequestsViewModel = displayRequestsViewModel;
        this.displayProfileController = displayProfileController;
        this.displayProfileViewModel = displayProfileViewModel;
        this.acceptRequestController = acceptRequestController;
        this.acceptRequestViewModel = acceptRequestViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.goBackController = goBackController;
        this.goBackViewModel = goBackViewModel;

        displayRequestsViewModel.addPropertyChangeListener(this);
        displayProfileViewModel.addPropertyChangeListener(this);
        acceptRequestViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(DisplayRequestsViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttons = new JPanel();
        requestComponents = new JPanel();

        JButton back = new JButton(GoBackViewModel.BACK_BUTTON_LABEL);
        buttons.add(back);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(buttons);

        back.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (evt.getSource().equals(back)) {
                        goBackController.execute();
                    }
                }
            }
        );
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(requestComponents);
        this.add(buttons);
    }

    /**
     * Responds to property change events, updating DisplayRequestsView to display a message if user accepts.
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("displayRequestsState")) {
            DisplayRequestsState displayRequestsState = displayRequestsViewModel.getState();
            ArrayList<String> requests = displayRequestsState.getRequests();
            requests.removeIf(String::isEmpty);

            requestComponents.removeAll();

            if (!(requests.isEmpty())) {
                for (String request : requests) {
                    // acceptRequestState.setRequestName();

                    JLabel requestUsername = new JLabel(request);
                    buttons.add(requestUsername);

                    JButton viewProfile = new JButton(DisplayProfileViewModel.VIEW_BUTTON_LABEL);
                    JButton acceptRequest = new JButton(AcceptRequestViewModel.ACCEPT_BUTTON_LABEL);

                    // Associate each view profile button with the corresponding request username
                    viewProfile.putClientProperty("userString", request);
                    acceptRequest.putClientProperty("userString", request);

                    buttons.add(viewProfile);
                    buttons.add(acceptRequest);

                    viewProfile.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    if (evt.getSource().equals(viewProfile)) {
                                        // Retrieve the associated request name
                                        String associatedString = (String) viewProfile.getClientProperty("userString");
                                        displayRequestsState.setRequestName(associatedString);

                                        displayProfileController.execute(
                                                displayRequestsState.getUsername(),
                                                displayRequestsState.getRequestName()
                                        );
                                    }
                                }
                            }
                    );

                    acceptRequest.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    DisplayRequestsState displayRequestsState = displayRequestsViewModel.getState();
                                    AcceptRequestState acceptRequestState = acceptRequestViewModel.getState();

                                    if (evt.getSource().equals(acceptRequest)) {
                                        // Retrieve the associated request name
                                        String associatedString = (String) acceptRequest.getClientProperty("userString");

                                        displayRequestsState.setRequestName(associatedString);
                                        acceptRequestState.setAcceptError(null);

                                        acceptRequestController.execute(
                                                displayRequestsState.getUsername(),
                                                displayRequestsState.getRequestName()
                                        );
                                    }
                                }
                            }
                    );
                    requestComponents.add(requestUsername);
                    requestComponents.add(viewProfile);
                    requestComponents.add(acceptRequest);
                }
                requestComponents.revalidate();
                requestComponents.repaint();
            }
        } else if (evt.getPropertyName().equals("acceptRequestState")) {
            AcceptRequestState state = (AcceptRequestState) evt.getNewValue();

            if (state.getAcceptError() != null) {
                JOptionPane.showMessageDialog(this, state.getAcceptError());
            } else {
                JOptionPane.showMessageDialog(this, state.getAcceptedMessage());
            }
        }
    }
}