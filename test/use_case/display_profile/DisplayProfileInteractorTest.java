package use_case.display_profile;

import data_access.FileUserDataAccessObject;
import entity.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.display_common_profile.DisplayCommonProfileViewModel;
import interface_adapter.display_friend_profile.DisplayFriendProfileViewModel;
import interface_adapter.display_profile.DisplayProfilePresenter;
import interface_adapter.display_profile.DisplayProfileViewModel;
import org.junit.jupiter.api.BeforeEach;
import use_case.display_friends.DisplayFriendsUserDataAccessInterface;
import use_case.display_requests.DisplayRequestsUserDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DisplayProfileInteractorTest {
    private DisplayProfileInteractor displayProfileInteractor;
    private ViewManagerModel viewManagerModel;
    /**
     * First add three users, one is a friend, one isn't and one is logged in trying to see both profiles.
     */
    @BeforeEach
    public void setUp() throws IOException {
        // Initialize the objects before each test
        viewManagerModel = new ViewManagerModel();
        UserFactory uf = new CommonUserFactory();
        ProfileFactory pf = new CommonProfileFactory();
        DisplayProfileUserDataAccessInterface user = new FileUserDataAccessObject("./users", uf, pf);
        displayProfileInteractor = new DisplayProfileInteractor(user, new DisplayProfilePresenter(
                new DisplayFriendProfileViewModel(), new DisplayCommonProfileViewModel(), viewManagerModel),
                new DisplayProfileViewModel());
    }
    @BeforeEach
    public void addThreeUsers() {
        UserFactory uf = new CommonUserFactory();
        ProfileFactory pf = new CommonProfileFactory();
        FileUserDataAccessObject fudao;
        try {
            fudao = new FileUserDataAccessObject("./users.csv", uf, pf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // user with user2 as a friend:
        fudao.save(uf.create("user1", "password1", new CommonProfile("Hi", new ArrayList<>(),
                "handle1"), new CommonPlaylist("", new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                0, 0,0,0, new ArrayList<>()), new ArrayList<>(Arrays.asList("user2")), new ArrayList<>()));

        // user with user1 as a friend:
        fudao.save(uf.create("user2", "password2", new CommonProfile("Hello", new ArrayList<>(),
                "handle2"), new CommonPlaylist("", new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                0, 0,0,0, new ArrayList<>()), new ArrayList<>(Arrays.asList("user1")), new ArrayList<>()));

        // user with no friends:
        fudao.save(uf.create("user3", "password3", new CommonProfile("Hi", new ArrayList<>(),
                "handle3"), new CommonPlaylist("", new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                0, 0,0,0, new ArrayList<>()), new ArrayList<>(), new ArrayList<>()));
    }

    /**
     * Testing that the correct profile gets displayed depending on if the user is a friend or not.
     */
    public void testFriendExecute() {
        displayProfileInteractor.execute("user1", new DisplayProfileInputData("user2"));
        assertEquals("friend profile", viewManagerModel.getActiveView());
    }

    public void testNonFriendExecute() {
        displayProfileInteractor.execute("user1", new DisplayProfileInputData("user3"));
        assertEquals("common profile", viewManagerModel.getActiveView());

    }


}