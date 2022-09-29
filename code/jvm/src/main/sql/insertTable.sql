begin transaction;

insert into player (id, name, email, score, password_ver) values
    (1, 'Leki', 'leki@leki.leki', 10, 123),
    (2, 'Palma', 'palma@palma.palma', 0, 123),
    (3, 'Barroso', 'barroso@barroso.barroso', 20, 123);

insert into token (token_ver, player) values
    ('123', 1);

insert into game (id, width, height, state, player1, player2, curr_player) values
    (1, 10, 10, 'layout_definition', 1, 2, 1);

insert into ship_type values
    ('carrier', 5),
    ('battleship', 4),
    ('submarine', 3),
    ('cruiser', 3),
    ('destroyer', 2);

commit transaction;