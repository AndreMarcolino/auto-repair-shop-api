package br.com.autorepairshop.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "veiculos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_veiculos_placa",
            columnNames = "placa"
        )
    }
)
public class VeiculoEntity {

    @Id
    private UUID id;

    @Embedded
    private PlacaEmbeddable placa;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano_fabricacao", nullable = false)
    private Integer anoFabricacao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected VeiculoEntity() {
    }

    public static VeiculoEntity paraEntity(Veiculo veiculo) {
        VeiculoEntity entidade = new VeiculoEntity();

        entidade.id = veiculo.getId();
        entidade.placa = new PlacaEmbeddable(veiculo.getPlaca().placa());
        entidade.marca = veiculo.getMarca();
        entidade.modelo = veiculo.getModelo();
        entidade.anoFabricacao = veiculo.getAnoFabricacao();
        entidade.criadoEm = veiculo.getCriadoEm();
        entidade.atualizadoEm = veiculo.getAtualizadoEm();

        return entidade;
    }

    public Veiculo paraDominio() {
        return Veiculo.reconstituir(
            id,
            Placa.criar(placa.getPlaca()),
            marca,
            modelo,
            anoFabricacao,
            criadoEm,
            atualizadoEm
        );
    }

    public UUID getId() {
        return id;
    }

    public PlacaEmbeddable getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}