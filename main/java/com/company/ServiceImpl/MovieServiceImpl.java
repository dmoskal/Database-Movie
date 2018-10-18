package com.company.ServiceImpl;

import com.company.DAO.MovieCRUDRepository;
import com.company.DTO.MovieDTO;
import com.company.Entity.Movie;
import com.company.Service.MovieService;
import com.company.Validator.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service("movieService")
public class MovieServiceImpl implements MovieService {

    private MovieCRUDRepository movieCRUDRepository;

    @Autowired
    public MovieServiceImpl(MovieCRUDRepository movieCRUDRepository) {
        this.movieCRUDRepository = movieCRUDRepository;
    }

    @Override
    public void addNewMovie(final MovieDTO movieDTO) {
        movieCRUDRepository.save(new Mapped().mappedToMovie(movieDTO));
    }

    @Override
    public List<Movie> findMovieByTitle(final String title) {
        return movieCRUDRepository.findByTitleIgnoreCaseContaining(title);
    }

    @Override
    public List<Movie> findMovieByIDAuthor(final Long IDAuthor) {
        return movieCRUDRepository.findByIDAuthor(IDAuthor);
    }

    @Override
    public Long countAddedMoviesByIdAuthor(final Long IDAuthor) {
        return movieCRUDRepository.countByIDAuthor(IDAuthor);
    }
}
