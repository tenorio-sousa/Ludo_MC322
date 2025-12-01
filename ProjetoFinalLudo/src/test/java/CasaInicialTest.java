import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.CasaInicial;

/**
 * Testes para a classe CasaInicial.
 */
class CasaInicialTest {

    /**
     * Testa se CasaInicial é uma Casa e se não é segura (por design).
     */
    @Test
    void casaInicialEhCasaENaoEhSegura() {
        Casa casa = new CasaInicial();

        // Verifica o tipo
        assertInstanceOf(Casa.class, casa);

        // Lista de peões deve começar vazia
        assertTrue(casa.getPeoes().isEmpty(), "CasaInicial deve iniciar sem peões.");

        // Não é casa segura (por escolha de design)
        assertFalse(casa.isCasaSegura(), "CasaInicial não deve ser segura (por design).");
    }
}
