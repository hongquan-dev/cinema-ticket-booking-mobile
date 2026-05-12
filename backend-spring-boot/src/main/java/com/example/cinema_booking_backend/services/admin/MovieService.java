package com.example.cinema_booking_backend.services.admin;

import com.cloudinary.Cloudinary;
import com.example.cinema_booking_backend.dtos.movie.MovieListResponse;
import com.example.cinema_booking_backend.models.Movie;
import com.example.cinema_booking_backend.repositories.admin.MovieRepository;
import org.springframework.transaction.annotation.Transactional; // Correct import for Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Transactional(rollbackFor = Exception.class)
    public Movie saveMovieWithPoster(Movie movie, MultipartFile file) {
        try {
            // Check if file exists and is not empty before uploading
            if (file != null && !file.isEmpty()) {
                // Upload image to 'movies' folder on Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("folder", "moviePosters"));

                // Retrieve the secure HTTPS URL from Cloudinary result
                String imageUrl = (String) uploadResult.get("secure_url");
                movie.setPosterUrl(imageUrl);
            }

            // Save the movie entity (including JSONB fields) to PostgresQL
            return movieRepository.save(movie);

        } catch (IOException e) {
            // Handle file reading or Cloudinary connection issues
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        } catch (Exception e) {
            // Handle database errors or other system exceptions
            throw new RuntimeException("System error while saving movie: " + e.getMessage());
        }
    }

    public Page<MovieListResponse> getAllMoviesPaged(int page, int size, String search) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");
        try {
            int internalPage = page - 1;

            // Sort by releaseDate descending (newest movies first)
            // If you want oldest first, use Sort.by("releaseDate").ascending()
            Pageable pageable = PageRequest.of(internalPage, size, Sort.by("releaseDate").ascending());

            Page<Movie> moviePage;

            if (search != null && !search.trim().isEmpty()) {
                moviePage = movieRepository.findByMovieNameContainingIgnoreCase(search.trim(), pageable);
            } else {
                moviePage = movieRepository.findAll(pageable);
            }

            return moviePage.map(movie -> new MovieListResponse(
                    movie.getId(),
                    movie.getMovieName(),
                    movie.getPosterUrl(),
                    movie.getReleaseDate(),
                    movie.getSelectedGenres(),
                    movie.getSelectedFormats(),
                    movie.getDuration(),
                    movie.getCreatedAt()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Database error while retrieving movies: " + e.getMessage());
        }
    }

    public Movie getMovieById(UUID id) {
        // Find movie by ID or throw exception if not found
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    private String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("moviePosters/")) return null;
        try {
            String partAfterFolder = url.substring(url.indexOf("moviePosters/"));
            return partAfterFolder.substring(0, partAfterFolder.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Movie updateMovie(UUID id, Movie movieDetails, MultipartFile file) {
        // Find current movies
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        // Get updated basic information from movieDetails
        existingMovie.setMovieName(movieDetails.getMovieName());
        existingMovie.setDescription(movieDetails.getDescription());
        existingMovie.setAgeRating(movieDetails.getAgeRating());
        existingMovie.setDuration(movieDetails.getDuration());
        existingMovie.setReleaseDate(movieDetails.getReleaseDate());
        existingMovie.setSelectedGenres(movieDetails.getSelectedGenres());
        existingMovie.setSelectedFormats(movieDetails.getSelectedFormats());
        existingMovie.setSelectedLanguages(movieDetails.getSelectedLanguages());
        existingMovie.setSelectedDirectors(movieDetails.getSelectedDirectors());
        existingMovie.setSelectedActors(movieDetails.getSelectedActors());
        existingMovie.setSelectedCountries(movieDetails.getSelectedCountries());

        // 3. Handle new poster
        if (file != null && !file.isEmpty()) {
            try {
                // Delete old photos on Cloudinary (if any).
                String oldUrl = existingMovie.getPosterUrl();
                String publicId = extractPublicIdFromUrl(oldUrl);

                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
                    System.out.println("Deleted old image: " + publicId);
                }

                // Upload new poster
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("folder", "moviePosters"));

                String newImageUrl = (String) uploadResult.get("secure_url");
                existingMovie.setPosterUrl(newImageUrl);

            } catch (IOException e) {
                throw new RuntimeException("Image update failed: " + e.getMessage());
            }
        }

        return movieRepository.save(existingMovie);
    }

    // Delete movie by ID and remove its poster from Cloudinary
    @Transactional(rollbackFor = Exception.class)
    public void deleteMovie(UUID id) {
        // 1. Find existing movie
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No movie were found with ID: " + id));

        try {
            // 2. Delete poster from Cloudinary if it exists
            String posterUrl = movie.getPosterUrl();
            String publicId = extractPublicIdFromUrl(posterUrl);

            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
                System.out.println("Cloudinary deletion result: " + result.get("result"));
            }

            // 3. Delete from Database
            movieRepository.delete(movie);

        } catch (IOException e) {
            throw new RuntimeException("Error when deleting photos on Cloudinary: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("System error when deleting a movie: " + e.getMessage());
        }
    }

    public List<Movie> getComingSoonMovies(LocalDate date) {
        try {
            List<Movie> movies = movieRepository.findByReleaseDateAfterOrderByReleaseDateAsc(date);

            return movies;
        } catch (Exception e) {
            throw new RuntimeException("Error when fetching movie coming-soon list: " + e.getMessage());
        }
    }
}