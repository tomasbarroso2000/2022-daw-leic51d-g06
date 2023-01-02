## Introduction

This document contains the relevant design and implementation aspects of the client application of the Exploding Battleships game.

## Software organization

### React Framework

The React.js framework is an open-source, component-based JavaScript framework and library used for building interactive user interfaces and web applications.
This framework was used to build the client application, allowing the developers to easily create the HTML elements used in the different pages of the single page application.

### Domain

The domain module contains the types that represent the entities of the server responses and some helper functions that are useful when making logic calculations for the development of a game.

### Router

To achieve the successful development of the single page application, we used the React Router technology. This technology provides a simple way to declaratively map routes to components and manage the behavior of the application when the URL changes.

### Service

The service module contains all the operations related to the API in the RealService.tsx file.
In order to test the code that depends on the API responses without actually needing to connect to the API, we created a fake service that implements all the functions the real service implements but with mocked results.

### Views

#### GetAuthn

GetAuthn is a React element that verifies authentication when loading a page. This element uses the token stored in the LoggedInContext if there is one or the one stored in the cookies. The required attribute can be used to declare if a page requires authentication or not. If the page requires authentication, the user is redirected to the Login page in the abscence of one.

#### Home

The Home page shows some information about the application and the developers. From this page, the user can navigate to the Login page and start playing some battleships games as well as check the rankings of the players.

#### Login

The Login page allows the user to login and start playing. If the user doesn't yet have an account, there is also a button on this page to navigate to the user creation page.

#### CreateUser

The CreateUser page allows the creation of a new user. The new user must supply a username, an email address that is not yet used and a password that is at least 4 characters long and has at least 1 lowercase letter, 1 uppercase letter and 1 number.

#### Rankings

The Rankings page shows the current state of the players' scores in the database.

#### ListGames

The ListGames page presents the games the user is currently playing and allows for the creation of a new one.

#### CreateGame

In the CreateGame page, the user can check which are the current game types and their respective rules and choose one to play.

#### WaitForGame

The WaitForGame page is where the user will stay after selecting a game type to play. Once a match is established against another user, the application will automatically navigate to the PlayGame page.

#### PlayGame

The PlayGame page is where the actual game is played. This page includes all the stages of a game of battleships (layout definition, shooting and completed game)

## Implementation Challenges

Biggest challenges we faced while developing the client application:

- Because the Siren Hypermedia is a completely new technology for us, it was really challenging to figure out the best way to interpret the Siren API responses and to establish the connections between each entity. Although a good navigation in the client application was achieved, our use and understanding of the Siren technology is still far from perfect;
- Most of the styling code is in a CSS (Cascading Style Sheets) file but we also used the React framework to define the styling of the HTML elements which might be an organization problem;
- Building the drag-and-drop system was challenging since we never had to something smiliar before. We achieved a working system but it has some limitations and might not be very intuitive to the user. Because of this fact, we decided to include some instructions in the layout definition page so the user can better understand how to proceed;
- Understanding how hooks work in React and managing state with hooks brought a lot of challenges our way since we had to learn a lot of rules that need to be followed when using this technology.

##

<p align="center">ISEL - LEIC - DAW - 51D - G06<p>
