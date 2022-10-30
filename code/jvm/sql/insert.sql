begin transaction;

insert into users (name, email, score, password_ver) values
    ('Leki', 'leki@leki.leki', 10, 123),
    ('Palma', 'palma@palma.palma', 0, 123),
    ('Barroso', 'barroso@barroso.barroso', 20, 123);

insert into tokens (token_ver, user_id) values
    ('123', 1),
    ('321', 2);

insert into games (type, state, player1, player2, curr_player, started_at) values
    ('beginner', 'layout_definition', 1, 2, 1, now()),
    ('experienced', 'shooting', 1, 2, 1, now());

insert into lobbies (user_id, game_type, enter_time) values
    (1, 'beginner', now());

commit transaction;