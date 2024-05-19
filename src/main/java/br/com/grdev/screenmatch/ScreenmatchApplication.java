package br.com.grdev.screenmatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.grdev.screenmatch.model.DadosEpisodio;
import br.com.grdev.screenmatch.model.DadosSerie;
import br.com.grdev.screenmatch.model.DadosTemporada;
import br.com.grdev.screenmatch.service.ConsumoApi;
import br.com.grdev.screenmatch.service.ConverteDado;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi =  new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=f5f60b03");

		System.out.println(json);

		ConverteDado converso = new ConverteDado();
		DadosSerie dados = converso.obterDados(json, DadosSerie.class);
		System.out.println(dados);

		json = consumoApi.obterDados("https://omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=6585022c");
		DadosEpisodio dadosEpisodio = converso.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> temporadas = new ArrayList<>();
		for(int i = 1; i <= dados.totalDeTemporadas(); i++) {
			json = consumoApi.obterDados ("https://www.omdbapi.com/?t=gilmore+girls&season=" + i + "&apikey=6585022c");
			DadosTemporada dadosTemporada = converso.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);
	}	

}
