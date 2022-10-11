begin transaction;

delete from lobby;

delete from ship;

delete from hit;

delete from game;

delete from token;

delete from player;

commit transaction;