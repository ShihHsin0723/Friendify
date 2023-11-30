package interface_adapter.match;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.match.MatchOutputBoundary;
import use_case.match.MatchOutputData;

public class MatchPresenter implements MatchOutputBoundary {
    private final LoggedInViewModel loggedInViewModel;
    private final MatchViewModel matchViewModel;
    private ViewManagerModel viewManagerModel;

    public MatchPresenter(LoggedInViewModel loggedInViewModel,
                          ViewManagerModel viewManagerModel,
                          MatchViewModel matchViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.matchViewModel = matchViewModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(MatchOutputData response) {
        MatchState matchState = matchViewModel.getState();
        matchState.setMatches(response.getMatches());
        matchViewModel.setState(matchState);
        matchViewModel.firePropertyChanged();

        viewManagerModel.setActiveView(matchViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}