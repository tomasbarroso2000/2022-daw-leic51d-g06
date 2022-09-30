begin transaction;

create table if not exists player(
    id serial primary key,
    name varchar(20) not null,
    email  varchar(100) not null unique check (email like '%@%.%'),
    score integer,
    password_ver integer not null
);

create table if not exists token(
    token_ver varchar(40) primary key,
    player integer references player(id)
);

create table if not exists game(
    id serial primary key,
    width integer not null check (width >= 10),
    height integer not null check (height >= 10),
    hits_per_round integer not null check (hits_per_round >= 1),
    state varchar(20) not null check (state in ('layout_definition', 'shooting', 'completed')),
    player1 integer not null references player(id),
    player2 integer not null references player(id),
    curr_player integer references player(id) check (curr_player = player1 or curr_player = player2)
);

create table if not exists hit(
    square varchar(5) check (square ~ '[a-z][0-9]+'),
    hit_timestamp timestamp,
    player integer references player(id),
    game integer references game(id),
    primary key (square, player, game)
);

create table if not exists ship_type(
    type_name varchar(20) primary key check (type_name in ('carrier', 'battleship', 'submarine', 'cruiser', 'destroyer')),
    ship_size integer not null
);

create table if not exists ship(
   first_square varchar(5) check (first_square ~ '[a-z][0-9]+'),
   n_of_hits integer check (n_of_hits >= 0),
   orientation varchar(50) check (orientation in ('vertical', 'horizontal')),
   player integer references player(id),
   game integer references game(id),
   ship_type varchar(20) references ship_type(type_name),
   primary key (first_square, player, game)

);

create table if not exists lobby(
	player integer references player(id) primary key,
	lobby_time timestamp not null,
	width integer not null check (width >= 10),
	height integer not null check (height >= 10),
	hits_per_round integer not null check (lobby.hits_per_round >= 1)
);

commit transaction;