package com.company.Service;

import com.company.DTO.MovieDTO;
import com.company.Entity.Movie;

import java.util.List;

public interface MovieService {

    public void addNewMovie(final MovieDTO movieDTO);
    public List<Movie> findMovieByTitle(final String title);
    public List<Movie> findMovieByIDAuthor(final Long IDAuthor);
    public Long countAddedMoviesByIdAuthor(final Long IDAuthor);
}
