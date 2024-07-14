package com.rai.movieapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rai.movieapi.dto.MovieDto;
import com.rai.movieapi.dto.MoviePageResponse;
import com.rai.movieapi.entity.Movie;
import com.rai.movieapi.exception.EmptyFileException;
import com.rai.movieapi.service.MovieService;
import com.rai.movieapi.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException {
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File is empty! Please send another file!");
        }
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize
            ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false)String dir
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, dir));
    }


    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
