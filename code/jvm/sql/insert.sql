begin transaction;

insert into users (name, email, score, password_ver) values
    ('Leki', 'leki@leki.leki', 10, 'yes'),
    ('Palma', 'palma@palma.palma', 0, 'yes'),
    ('Barroso', 'barroso@barroso.barroso', 20, 'yes');

insert into tokens values
    ('pmWkWSBCL51Bfkhn79xPuKBKHz__H6B-mY6G9_eieuM=', 1, now(), now()),
    ('jSPPbIboNKeqbt7VTCbOK7LnSQNTjGG91dIZeZerL3I=', 2, now(), now());

insert into game_types values
    ('beginner', 10, 1, 60, 60),
    ('experienced', 12, 5, 60, 30),
    ('expert', 15, 6, 30, 30);

insert into games (type, state, player1, player2, curr_player, started_at) values
    ('beginner', 'layout_definition', 1, 2, 1, now()),
    ('experienced', 'shooting', 1, 2, 2, now());

insert into ship_types values
    ('carrier', 6, 'beginner'),
    ('battleship', 5, 'beginner'),
    ('cruiser', 4, 'beginner'),
    ('submarine', 4, 'beginner'),
    ('destroyer', 3, 'beginner'),
    ('carrier', 5, 'experienced'),
    ('battleship', 4, 'experienced'),
    ('cruiser', 3, 'experienced'),
    ('submarine', 3, 'experienced'),
    ('destroyer', 2, 'experienced'),
    ('carrier', 5, 'expert'),
    ('battleship', 4, 'expert'),
    ('destroyer', 3, 'expert');

insert into lobbies (user_id, game_type, enter_time) values
    (1, 'beginner', now());

commit transaction;