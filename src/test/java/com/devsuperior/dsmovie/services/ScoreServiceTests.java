package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private Long existingMovieId, nonExistingMovieId;
	private UserEntity userEntity;
	private MovieEntity movieEntity;
	private ScoreDTO scoreDTO;
	private Double score;

	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		userEntity = UserFactory.createUserEntity();

		movieEntity = MovieFactory.createMovieEntity();

		ScoreEntity existingScore = new ScoreEntity();
		existingScore.setValue(3.0);
		existingScore.setMovie(movieEntity);
		movieEntity.getScores().add(existingScore);

		score = 4.0;
		scoreDTO = ScoreFactory.createScoreDTO();

		Mockito.when(userService.authenticated()).thenReturn(userEntity);

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(scoreRepository.saveAndFlush(any()))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Mockito.when(movieRepository.save(any()))
				.thenAnswer(invocation -> invocation.getArgument(0));
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		movieEntity.setId(existingMovieId);
		scoreDTO = new ScoreDTO(existingMovieId, score);

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());

		Mockito.verify(scoreRepository, Mockito.times(1))
				.saveAndFlush(any());

		Mockito.verify(movieRepository, Mockito.times(1))
				.save(any());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		ScoreDTO dto = new ScoreDTO(nonExistingMovieId, score);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(dto);
		});

		Mockito.verify(scoreRepository, Mockito.never())
				.saveAndFlush(any());
	}
}