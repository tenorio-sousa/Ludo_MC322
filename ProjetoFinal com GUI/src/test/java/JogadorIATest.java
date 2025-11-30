import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Jogador;
import com.ludo.jogo.game.JogadorIA;
import com.ludo.jogo.game.Jogavel;
import com.ludo.jogo.game.MotorJogo;
import com.ludo.jogo.game.Peao;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;

/**
 * Testes para a classe JogadorIA.
 */
class JogadorIATest {

    /**
     * Cria um MotorJogo com um único JogadorIA da cor informada.
     */
    private MotorJogo criarJogoComIA(Cor corJogador) {
        MotorJogo motor = new MotorJogo();

        List<Class<? extends Jogador>> tipos = new ArrayList<>();
        tipos.add(JogadorIA.class);

        List<Cor> cores = new ArrayList<>();
        cores.add(corJogador);

        motor.iniciarNovoJogo(tipos, cores);
        return motor;
    }

    /**
     * Testa se o jogador IA:
     * - é instância de JogadorIA
     * - implementa Jogavel
     * - possui a cor correta.
     */
    @Test
    void jogadorIAEhJogadorEImplementaJogavel() {
        MotorJogo motor = criarJogoComIA(Cor.VERDE);
        Jogador jogador = motor.getJogadores().getFirst();

        assertInstanceOf(JogadorIA.class, jogador,
                "Jogador criado deve ser do tipo JogadorIA.");
        assertTrue(jogador instanceof Jogavel,
                "JogadorIA deve implementar Jogavel.");
        assertEquals(Cor.VERDE, jogador.getCor(),
                "JogadorIA deve guardar corretamente a cor.");
    }

    /**
     * Testa se o jogador IA inicia com 4 peões,
     * todos da cor do jogador e no estado BASE.
     */
    @Test
    void jogadorIAIniciaComQuatroPeoesNaBase() {
        MotorJogo motor = criarJogoComIA(Cor.AMARELO);
        JogadorIA jogador = (JogadorIA) motor.getJogadores().getFirst();

        List<Peao> peoes = jogador.getPeoes();

        assertEquals(4, peoes.size(),
                "JogadorIA deve iniciar com 4 peões.");

        for (Peao p : peoes) {
            assertEquals(Cor.AMARELO, p.getCor(),
                    "Todos os peões devem ter a mesma cor do jogador.");
            assertEquals(EstadoPeao.BASE, p.getEstado(),
                    "Todos os peões devem iniciar no estado BASE.");
            assertNotNull(p.getCasa(),
                    "Peão deve ter uma casa inicial associada.");
        }
    }
}
