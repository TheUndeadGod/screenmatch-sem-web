package br.com.grdev.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.grdev.screenmatch.model.DadosSerie;
import br.com.grdev.screenmatch.service.ConsumoApi;
import br.com.grdev.screenmatch.service.ConverteDado;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var api =  new ConsumoApi();
		var json = api.obterDados("https://www.omdbapi.com/?t=xena&apikey=f5f60b03");

		System.out.println(json);

		ConverteDado converso = new ConverteDado();
		DadosSerie dados = converso.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}

}
