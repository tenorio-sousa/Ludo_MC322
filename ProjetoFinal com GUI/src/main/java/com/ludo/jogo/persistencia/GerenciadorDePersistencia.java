package com.ludo.jogo.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ludo.jogo.game.MotorJogo;
import com.ludo.jogo.game.exceptions.SlotSaveIndisponivelException;

/** Classe utilitária responsável por salvar, carregar e apagar o estado do jogo em arquivos. */
public class GerenciadorDePersistencia {


    // ATRIBUTOS

    private static final String SAVE_PATH = "save_slot_";


    // MÉTODOS

    /**
     * Serializa o objeto MotorJogo e o salva no arquivo.
     *
     * @param motorJogo O objeto do jogo a ser salvo;
     * @param slot O número do slot (1, 2, 3 ou 4) onde será salvo.
     */
    public static void salvarJogo(MotorJogo motorJogo, int slot) {
        String arquivoNome = SAVE_PATH + slot + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoNome))) {
            oos.writeObject(motorJogo);
            System.out.println("Jogo salvo com sucesso no slot " + slot);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    /**
     * Tenta carregar o jogo. Se o arquivo não existir, lança a exceção informando que o slot está vazio.
     *
     * @param slot O número do slot a ser carregado.
     * @return O objeto MotorJogo recuperado.
     * @throws SlotSaveIndisponivelException Se o arquivo não existir (vazio) ou erro de leitura.
     */
    public static MotorJogo carregarJogo(int slot) throws SlotSaveIndisponivelException {
        String arquivoNome = SAVE_PATH + slot + ".dat";
        File arquivo = new File(arquivoNome);

        if (!arquivo.exists()) {
            throw new SlotSaveIndisponivelException("Slot " + slot + " vazio! Não há jogo salvo para carregar.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (MotorJogo) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new SlotSaveIndisponivelException("Erro ao ler o save do slot " + slot + ": " + e.getMessage());
        }
    }

    /**
     * Apaga o arquivo de save do slot especificado.
     *
     * @param slot O número do slot a ser apagado.
     * @return True se apagou com sucesso, False se o arquivo não existia ou falhou.
     */
    public static boolean apagarSave(int slot) {
        String arquivoNome = SAVE_PATH + slot + ".dat";
        File arquivo = new File(arquivoNome);

        if (arquivo.exists()) {
            boolean deletado = arquivo.delete();
            if (deletado) {
                System.out.println("Save do slot " + slot + " foi apagado.");
            }
            else {
                System.err.println("Falha ao tentar apagar o save do slot " + slot + ".");
            }
            return deletado;
        }
        return false;
    }

}