package br.com.grdev.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.grdev.screenmatch.model.DadosEpisodio;
import br.com.grdev.screenmatch.model.DadosSerie;
import br.com.grdev.screenmatch.model.DadosTemporada;
import br.com.grdev.screenmatch.model.Episodio;
import br.com.grdev.screenmatch.service.ConsumoApi;
import br.com.grdev.screenmatch.service.ConverteDado;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDado converteDados = new ConverteDado();
    private List<DadosTemporada> temporadas = new ArrayList<>();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f5f60b03";

    //"https://www.omdbapi.com/?t=gilmore+girls&apikey=f5f60b03"
    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = scanner.nextLine();
        nomeSerie = nomeSerie.replace("\s","+");
        var json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);

		DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);
     	
		for(int i = 1; i <= dadosSerie.totalDeTemporadas(); i++) {
			json = consumoApi.obterDados (ENDERECO + nomeSerie + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

        
        List<DadosEpisodio> DadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream())            
            .collect(Collectors.toList());

        System.out.println("\n top 5 episódios");
        DadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .peek(e -> System.out.println("N/A: " + e))
            .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            .peek(e -> System.out.println("sorted: " + e))
            .limit(5)
            .peek(e -> System.out.println("limit: " + e))
            .forEach(System.out::println);

        System.out.println("\n\n\n\n");
        
        List<Episodio> episodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream()
            .map(d -> new Episodio(t.numero(), d))
        ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("A partir de que ano você deseja ver os episódios? ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
            .filter(e -> e.getDataLancamento().isAfter(dataBusca))
            .forEach(e -> System.out.println(
                "Temporada: " + e.getTemporada() +
                    " Episódio: " + e.getTitulo() +
                        " Data lançamento: " +  e.getDataLancamento().format(formatador)
            ));

        System.out.println("Digite um trecho do título do episódio");
        var trechoTitulo = scanner.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
            .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
            .findFirst();

        if(episodioBuscado.isPresent()) {
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        } 
        else {
            System.out.println("Episódio não encontrado!");
        }

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(
                Collectors.groupingBy(Episodio::getTemporada,
                Collectors.averagingDouble(Episodio::getAvaliacao))
            );

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println(est);
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }

}
