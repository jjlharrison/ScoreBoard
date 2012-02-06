CREATE TABLE
	team_players
(
	team_id INTEGER NOT NULL,
	player_id INTEGER NOT NULL,
	PRIMARY KEY (team_id, player_id),
	CONSTRAINT FK_TEAM_PLAYERS_TEAM_ID FOREIGN KEY (team_id) REFERENCES team (id),
	CONSTRAINT FK_TEAM_PLAYERS_PLAYER_ID FOREIGN KEY (player_id) REFERENCES player (id)
)
