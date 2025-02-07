begin transaction;

create table if not exists users(
    id serial primary key,
    name varchar(64) not null,
    email  varchar(100) not null unique check (email like '%@%.%'),
    score integer not null check(score >= 0),
    password_ver varchar(256) not null
); -- might add privilege levels later for management purposes

create table if not exists tokens(
    token_ver varchar(256) primary key,
    user_id integer references users(id),
    created_at timestamp not null,
    last_used_at timestamp not null
);

create table if not exists game_types(
    name varchar(25) primary key,
    board_size integer not null,
    shots_per_round integer not null,
    layout_def_time_in_secs integer not null,
    shooting_time_in_secs integer not null
);

create table if not exists games(
    id serial primary key,
    type varchar(20) not null references game_types(name),
    state varchar(20) not null check (state in ('layout_definition', 'shooting', 'completed')),
    player1 integer not null references users(id),
    player2 integer not null references users(id),
    curr_player integer not null check (curr_player = player1 or curr_player = player2),
    started_at timestamp not null
);

create table if not exists ship_types(
    name varchar(25),
    size integer check (size > 0),
    game_type varchar(25) references game_types(name),
    primary key (name, game_type)
);

create table if not exists hits(
    square varchar(5) check (square ~ '[a-z][0-9]+'),
    hit_timestamp timestamp not null,
    on_ship bool not null,
    user_id integer references users(id),
    game_id integer references games(id),
    primary key (square, user_id, game_id)
);

create table if not exists ships(
   first_square varchar(5) check (first_square ~ '[a-z][0-9]+'),
   name varchar(20) not null,
   size integer not null check (size > 0),
   n_of_hits integer check (n_of_hits >= 0),
   destroyed bool not null,
   orientation varchar(50) not null check (orientation in ('vertical', 'horizontal')),
   user_id integer references users(id),
   game_id integer references games(id),
   primary key (first_square, user_id, game_id)
);

create table if not exists lobbies(
    id serial primary key,
	user_id integer references users(id),
	game_type varchar(20) not null references game_types(name),
	enter_time timestamp not null,
	game_id integer references  games(id)
);


commit transaction;