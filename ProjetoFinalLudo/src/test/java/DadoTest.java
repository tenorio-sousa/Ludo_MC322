import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ludo.jogo.game.Dado;

/**
 * Testes para a classe Dado.
 */
class DadoTest {

    /**
     * Testa se a rolagem do dado retorna sempre um valor entre 1 e 6.
     */
    @Test
    void rolagemSempreEntreUmESeis() {
        Dado dado = new Dado();

        for (int i = 0; i < 1000; i++) {  // várias rolagens para garantir
            int valor = dado.rolar();
            assertTrue(valor >= 1 && valor <= 6,
                    "O valor do dado deve estar entre 1 e 6, mas veio: " + valor);
        }
    }

    /**
     * Testa se o dado não retorna sempre o mesmo valor (verificação básica de aleatoriedade).
     */
    @Test
    void dadoNaoGeraSempreOMesmoValor() {
        Dado dado = new Dado();

        int primeira = dado.rolar();
        boolean houveDiferenca = false;

        for (int i = 0; i < 50; i++) {
            if (dado.rolar() != primeira) {
                houveDiferenca = true;
                break;
            }
        }

        assertTrue(houveDiferenca,
                "O dado parece estar retornando sempre o mesmo valor (" + primeira + ").");
    }

    /**
     * Testa se a rolagem não gera erros e retorna um valor inteiro.
     */
    @Test
    void rolarRetornaInteiroValido() {
        Dado dado = new Dado();
        int valor = dado.rolar();

        assertInstanceOf(Integer.class, valor, "A rolagem deve retornar um inteiro.");
    }
}
