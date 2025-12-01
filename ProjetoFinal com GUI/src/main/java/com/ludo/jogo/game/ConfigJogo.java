package com.ludo.jogo.game;

import java.util.ArrayList;
import java.util.List;

import com.ludo.jogo.game.enums.Cor;

/** Armazena a configuração escolhida no menu (tipos de jogador e cores) para ser usada ao iniciar o MotorJogo. */
public class ConfigJogo {

    // ATRIBUTOS

    private static List<Class<? extends Jogador>> tiposJogadores = new ArrayList<>();
    private static List<Cor> coresJogadores = new ArrayList<>();


    // MÉTODOS

    // Setters / Configuração

    /**
     * Define as configurações da próxima partida.
     *
     * @param tipos Lista contendo as classes dos jogadores (Humano ou IA).
     * @param cores Lista contendo as cores selecionadas para cada jogador.
     */
    public static void configurar(List<Class<? extends Jogador>> tipos, List<Cor> cores) {
        tiposJogadores = new ArrayList<>(tipos);
        coresJogadores = new ArrayList<>(cores);
    }

    // Getters

    /**
     * Retorna a lista de tipos de jogadores configurada.
     *
     * @return Lista de classes que estendem Jogador.
     */
    public static List<Class<? extends Jogador>> getTiposJogadores() {
        return tiposJogadores;
    }

    /**
     * Retorna a lista de cores configurada.
     *
     * @return Lista de enums Cor.
     */
    public static List<Cor> getCoresJogadores() {
        return coresJogadores;
    }
}