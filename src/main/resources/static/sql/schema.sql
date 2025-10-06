CREATE TYPE game_status AS ENUM ('ON_MATCHING', 'END');
CREATE TYPE oauth_provider AS ENUM ('KAKAO');
CREATE TYPE report_status AS ENUM ('PENDING', 'RESOLVED', 'REJECTED');
CREATE TYPE rank_game_team AS ENUM ('RED_TEAM', 'BLUE_TEAM', 'NONE');


CREATE TABLE school (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    postfix VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    oauth_id BIGINT NOT NULL,
    oauth_provider oauth_provider NOT NULL,
    name VARCHAR(31),
    image_url VARCHAR(255),
    description VARCHAR(255),
    school_email VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    school_id BIGINT,
    CONSTRAINT fk_member_school FOREIGN KEY (school_id) REFERENCES school(id)
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
    description VARCHAR(255),
    game_status game_status NOT NULL DEFAULT 'ON_MATCHING'
);

CREATE TABLE rank_game (
    id BIGINT PRIMARY KEY REFERENCES game(id),
    win_team rank_game_team
);

CREATE TABLE game_user (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES member(id),
    game_id BIGINT NOT NULL REFERENCES game(id)
);

CREATE TABLE rank_game_user (
    team rank_game_team
);

CREATE TABLE game_report (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL REFERENCES game(id) ON DELETE CASCADE,
    reporter_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    reported_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    reason_text VARCHAR(255),
    status report_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_different_users CHECK (reporter_id <> reported_id),
    CONSTRAINT unique_report UNIQUE (game_id, reporter_id, reported_id)
);

CREATE TABLE mmr (
    id BIGSERIAL PRIMARY KEY,
    sport_id BIGINT NOT NULL REFERENCES sport(id),
    member_id BIGINT NOT NULL REFERENCES member(id),
    mu DOUBLE PRECISION DEFAULT 25,
    sigma DOUBLE PRECISION DEFAULT 8.3
);

ALTER TABLE mmr ADD CONSTRAINT uq_mmr_sport_id_member_id UNIQUE (sport_id, member_id);

CREATE TABLE match_result_vote (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES member(id),
    game_id BIGINT NOT NULL REFERENCES game(id),
    win_team rank_game_team NOT NULL
);
