package use_case.display_profile;

import entity.Profile;
import entity.User;
import interface_adapter.display_profile.DisplayProfileState;
import interface_adapter.display_profile.DisplayProfileViewModel;


public class DisplayProfileInteractor implements DisplayProfileInputBoundary {
    final DisplayProfileUserDataAccessInterface userDataAccessObject;
    final DisplayProfileOutputBoundary displayProfilePresenter;
    final DisplayProfileViewModel displayProfileViewModel;

    public DisplayProfileInteractor(DisplayProfileUserDataAccessInterface userDataAccessInterface, DisplayProfileOutputBoundary displayProfileOutputBoundary
    , DisplayProfileViewModel displayProfileViewModel) {
        this.userDataAccessObject = userDataAccessInterface;
        this.displayProfilePresenter = displayProfileOutputBoundary;
        this.displayProfileViewModel = displayProfileViewModel;
    }

    @Override
    public void execute(DisplayProfileInputData displayProfileInputData) {
        String otherUsername = displayProfileInputData.getUsername();

        User user = userDataAccessObject.get(otherUsername);
        Profile profile = user.getProfile();
        DisplayProfileOutputData displayProfileOutputData = new DisplayProfileOutputData(profile.getBio(), profile.getTopThreeArtists(), profile.getSpotifyHandle());
        // if user is a friend, then they can view the friends-only user profile:
        if (userDataAccessObject.isFriend(otherUsername)) {
            displayProfilePresenter.prepareSuccessViewFriends(displayProfileOutputData);
        }
        // else the user can only see the common user profile:
        else {
            displayProfilePresenter.prepareSuccessViewCommon(displayProfileOutputData);
        }
    }
}