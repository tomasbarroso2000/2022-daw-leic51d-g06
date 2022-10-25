begin transaction;

delete from lobbies;

delete from ship_types;

delete from ships;

delete from hits;

delete from game_types;

delete from games;

delete from tokens;

delete from users;

commit transaction;