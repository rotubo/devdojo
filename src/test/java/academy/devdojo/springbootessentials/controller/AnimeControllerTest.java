package academy.devdojo.springbootessentials.controller;

import academy.devdojo.springbootessentials.domain.Anime;
import academy.devdojo.springbootessentials.requests.AnimePostRequestBody;
import academy.devdojo.springbootessentials.requests.AnimePutRequestBody;
import academy.devdojo.springbootessentials.service.AnimeService;
import academy.devdojo.springbootessentials.util.AnimeCreator;
import academy.devdojo.springbootessentials.util.AnimePostRequestBodyCreator;
import academy.devdojo.springbootessentials.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeService;

    @BeforeEach()
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeService.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeService.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeService.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeService.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeService.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeService).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful(){

        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("listAll returns list of anime when successful")
    void listAll_ReturnsListOfAnime_WhenSuccessful(){

        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.listAll().getBody();

        Assertions.assertThat(animes)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findById returns when successful")
    void findById_ReturnsAnime_WhenSuccessful(){

        Long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = animeController.findById(1).getBody();

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns a list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful(){

        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName returns an empty list of anime when is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenIsNotFound(){

        BDDMockito.when(animeService.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful(){

        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){

        Assertions.assertThatCode( () -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
            .doesNotThrowAnyException();

        ResponseEntity<?> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

        Assertions.assertThat(entity).isNotNull();

        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful(){

        Assertions.assertThatCode( () -> animeController.delete(1L))
                .doesNotThrowAnyException();

        ResponseEntity<?> entity = animeController.delete(1L);

        Assertions.assertThat(entity).isNotNull();

        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
}