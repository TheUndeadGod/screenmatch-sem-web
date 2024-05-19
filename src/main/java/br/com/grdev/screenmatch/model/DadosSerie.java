package br.com.grdev.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
    @JsonAlias("Title") String titulo,
    @JsonAlias("totalSeasons") String totalDeTemporadas,
    @JsonAlias("imdbRating")  String avaliacao) {

}
