import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.CasaSegura;

/**
 * Testes para a classe CasaSegura.
 */
class CasaSeguraTest {

    /**
     * Testa se CasaSegura é uma Casa e se é segura.
     */
    @Test
    void casaSeguraEhCasaESegura() {
        Casa casa = new CasaSegura();

        // Verifica o tipo
        assertInstanceOf(Casa.class, casa);

        // Lista de peões deve começar vazia
        assertTrue(casa.getPeoes().isEmpty(), "CasaSegura deve iniciar sem peões.");

        // Deve ser casa segura
        assertTrue(casa.isCasaSegura(), "CasaSegura deve ser segura (sem capturas).");
    }
}
