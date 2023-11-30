package use_case.match;

import entity.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchInteractor implements MatchInputBoundary {
    final MatchUserDataAccessInterface matchUserDAO;
    final MatchOutputBoundary matchPresenter;
    public MatchInteractor(MatchUserDataAccessInterface matchingUserDataAccessInterface,
                           MatchOutputBoundary matchingOutputBoundary) {
        this.matchUserDAO = matchingUserDataAccessInterface;
        this.matchPresenter = matchingOutputBoundary;
    }

    @Override
    public void execute(String currentUsername) {
        User currentUser = matchUserDAO.get(currentUsername);
        HashMap<String, Double> similarityScores = new HashMap<>();

        MatchingStrategy titleStrategy = new TitleStrategy();
        MatchingStrategy artistStrategy = new ArtistStrategy();
        MatchingStrategy genreStrategy = new GenreStrategy();
        MatchingStrategy attributeStrategy = new AttributeStrategy();

        HashMap<String, Double> titleScores = matchUserDAO.getScores(currentUser, titleStrategy);
        HashMap<String, Double> artistScores = matchUserDAO.getScores(currentUser, artistStrategy);
        HashMap<String, Double>  genreScores= matchUserDAO.getScores(currentUser, genreStrategy);
        HashMap<String, Double> attributeScores = matchUserDAO.getScores(currentUser, attributeStrategy);

        for (HashMap.Entry<String, Double> entry : titleScores.entrySet()) {
           Double score = 0.4 * titleScores.get(entry.getKey()) + 0.3 * artistScores.get(entry.getKey()) +
                   0.2 * genreScores.get(entry.getKey()) + 0.1 * attributeScores.get(entry.getKey());
           similarityScores.put(entry.getKey(), score);
        }

        ArrayList<HashMap.Entry<String, Double>> sortedUsers = new ArrayList<>(similarityScores.entrySet());
        sortedUsers.sort(HashMap.Entry.comparingByValue());

        HashMap<String, Double> topSimilarUsers = new HashMap<>();
        for (int i = 0; i < Math.min(5, sortedUsers.size()); i++) {
            topSimilarUsers.put(sortedUsers.get(i).getKey(), sortedUsers.get(i).getValue());
        }

        MatchOutputData matchingOutputData = new MatchOutputData(topSimilarUsers);
        matchPresenter.prepareSuccessView(matchingOutputData);
    }
}