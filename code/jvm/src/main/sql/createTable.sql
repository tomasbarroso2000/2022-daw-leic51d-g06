begin transaction;

create table if not exists player(
    id serial primary key,
    name varchar(20) not null,
    email  varchar(100) not null unique check (email like '%@%.%'),
    score integer,
    password integer not null
);

create table if not exists token(
    token varchar(40) primary key,
    player integer references player(id)
);

create table if not exists game(
    id serial primary key,
    game_width integer check (game_width >= 10),
    game_heigth integer check (game_heigth >= 10),
    state varchar(20) check (state in ('layout_definition', 'shooting', 'completed')),
    player_one integer references player(id),
    player_two integer references player(id),
    curr_player integer references player(id) check (curr_player = player_one or curr_player = player_two)
);

create table if not exists hit(
    square varchar(5) primary key check (square like '[a-za-z][0-10]&' or square like '[0-10][a-za-z]&'),
    hit_timestamp timestamp,
    player integer references player(id),
    game integer references game(id)
);

create table if not exists ship(
    first_square varchar(5) primary key check (first_square like '[a-za-z][0-10]&' or first_square like '[0-10][a-za-z]&'),
    n_of_hits integer check (n_of_hits >= 0),
    orientation varchar(50) check (orientation in ('vertical', 'horizontal')),
    player integer references player(id),
    game integer references game(id)
);

create table if not exists ship_type(
    stype_name varchar(20) primary key check (stype_name in ('carrier', 'battleship', 'submarine', 'cruiser', 'destroyer')),
    ship_size integer not null
);

commit transaction;