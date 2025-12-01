import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.MotorJogo;
import com.ludo.jogo.game.enums.EstadoJogo;
import com.ludo.jogo.game.exceptions.MovimentoInvalidoException;
import com.ludo.jogo.game.exceptions.NenhumMovimentoPossivelException;

/**
 * Testes básicos para a classe MotorJogo.
 */
class MotorJogoTest {

    /**
     * Testa se o construtor inicializa o jogo no estado ENCERRADO
     * e sem tabuleiro, jogadores ou jogador atual definidos.
     */
    @Test
    void construtorIniciaEstadoEncerradoESemComponentes() {
        MotorJogo motor = new MotorJogo();

        assertEquals(EstadoJogo.ENCERRADO, motor.getEstado(),
                "MotorJogo deve iniciar no estado ENCERRADO.");

        assertNull(motor.getTabuleiro(), "Tabuleiro deve iniciar nulo.");
        assertNull(motor.getJogadorAtual(), "Jogador atual deve iniciar nulo.");
        assertNull(motor.getJogadores(), "Lista de jogadores deve iniciar nula.");
        assertEquals(0, motor.getValorDadoAtual(),
                "Valor do dado deve iniciar em 0 (valor padrão).");
    }

    /**
     * Testa se chamar rolarDado() antes do jogo ser iniciado
     * (estado diferente de EM_JOGO) não altera o estado nem o valor do dado
     * e não lança exceção.
     */
    @Test
    void rolarDadoAntesDeIniciarNaoFazNada() throws NenhumMovimentoPossivelException, MovimentoInvalidoException {
        MotorJogo motor = new MotorJogo();

        // Estado inicial é ENCERRADO
        assertEquals(EstadoJogo.ENCERRADO, motor.getEstado());

        // Chamada deve simplesmente retornar sem fazer nada
        motor.rolarDado();

        // Continua tudo igual
        assertEquals(EstadoJogo.ENCERRADO, motor.getEstado(),
                "Estado não deve mudar ao rolar dado com jogo encerrado.");
        assertEquals(0, motor.getValorDadoAtual(),
                "Valor do dado não deve ser alterado quando o jogo não está EM_JOGO.");
    }

    /**
     * Testa se chamar finalizarTurno() com o jogo em estado ENCERRADO
     * não lança exceção (apenas retorna).
     */
    @Test
    void finalizarTurnoComJogoEncerradoNaoLancaExcecao() {
        MotorJogo motor = new MotorJogo();

        // Se houver NullPointerException ou algo assim, o teste falha.
        assertDoesNotThrow(motor::finalizarTurno,
                "finalizarTurno() não deve lançar exceção quando o jogo não está EM_JOGO.");
    }
}
