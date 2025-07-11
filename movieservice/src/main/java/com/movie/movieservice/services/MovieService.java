package com.movie.movieservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.movie.movieservice.dtos.requests.MovieRequestDto;
import com.movie.movieservice.entities.Movie;
import com.movie.movieservice.enums.MovieStatus;
import com.movie.movieservice.exceptions.CustomException;
import com.movie.movieservice.exceptions.ErrorCode;
import com.movie.movieservice.mappers.MovieMapper;
import com.movie.movieservice.repositories.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie createMovie(MovieRequestDto moviedDto) {
        Movie movie = MovieMapper.INSTANCE.toMovie(moviedDto);
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getNowShowingMovies() {
        return movieRepository.findMoviesByStatus(MovieStatus.NOW_SHOWING);
    }

    public List<Movie> getComingSoonMovies() {
        return movieRepository.findMoviesByStatus(MovieStatus.UPCOMING);
    }

    public Movie getMovieDetail(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.orElse(null);
    }

    public Movie updateExistingMovie(Long id, MovieRequestDto moviedDto) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            Movie existingMovie = movie.get();
            MovieMapper.INSTANCE.updateMovieFromDto(moviedDto, existingMovie);
            return movieRepository.save(existingMovie);
        }
        return movie.orElse(null);
    }

    public Movie deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_DONT_EXISTED));

        movieRepository.delete(movie);

        return movie;
    }

    public Page<Movie> getMoviesForAdminTable(int page, int size, String sortBy, String orderBy) {
        Pageable pageable;
        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        }
        return movieRepository.findAll(pageable);
    }
}