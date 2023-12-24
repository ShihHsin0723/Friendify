# Friendify
## Problem Domain Description
Social connections and friend-matching

## Software Specification
Friendify allows users to match with other users in the system based on the similarities of their chosen Spotify playlist. 
The user first creates their account and is taken to the logged-in page, which has the options to match, view friends, 
view pending requests, and edit their profile. 
In the edit profile page, the user can change their bio, spotify handle, and their chosen Spotify playlist.
For match, the user gets the top 5 users who have the highest similarity scores with them. 
The user can view their profiles and send requests to them, but the profiles have limited information until the 
requested user accepts the friend request.
Once the requests are accepted, users become friends and they can view each other's full profile. 

## Main Entities for Domain
- User
- Profile
- Playlist
- MatchingStrategies (TitleStrategy, ArtistStrategy, GenreStrategy, AttributeStrategy)

## Use Cases
- Accept Request
- Authorize
- Choose Playlist
- Display Friends
- Display Playlists
- Display Profile
- Display Requests
- Edit Bio
- Edit Profile
- Edit Spotify Handle
- Go Back
- Login
- Logout
- Match
- Send Requests
- Signup

## Developer Workflow
1. Task (describe plan)
2. Pull Request (Technical plan, concerns, limitations)  
3. Code (working commits/push)
4. Midway Review
5. Continue Coding
6. Final Code Review (PR)
7. Accept/Modify

## Running Friendify
1. Download the JSON-java JAR file by following the instructions here: https://github.com/stleary/JSON-java
2. Add the file as a new library (Open Module Settings -> + Libraries -> Java -> Select the file)
