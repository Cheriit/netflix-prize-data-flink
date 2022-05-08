CREATE TABLE IF NOT EXISTS movie_ratings (
    window_start DATE NOT NULL,
    movie_id varchar(128) NOT NULL,
    title varchar(128) NOT NULL,
    rating_count LONG NOT NULL,
    rating_sum LONG NOT NULL,
    unique_rating_count LONG NOT NULL,
    PRIMARY KEY (window_start, movie_id)
);

