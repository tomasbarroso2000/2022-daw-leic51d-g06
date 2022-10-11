begin transaction;

insert into player (id, name, email, score, password_ver) values
    (1, 'Leki', 'leki@leki.leki', 10, 123),
    (2, 'Palma', 'palma@palma.palma', 0, 123),
    (3, 'Barroso', 'barroso@barroso.barroso', 20, 123);

insert into token (token_ver, player) values
    ('123', 1);

insert into game (id, width, height, hits_per_round, state, player1, player2, curr_player) values
    (1, 10, 10, 1, 'shooting', 1, 2, 1);


commit transaction;