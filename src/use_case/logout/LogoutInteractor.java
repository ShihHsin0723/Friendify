package use_case.logout;

public class LogoutInteractor implements LogoutInputBoundary {
    final LogoutUserDataAccessInterface userDataAccessObject;
    final LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor(LogoutOutputBoundary logoutOutputBoundary) {
        this.logoutPresenter = logoutOutputBoundary;
    }
    @Override
    public void execute() {
        logoutPresenter.prepareSuccessView();
    }
}

