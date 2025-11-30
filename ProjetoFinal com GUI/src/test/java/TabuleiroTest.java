import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.Peao;
import com.ludo.jogo.game.Tabuleiro;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;

/**
 * Testes para a classe Tabuleiro.
 */
class TabuleiroTest {

    /**
     * Testa se o construtor cria bases não nulas para todas as cores.
     */
    @Test
    void basesNaoSaoNulasParaTodasAsCores() {
        Tabuleiro tabuleiro = new Tabuleiro();

        assertNotNull(tabuleiro.getCasaBase(Cor.VERMELHO), "Base VERMELHO não pode ser nula.");
        assertNotNull(tabuleiro.getCasaBase(Cor.VERDE),    "Base VERDE não pode ser nula.");
        assertNotNull(tabuleiro.getCasaBase(Cor.AMARELO),  "Base AMARELO não pode ser nula.");
        assertNotNull(tabuleiro.getCasaBase(Cor.AZUL),     "Base AZUL não pode ser nula.");
    }

    /**
     * Testa a lógica da casa de destino quando o peão está na base:
     * - Só sai da base com valor 6;
     * - Com valor diferente de 6, o destino é null.
     */
    @Test
    void peaoNaBaseSoSaiComSeis() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Casa baseVerde = tabuleiro.getCasaBase(Cor.VERDE);

        Peao peao = new Peao(Cor.VERDE, baseVerde);

        // Valor diferente de 6: não pode sair da base
        Casa destinoComTres = tabuleiro.getCasaDestino(peao, 3);
        assertNull(destinoComTres, "Peão na base não deve sair com valor diferente de 6.");

        // Valor 6: deve ir para uma casa do circuito (casa de saída)
        Casa destinoComSeis = tabuleiro.getCasaDestino(peao, 6);
        assertNotNull(destinoComSeis, "Peão na base deve ter destino ao tirar 6.");
        assertTrue(tabuleiro.isCasaDoCircuito(destinoComSeis),
                "Casa de saída deve fazer parte do circuito.");
    }

    /**
     * Testa se o Tabuleiro avança corretamente no circuito:
     * a partir da casa de saída, mover 3 casas deve aumentar o índice em 3 (mod 56).
     */
    @Test
    void getCasaDestinoAvancaCorretamenteNoCircuito() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Casa base = tabuleiro.getCasaBase(Cor.VERMELHO);
        Peao peao = new Peao(Cor.VERMELHO, base);

        // Sai da base com 6
        Casa casaSaida = tabuleiro.getCasaDestino(peao, 6);
        assertNotNull(casaSaida);
        assertTrue(tabuleiro.isCasaDoCircuito(casaSaida));

        // Coloca o peão efetivamente na casa de saída
        casaSaida.adicionarPeao(peao);
        peao.setCasa(casaSaida);
        peao.setEstado(EstadoPeao.JOGANDO);

        int indiceInicial = tabuleiro.getIndiceCircuito(casaSaida);
        assertTrue(indiceInicial >= 0);

        // Avança 3 casas no circuito
        Casa destino = tabuleiro.getCasaDestino(peao, 3);
        assertNotNull(destino);
        assertTrue(tabuleiro.isCasaDoCircuito(destino));

        int indiceDestino = tabuleiro.getIndiceCircuito(destino);
        assertEquals((indiceInicial + 3) % 56, indiceDestino,
                "O índice da casa de destino deve ser o índice inicial + 3 (mod 56).");
    }

    /**
     * Testa se moverPeao:
     * - remove o peão da casa antiga;
     * - adiciona na casa de destino;
     * - atualiza a casa do peão;
     * - ajusta o estado para JOGANDO ao sair da base e ir para o circuito.
     */
    @Test
    void moverPeaoAtualizaCasaEEstado() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Casa base = tabuleiro.getCasaBase(Cor.VERMELHO);
        Peao peao = new Peao(Cor.VERMELHO, base);

        // Garantimos que a base saiba que o peão está lá
        base.adicionarPeao(peao);

        // Destino de saída com valor 6
        Casa casaSaida = tabuleiro.getCasaDestino(peao, 6);
        assertNotNull(casaSaida);

        tabuleiro.moverPeao(peao, casaSaida);

        assertEquals(casaSaida, peao.getCasa(),
                "Peão deve estar na casa de saída após o movimento.");
        assertFalse(base.getPeoes().contains(peao),
                "Peão não deve mais estar na base.");
        assertTrue(casaSaida.getPeoes().contains(peao),
                "Peão deve estar registrado na nova casa.");
        assertEquals(EstadoPeao.JOGANDO, peao.getEstado(),
                "Peão deve estar no estado JOGANDO após sair da base.");
    }

    /**
     * Testa verificarBloqueio em uma casa do circuito:
     * - Deve retornar true quando há mais de um peão na mesma casa;
     * - Usamos a casa de saída como uma casa de circuito válida.
     */
    @Test
    void verificarBloqueioRetornaTrueQuandoMaisDeUmPeaoNaMesmaCasaDoCircuito() {
        Tabuleiro tabuleiro = new Tabuleiro();

        // Criamos dois peões de cores diferentes
        Casa baseVermelha = tabuleiro.getCasaBase(Cor.VERMELHO);
        Casa baseAzul = tabuleiro.getCasaBase(Cor.AZUL);

        Peao p1 = new Peao(Cor.VERMELHO, baseVermelha);
        Peao p2 = new Peao(Cor.AZUL, baseAzul);

        // Pegamos a casa de saída do vermelho
        Casa casaSaidaVermelha = tabuleiro.getCasaDestino(p1, 6);
        assertNotNull(casaSaidaVermelha);
        assertTrue(tabuleiro.isCasaDoCircuito(casaSaidaVermelha));

        // Colocamos os dois peões na mesma casa do circuito
        casaSaidaVermelha.adicionarPeao(p1);
        casaSaidaVermelha.adicionarPeao(p2);

        assertTrue(tabuleiro.verificarBloqueio(casaSaidaVermelha),
                "Deve haver bloqueio quando há mais de um peão em uma casa do circuito.");
    }

    /**
     * Testa que na reta final, mesmo com mais de um peão na mesma casa,
     * verificarBloqueio deve retornar false.
     */
    @Test
    void verificarBloqueioRetornaFalseNaRetaFinal() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Cor cor = Cor.VERMELHO;

        Casa base = tabuleiro.getCasaBase(cor);
        Peao p1 = new Peao(cor, base);

        // Garante que a base saiba que o peão está lá
        base.adicionarPeao(p1);

        // Primeiro movimento: sair da base
        Casa casaSaida = tabuleiro.getCasaDestino(p1, 6);
        tabuleiro.moverPeao(p1, casaSaida);

        // Agora vamos andando de 1 em 1 até entrar na reta final
        while (!tabuleiro.isCasaDaRetaFinal(cor, p1.getCasa())) {
            Casa proxima = tabuleiro.getCasaDestino(p1, 1);
            tabuleiro.moverPeao(p1, proxima);
        }

        Casa casaRetaFinal = p1.getCasa();
        assertTrue(tabuleiro.isCasaDaRetaFinal(cor, casaRetaFinal),
                "A casa atual deve pertencer à reta final da cor vermelha.");

        // Adiciona um segundo peão na mesma casa da reta final
        Peao p2 = new Peao(cor, base);
        casaRetaFinal.adicionarPeao(p2);
        p2.setCasa(casaRetaFinal);

        // Mesmo com mais de um peão, NÃO deve haver bloqueio na reta final
        assertFalse(tabuleiro.verificarBloqueio(casaRetaFinal),
                "Não deve haver bloqueio em casas da reta final, mesmo com mais de um peão.");
    }

    /**
     * Testa os métodos de índice:
     * - getIndiceCircuito retorna -1 para uma casa que não é do circuito (base);
     * - getIndiceRetaFinal retorna -1 para uma casa que não está na reta final.
     */
    @Test
    void indicesInvalidosRetornamMenosUm() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Casa base = tabuleiro.getCasaBase(Cor.AMARELO);

        assertFalse(tabuleiro.isCasaDoCircuito(base),
                "Base não deve ser considerada casa do circuito.");
        assertEquals(-1, tabuleiro.getIndiceCircuito(base),
                "getIndiceCircuito deve retornar -1 para casas fora do circuito.");

        assertEquals(-1, tabuleiro.getIndiceRetaFinal(Cor.AMARELO, base),
                "getIndiceRetaFinal deve retornar -1 para casas fora da reta final.");
    }
}
