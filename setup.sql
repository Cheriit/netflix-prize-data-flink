DROP USER IF EXISTS 'streamuser'@'%';
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS netflix_ratings CHARACTER SET utf8;
GRANT ALL ON netflix_ratings.* TO 'streamuser'@'%';
USE netflix_ratings;
CREATE TABLE IF NOT EXISTS movie_ratings (
    window_start BIGINT NOT NULL,
    movie_id varchar(32) NOT NULL,
    title varchar(64) NOT NULL,
    rating_count INTEGER NOT NULL,
    rating_sum INTEGER NOT NULL,
    unique_rating_count INTEGER NOT NULL,
    PRIMARY KEY (window_start, movie_id)
);

