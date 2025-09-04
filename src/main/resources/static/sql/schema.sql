CREATE TYPE game_status AS ENUM ('ON_MATCHING', 'END');

CREATE TABLE sport (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(31) NOT NULL,
    recommended_player_count INT NOT NULL
);

CREATE TABLE game (
    id BIGSERIAL PRIMARY KEY,
    sport_id BIGINT NOT NULL REFERENCES sport(id),
    name VARCHAR(31) NOT NULL,
    player_count INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    game_status game_status NOT NULL DEFAULT 'ON_MATCHING'
);

CREATE TABLE game_user (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL REFERENCES game(id)
);
