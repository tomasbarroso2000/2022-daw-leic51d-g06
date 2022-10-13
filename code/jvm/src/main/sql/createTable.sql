begin transaction;

create table if not exists player(
    id serial primary key,
    name varchar(20) not null,
    email  varchar(100) not null unique check (email like '%@%.%'),
    score integer,
    password_ver integer not null
);

create table if not exists token(
    token_ver varchar(40) primary key, -- maybe should be hashed
    player integer references player(id)
);

create table if not exists game(
    id serial primary key,
    type varchar(20) not null check (type in ('beginner', 'experienced', 'expert')),
    state varchar(20) not null check (state in ('layout_definition', 'shooting', 'completed')),
    player1 integer not null references player(id),
    player2 integer not null references player(id),
    curr_player integer references player(id) check (curr_player = player1 or curr_player = player2),
    deadline interval
);

create table if not exists game_type(
    name varchar(25) primary key,
    board_size integer not null,
    shots_per_round integer not null,
    layout_def_time_in_secs integer not null,
    shooting_time_in_secs integer not null
);

create table if not exists ship_type(
    name varchar(25),
    size integer check (size > 0),
    game_type varchar(25) references game_type(name),
    primary key (name, game_type)
);

create table if not exists hit(
    square varchar(5) check (square ~ '[a-z][0-9]+'),
    hit_timestamp timestamp,
    player integer references player(id),
    game integer references game(id),
    primary key (square, player, game)
);

create table if not exists ship(
   first_square varchar(5) check (first_square ~ '[a-z][0-9]+'),
   name varchar(20) not null check (name in ('carrier', 'battleship', 'cruiser', 'submarine', 'destroyer')),
   size integer not null check (size > 0),
   n_of_hits integer check (n_of_hits >= 0),
   destroyed bool not null,
   orientation varchar(50) not null check (orientation in ('vertical', 'horizontal')),
   player integer references player(id),
   game integer references game(id),
   primary key (first_square, player, game)
);

create table if not exists lobby(
	player integer references player(id),
	game_type varchar(20) not null, --maybe check if game type belongs to list
	enter_time timestamp not null,
	primary key (player, game_type, enter_time)
);


commit transaction;