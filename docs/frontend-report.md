## Introduction
This document contains the relevant design and implementation aspects of the client application of the battleships game.

## Software organization

### React Framework 
The React.js framework is an open-source, component-based JavaScript framework and library used for building interactive user interfaces and web applications.
This framework was used to build this client application, allowing the developers to easily create the HTML elements used in the different web pages.

### Domain

### Router

### Service
The service module contains all the operations related to the API in the RealService.tsx file.
In order to test the code dependent from the API without actually needing to connect to the API, we created a fake service that implements all the functions the real service implements but with mocked results.
This module also contains a utils file to help organize the more complex operations of the module.

## Implementation Challenges
Biggest challenges we faced while developing the client application:
* Since the Siren framework was a completelly different technology to what we are used to, developing code to interpret the API responses (received in Siren) was one od the biggest challenges;
* The organization of the styling code;
* Drag and drop  

##

<p align="center">ISEL - LEIC - DAW - 51D - G06<p>