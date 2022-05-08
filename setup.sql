CREATE TABLE IF NOT EXISTS movie_ratings (
    window_start DATE NOT NULL,
    movie_id varchar(128) NOT NULL,
    title varchar(128) NOT NULL,
    rating_count INTEGER NOT NULL,
    rating_sum INTEGER NOT NULL,
    unique_rating_count INTEGER NOT NULL,
    PRIMARY KEY (window_start, movie_id)
);

