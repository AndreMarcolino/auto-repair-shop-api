
package br.com.autorepairshop.domain.veiculo;

import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;

public class Veiculo {

    private final UUID id;
    private Placa placa;
    private String marca;
    private String modelo;
    private Integer anoFabricacao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    private Veiculo(
            UUID id,
            Placa placa,
            String marca,
            String modelo,
            Integer anoFabricacao,
            Instant criadoEm,
            Instant atualizadoEm) {

        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Veiculo criar(
            Placa placa,
            String marca,
            String modelo,
            Integer anoFabricacao) {

    	validarPlaca(placa);
        validarMarca(marca);
        validarModelo(modelo);
        validarAnoFabricacao(anoFabricacao);
        Instant agora = Instant.now();

        return new Veiculo(
            UUID.randomUUID(),
            placa,
            marca,
            modelo,
            anoFabricacao,
            agora,
            agora
        );
    }

    public void atualizarCadastro(
            Placa placa,
            String marca,
            String modelo,
            Integer anoFabricacao) {

        this.placa = validarPlaca(placa);
        this.marca = validarMarca(marca);
        this.modelo = validarModelo(modelo);
        this.anoFabricacao = validarAnoFabricacao(anoFabricacao);
        this.atualizadoEm = Instant.now();
    }



    private static Placa validarPlaca(Placa placa) {
        if (placa == null) {
            throw new DomainException(DomainError.PLACA_INVALIDA);
        }
        return placa;
    }

    private static String validarMarca(String marca) {
        if (marca == null || marca.isBlank()) {
            throw new DomainException(
                DomainError.MARCA_VEICULO_INVALIDA
            );
        }
        return marca.trim();
    }

    private static String validarModelo(String modelo) {
        if (modelo == null || modelo.isBlank()) {
            throw new DomainException(
                DomainError.MODELO_VEICULO_INVALIDO
            );
        }
        return modelo.trim();
    }

    private static Integer validarAnoFabricacao(Integer ano) {
        if (ano == null || ano < 1886 || ano > 2100) {
            throw new DomainException(
                DomainError.ANO_FABRICACAO_INVALIDO
            );
        }
        return ano;
    }

    public UUID getId() { return id; }
    public Placa getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAnoFabricacao() { return anoFabricacao; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    
    public static Veiculo reconstituir(
            UUID id,
            Placa placa,
            String marca,
            String modelo,
            Integer anoFabricacao,
            Instant criadoEm,
            Instant atualizadoEm) {

        return new Veiculo(
            id,
            placa,
            marca,
            modelo,
            anoFabricacao,
            criadoEm,
            atualizadoEm
        );
    }
}
