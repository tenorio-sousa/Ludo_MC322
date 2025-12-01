import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Jogador;
import com.ludo.jogo.game.JogadorHumano;
import com.ludo.jogo.game.Jogavel;
import com.ludo.jogo.game.MotorJogo;
import com.ludo.jogo.game.Peao;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;

/**
 * Testes para a classe JogadorHumano.
 */
class JogadorHumanoTest {

    /**
     * Cria um MotorJogo com um único JogadorHumano da cor informada.
     */
    private MotorJogo criarJogoComHumano(Cor corJogador) {
        MotorJogo motor = new MotorJogo();

        List<Class<? extends Jogador>> tipos = new ArrayList<>();
        tipos.add(JogadorHumano.class);

        List<Cor> cores = new ArrayList<>();
        cores.add(corJogador);

        motor.iniciarNovoJogo(tipos, cores);
        return motor;
    }

    /**
     * Testa se o jogador humano:
     * - é instância de Jogador
     * - implementa Jogavel
     * - possui a cor correta.
     */
    @Test
    void jogadorHumanoEhJogadorEImplementaJogavel() {
        MotorJogo motor = criarJogoComHumano(Cor.VERMELHO);
        Jogador jogador = motor.getJogadores().getFirst();

        assertInstanceOf(JogadorHumano.class, jogador,
                "Jogador criado deve ser do tipo JogadorHumano.");
        assertTrue(jogador instanceof Jogavel,
                "JogadorHumano deve implementar Jogavel.");
        assertEquals(Cor.VERMELHO, jogador.getCor(),
                "JogadorHumano deve guardar corretamente a cor.");
    }

    /**
     * Testa se o jogador humano inicia com 4 peões,
     * todos da cor do jogador e no estado BASE.
     */
    @Test
    void jogadorHumanoIniciaComQuatroPeoesNaBase() {
        MotorJogo motor = criarJogoComHumano(Cor.AZUL);
        JogadorHumano jogador = (JogadorHumano) motor.getJogadores().getFirst();

        List<Peao> peoes = jogador.getPeoes();

        assertEquals(4, peoes.size(),
                "JogadorHumano deve iniciar com 4 peões.");

        for (Peao p : peoes) {
            assertEquals(Cor.AZUL, p.getCor(),
                    "Todos os peões devem ter a mesma cor do jogador.");
            assertEquals(EstadoPeao.BASE, p.getEstado(),
                    "Todos os peões devem iniciar no estado BASE.");
            assertNotNull(p.getCasa(),
                    "Peão deve ter uma casa inicial associada.");
        }
    }
}
