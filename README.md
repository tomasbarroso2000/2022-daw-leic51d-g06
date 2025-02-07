# DAW project

## Exploding Battleships
This project was developed for the Web Applications Development (DAW) course and consists of a web-based Battleships game that allows two people to play online against each other. 

This system is composed of a centralized backend service and two frontend applications. The frontend applications run on the user's devices, providing the interface between those users and the system. The backend service manages all the game related data and enforces the game rules.

Frontend applications communicate with the backend service using an HTTP API. These applications do not communicate directly between themselves. All communication is done via the backend service, which has the responsibility of ensuring all the game rules are followed, as well as storing the game states and final outcomes.

### Documentation
[Backend service technical report](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/blob/main/docs/backend-report.md)

[Frontend application technical report](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/blob/main/docs/frontend-report.md)

[Server run instructions](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/blob/main/code/jvm/README.md)

[Web application run instructions](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/blob/main/code/js/README.md)

[HTTP API specification](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/blob/main/docs/api-spec.md)

### Developed by 
#### TheExplodingKittens:
* Alexandre Madeira
* Miguel Palma
* Tomás Barroso
