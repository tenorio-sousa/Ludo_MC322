import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.CasaComum;

/**
 * Testes para a classe CasaComum.
 */
class CasaComumTest {

    /**
     * Testa se CasaComum é uma Casa e se não é segura.
     */
    @Test
    void casaComumEhCasaENaoEhSegura() {
        Casa casa = new CasaComum();

        // Verifica o tipo
        assertInstanceOf(Casa.class, casa);

        // Lista de peões deve começar vazia
        assertTrue(casa.getPeoes().isEmpty(), "CasaComum deve iniciar sem peões.");

        // Não é casa segura
        assertFalse(casa.isCasaSegura(), "CasaComum não deve ser segura.");
    }
}
