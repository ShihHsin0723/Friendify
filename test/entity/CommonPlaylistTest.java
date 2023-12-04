package entity;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Testing file for CommonUser Class
 */
public class CommonPlaylistTest {
    private PlaylistFactory playlistFactory;

    /**
     * Create empty profile and playlists objects as we are only testing User behaviour
     */
    @Before
    public void init() {
        this.playlistFactory = new CommonPlaylistFactory();

    }
}