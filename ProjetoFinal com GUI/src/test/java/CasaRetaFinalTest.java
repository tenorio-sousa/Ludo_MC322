import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.CasaRetaFinal;
import com.ludo.jogo.game.enums.Cor;

/**
 * Testes para a classe CasaRetaFinal.
 */
class CasaRetaFinalTest {

    /**
     * Testa se CasaRetaFinal é uma Casa, é segura e guarda corretamente a corPermitida.
     */
    @Test
    void casaRetaFinalEhCasaSeguraEGuardaCor() {
        CasaRetaFinal casa = new CasaRetaFinal(Cor.VERMELHO);

        // Verifica o tipo
        assertInstanceOf(Casa.class, casa);

        // Lista de peões deve começar vazia
        assertTrue(casa.getPeoes().isEmpty(), "CasaRetaFinal deve iniciar sem peões.");

        // Deve ser casa segura
        assertTrue(casa.isCasaSegura(), "CasaRetaFinal deve ser segura (sem capturas).");

        // Cor permitida deve ser armazenada corretamente
        assertEquals(Cor.VERMELHO, casa.getCorPermitida(),
                "CasaRetaFinal deve armazenar corretamente a corPermitida.");
    }
}
