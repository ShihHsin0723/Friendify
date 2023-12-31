package use_case.match;

import entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MatchInteractor implements MatchInputBoundary {
    final MatchUserDataAccessInterface matchUserDAO;
    final MatchOutputBoundary matchPresenter;
    public MatchInteractor(MatchUserDataAccessInterface matchingUserDataAccessInterface,
                           MatchOutputBoundary matchingOutputBoundary) {
        this.matchUserDAO = matchingUserDataAccessInterface;
        this.matchPresenter = matchingOutputBoundary;
    }

    /**
     * Calculates weighted averages of the similarity scores generated from four concrete matching
     * strategies: TitleStrategy, ArtistStrategy, GenreStrategy, and AttributeStrategy.
     * Using the weighted averages, get the top three users (or less) in terms of similarity to the current user.
     * Finally, invoke the Presenter with the Output Data.
     * @param currentUsername The username of the currently logged-in user
     */
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

        // Weights applied to similarity score values
        // 0.6 for titleStrategy, 0.25 for artistStrategy, 0.1 for genreStrategy, and 0.05 for attributeStrategy
        double weights = 0.4 + 0.3 + 0.2 + 0.1;

        for (HashMap.Entry<String, Double> entry : titleScores.entrySet()) {
            // Calculate weighted average
            Double weightedAvg = (0.4 * titleScores.get(entry.getKey()) +
                    0.3 * artistScores.get(entry.getKey()) +
                    0.2 * genreScores.get(entry.getKey()) +
                    0.1 * attributeScores.get(entry.getKey())) / weights;

            similarityScores.put(entry.getKey(), weightedAvg);
        }

        ArrayList<HashMap.Entry<String, Double>> sortedUsers = new ArrayList<>(similarityScores.entrySet());
        // Sort in descending order (highest similarity first)
        sortedUsers.sort(HashMap.Entry.<String, Double>comparingByValue().reversed());

        LinkedHashMap<String, Double> topSimilarUsers = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(5, sortedUsers.size()); i++) {
            topSimilarUsers.put(sortedUsers.get(i).getKey(), sortedUsers.get(i).getValue());
        }

        MatchOutputData matchingOutputData = new MatchOutputData(topSimilarUsers);
        matchPresenter.prepareSuccessView(matchingOutputData);
    }
}