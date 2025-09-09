CREATE TYPE game_status AS ENUM ('ON_MATCHING', 'END');
CREATE TYPE oauth_provider AS ENUM ('KAKAO');

CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    oauth_id BIGINT NOT NULL,
    oauth_provider oauth_provider NOT NULL,
    name VARCHAR(31),
    image_url VARCHAR(255),
    description VARCHAR(255),
    school_email VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE
);

ALTER TABLE member
    ADD CONSTRAINT uq_oauth UNIQUE (oauth_id, oauth_provider);

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
    member_id BIGINT NOT NULL REFERENCES member(id),
    game_id BIGINT NOT NULL REFERENCES game(id)
);
