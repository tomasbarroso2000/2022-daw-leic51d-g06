begin transaction;

insert into player (name, email, score, password_ver) values
    ('Leki', 'leki@leki.leki', 10, 123),
    ('Palma', 'palma@palma.palma', 0, 123),
    ('Barroso', 'barroso@barroso.barroso', 20, 123);

insert into token (token_ver, player) values
    ('123', 1),
    ('321', 2);

insert into game (type, state, player1, player2, curr_player, deadline) values
    ('beginner', 'layout_definition', 1, 2, 1, interval '20 seconds'),
    ('experienced', 'shooting', 1, 2, 1, interval '20 seconds');


commit transaction;