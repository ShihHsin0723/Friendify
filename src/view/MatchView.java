package view;

import interface_adapter.go_back.GoBackController;
import interface_adapter.go_back.GoBackViewModel;
import interface_adapter.match.MatchState;
import interface_adapter.match.MatchViewModel;
import interface_adapter.send_request.SendRequestViewModel;
import interface_adapter.send_request.SendRequestController;
import interface_adapter.send_request.SendRequestState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MatchView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "display matches";
    private final MatchViewModel matchViewModel;
    private final SendRequestViewModel sendRequestViewModel;
    private final SendRequestController sendRequestController;
    private final GoBackViewModel goBackViewModel;
    private final GoBackController goBackController;
    private JPanel buttons;
    private JPanel matchComponents;

    public MatchView(MatchViewModel matchViewModel,
                     SendRequestViewModel sendRequestViewModel,
                     SendRequestController sendRequestController,
                     GoBackViewModel goBackViewModel,
                     GoBackController goBackController) {

        this.matchViewModel = matchViewModel;
        this.sendRequestViewModel = sendRequestViewModel;
        this.sendRequestController = sendRequestController;
        this.goBackViewModel = goBackViewModel;
        this.goBackController = goBackController;

        matchViewModel.addPropertyChangeListener(this);
        sendRequestViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(MatchViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttons = new JPanel();
        matchComponents = new JPanel();

        JButton back = new JButton(GoBackViewModel.BACK_BUTTON_LABEL);
        buttons.add(back);

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
        this.add(matchComponents);
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("matchState")) {
            MatchState currentState = matchViewModel.getState();
            LinkedHashMap<String, Double> topSimilarUsers = currentState.getTopSimilarUsers();

            // Clear previous components
            matchComponents.removeAll();

            for (HashMap.Entry<String, Double> entry : topSimilarUsers.entrySet()) {
                String username = entry.getKey();
                Double similarityScore = entry.getValue();

                JLabel match = new JLabel(username + ": " + similarityScore);

                // Create a button for sending a friend request
                JButton request = new JButton(MatchViewModel.REQUEST_BUTTON_LABEL);

                // Associate the button with the username
                request.putClientProperty("userString", username);

                request.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        MatchState matchState = matchViewModel.getState();
                        SendRequestState sendRequestState = sendRequestViewModel.getState();

                        if (evt.getSource().equals(request)) {
                            // Retrieve the associated username with the user being requested
                            String associatedString = (String) request.getClientProperty("userString");
                            String senderUsername = matchState.getUsername();

                            // Setting the receiverUsername and requestError attributes in the state
                            sendRequestState.setReceiverUsername(associatedString);
                            String receiverUsername = sendRequestState.getReceiverUsername();
                            sendRequestState.setRequestError(null);

                            sendRequestController.execute(senderUsername, receiverUsername);
                        }
                    }
                });

                matchComponents.add(match);
                matchComponents.add(request);
            }

            matchComponents.revalidate();
            matchComponents.repaint();

        } else if (evt.getPropertyName().equals("sendRequestState")) {
            SendRequestState state = (SendRequestState) evt.getNewValue();

            if (state.getRequestError() != null) {
                JOptionPane.showMessageDialog(this, state.getRequestError());
            } else {
                JOptionPane.showMessageDialog(this, state.getRequestSentMessage());
            }
        }
    }
}
