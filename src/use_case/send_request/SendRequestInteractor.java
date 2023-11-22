package use_case.send_request;

import entity.User;

public class SendRequestInteractor implements SendRequestInputBoundary {
    final SendRequestUserDataAccessInterface sendRequestUserDAO;
    final SendRequestOutputBoundary sendRequestPresenter;
    public SendRequestInteractor(SendRequestUserDataAccessInterface sendRequestUserDataAccessInterface,
                                 SendRequestOutputBoundary sendRequestOutputBoundary) {
        this.sendRequestUserDAO = sendRequestUserDataAccessInterface;
        this.sendRequestPresenter = sendRequestOutputBoundary;
    }
    @Override
    public void execute(SendRequestInputData sendRequestInputData) {
        String senderUsername = sendRequestInputData.getSenderUsername();
        String receiverUsername = sendRequestInputData.getReceiverUsername();

        User sender = sendRequestUserDAO.get(senderUsername);
        User receiver = sendRequestUserDAO.get(receiverUsername);

        //execute if the sender has already requested the receiver
        if (sendRequestUserDAO.isRequested(sender, receiver)) {
            sendRequestPresenter.prepareFailView("You have already requested" + receiverUsername);

        //execute if the sender has not previously requested the receiver
        } else {
            sendRequestUserDAO.sendFriendRequest(sender, receiver);

            SendRequestOutputData sendRequestOutputData = new SendRequestOutputData(receiver.getUsername(), false);
            sendRequestPresenter.prepareSuccessView(sendRequestOutputData);
        }
    }
}