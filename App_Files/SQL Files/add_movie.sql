DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie(
        IN movie_title VARCHAR(100),
        IN movie_year INTEGER,
        IN movie_director VARCHAR(100),
        IN star_name VARCHAR(50),
        IN genre VARCHAR(32),
        OUT movie_ID VARCHAR(10)
)

BEGIN
		DECLARE newMovieId VARCHAR(10);
        DECLARE newStarId VARCHAR(10);
        DECLARE newGenreId INTEGER;
		DECLARE star_ID VARCHAR(10);
        DECLARE genre_ID INTEGER;
        IF (SELECT EXISTS(SELECT * FROM movies WHERE movies.title = movie_title and movies.year = movie_year and movies.director = movie_director)) = 0 THEN
                
				SET newMovieId = (SELECT concat('tt',substring(max(id),3,100)+1) as newMovieId from movies);
                INSERT INTO movies(id,title, year, director) VALUES (newMovieId,movie_title, movie_year, movie_director);
        
				IF (SELECT EXISTS(SELECT * FROM stars WHERE stars.name = star_name)) = 0 THEN
						SET newStarId = (SELECT concat('nm',substring(max(id),3,100)+1) as newStarId from stars);
						INSERT INTO stars(id,name) VALUES (newStarId,star_name);
				END IF;

				IF (SELECT EXISTS(SELECT * FROM genres WHERE genres.name = genre)) = 0 THEN
						Set newGenreId = (SELECT max(id)+1 as newGenreId from genres);
						INSERT INTO genres(id,name) VALUES (newGenreId,genre);
				END IF;

				SET movie_ID = (SELECT id FROM movies WHERE movies.title = movie_title and movies.director = movie_director and movies.year = movie_year);
				SET star_ID = (SELECT id FROM stars WHERE stars.name = star_name);
				SET genre_ID = (SELECT id FROM genres WHERE genres.name = genre);

				IF (SELECT EXISTS(SELECT * FROM stars_in_movies as sm WHERE sm.starId = star_ID and sm.movieId = movie_ID)) = 0 THEN
						INSERT INTO stars_in_movies VALUES(star_ID, movie_ID);
				END IF;

				IF (SELECT EXISTS(SELECT * FROM genres_in_movies as gm WHERE gm.genreId = genre_ID and gm.movieId = movie_ID)) = 0 THEN
						INSERT INTO genres_in_movies VALUES(genre_ID, movie_ID);
				END IF;
		ELSE
			SET movie_ID = 'na';
        END IF;
		
END $$