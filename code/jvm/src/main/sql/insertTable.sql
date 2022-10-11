begin transaction;

insert into player (id, name, email, score, password_ver) values
    (1, 'Leki', 'leki@leki.leki', 10, 123),
    (2, 'Palma', 'palma@palma.palma', 0, 123),
    (3, 'Barroso', 'barroso@barroso.barroso', 20, 123);

insert into token (token_ver, player) values
    ('123', 1);

insert into game (id, type, state, player1, player2, curr_player, deadline) values
    (1, 'beginner', 'layout_definition', 1, 2, 1, interval '20 seconds'),
    (2, 'experienced', 'shooting', 1, 2, 1, interval '20 seconds');


commit transaction;