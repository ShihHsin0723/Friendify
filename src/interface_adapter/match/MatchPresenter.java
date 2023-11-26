package interface_adapter.match;

import interface_adapter.ViewManagerModel;
import interface_adapter.display_matches.DisplayMatchesState;
import interface_adapter.display_matches.DisplayMatchesViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.match.MatchOutputBoundary;
import use_case.match.MatchOutputData;

public class MatchPresenter implements MatchOutputBoundary {
    private final LoggedInViewModel loggedInViewModel;
    private final DisplayMatchesViewModel displayMatchesViewModel;
    private ViewManagerModel viewManagerModel;

    public MatchPresenter(LoggedInViewModel loggedInViewModel,
                          ViewManagerModel viewManagerModel,
                          DisplayMatchesViewModel displayMatchesViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.displayMatchesViewModel = displayMatchesViewModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(MatchOutputData response) {
        DisplayMatchesState displayMatchesState = displayMatchesViewModel.getState();
        displayMatchesState.setMatches(response.getMatches());
        displayMatchesViewModel.setState(displayMatchesState);
        displayMatchesViewModel.firePropertyChanged();

        viewManagerModel.setActiveView(displayMatchesViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}