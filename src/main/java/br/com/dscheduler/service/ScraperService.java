package br.com.dscheduler.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@FeignClient("d-server-scraper")
public interface ScraperService {

    @GetMapping("/scraper/pagamento-mes-atual")
    void pagamentoMesAtual();

    @GetMapping("/scraper/pagamento-mes-anterior")
    void pagamentoMesAnterior();

    @GetMapping("/scraper/empenho-ano-atual")
    void empenhoAnoAtual();

    @GetMapping("/scraper/empenho-ano-anterior")
    void empenhoAnoAnterior();
}
