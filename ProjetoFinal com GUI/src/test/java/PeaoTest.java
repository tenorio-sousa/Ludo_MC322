import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.CasaComum;
import com.ludo.jogo.game.CasaInicial;
import com.ludo.jogo.game.Peao;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;

/**
 * Testes para a classe Peao.
 */
class PeaoTest {

    /**
     * Testa se o construtor inicializa corretamente:
     * - cor
     * - casa inicial
     * - casa atual = casa inicial
     * - estado = BASE
     */
    @Test
    void construtorInicializaCorCasaEEstadoCorretamente() {
        Casa casaInicial = new CasaInicial();
        Peao peao = new Peao(Cor.AZUL, casaInicial);

        assertEquals(Cor.AZUL, peao.getCor(), "Peão deve guardar corretamente sua cor.");
        assertEquals(casaInicial, peao.getCasa(), "Peão deve começar na casa inicial.");
        assertEquals(EstadoPeao.BASE, peao.getEstado(), "Peão deve iniciar no estado BASE.");
    }

    /**
     * Testa se o setter de casa altera a referência corretamente.
     */
    @Test
    void setCasaMudaCasaCorretamente() {
        Casa casaInicial = new CasaInicial();
        Casa novaCasa = new CasaComum();

        Peao peao = new Peao(Cor.VERMELHO, casaInicial);
        peao.setCasa(novaCasa);

        assertEquals(novaCasa, peao.getCasa(),
                "setCasa deve alterar a casa atual do peão.");
    }

    /**
     * Testa se o setter de estado funciona.
     */
    @Test
    void setEstadoMudaEstadoCorretamente() {
        Casa casa = new CasaInicial();
        Peao peao = new Peao(Cor.VERDE, casa);

        peao.setEstado(EstadoPeao.JOGANDO);
        assertEquals(EstadoPeao.JOGANDO, peao.getEstado());
    }

    /**
     * Testa o método voltarParaBase():
     * - remove peão da casa atual
     * - volta para casa inicial
     * - estado volta para BASE
     */
    @Test
    void voltarParaBaseFuncionaCorretamente() {
        Casa casaInicial = new CasaInicial();
        Casa casaComum = new CasaComum();

        Peao peao = new Peao(Cor.AMARELO, casaInicial);

        // Simula peão indo para outra casa
        casaComum.adicionarPeao(peao);
        peao.setCasa(casaComum);
        peao.setEstado(EstadoPeao.JOGANDO);

        // Agora volta para base
        peao.voltarParaBase();

        // Verificações
        assertEquals(casaInicial, peao.getCasa(), "Peão deve voltar para a casa inicial.");
        assertEquals(EstadoPeao.BASE, peao.getEstado(), "Peão deve voltar ao estado BASE.");
        assertFalse(casaComum.getPeoes().contains(peao),
                "Peão deve ser removido da casa antiga.");
    }
}
