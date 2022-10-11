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
    deadline timestamp
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
   destroyed bool not null,
   orientation varchar(50) check (orientation in ('vertical', 'horizontal')),
   player integer references player(id),
   game integer references game(id),
   ship_type varchar(20) references ship_type(type_name),
   primary key (first_square, player, game)
);

create or replace function maybe_destroy_ship() returns trigger language plpgsql as $$
    declare
        size_of_ship integer;
    begin
        select ship_size into size_of_ship from ship_type where type_name = new.ship_type;
        if (new.n_of_hits = size_of_ship) then
            new.destroyed = true;
        end if;
        return new;
    end;
$$;

create or replace trigger maybe_destroy_ship
    before update of n_of_hits on ship
    for each row
execute function maybe_destroy_ship();

create table if not exists lobby(
	player integer references player(id) primary key,
	width integer not null check (width >= 10),
	height integer not null check (height >= 10),
	hits_per_round integer not null check (lobby.hits_per_round >= 1)
);

create or replace function maybe_start_game() returns trigger language plpgsql as $$
    declare
        player_with_same_settings integer = null;
    begin
        select player into player_with_same_settings from lobby
        where width = new.width and height = new.height and hits_per_round = new.hits_per_round;
        if (player_with_same_settings is not null) then
            insert into game (width, height, hits_per_round, state, player1, player2, curr_player) values
            (new.width, new.height, new.hits_per_round, 'layout_definition', player_with_same_settings, new.player, player_with_same_settings);
            delete from lobby where (player = player_with_same_settings);
            return null;
        else
            return new;
        end if;
    end;
$$;

create or replace trigger maybe_start_game
    before insert on lobby
    for each row
execute function maybe_start_game();

commit transaction;