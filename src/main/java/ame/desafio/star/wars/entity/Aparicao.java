package ame.desafio.star.wars.entity;

import java.util.concurrent.atomic.AtomicInteger;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "desafio", name = "aparicoes")
public class Aparicao {

	private static final AtomicInteger COUNTER = new AtomicInteger();

	@PartitionKey
	private final int id;
	@Field(name = "filme_id")
	private int filmeId;
	@Field(name = "planeta_id")
	private int planetaId;
	private int quantidade;

	public Aparicao() {
		id = COUNTER.getAndIncrement();
	}

	public Aparicao(int filmeId, int planetaId, int quantidade) {
		id = COUNTER.getAndIncrement();
		this.filmeId = filmeId;
		this.planetaId = planetaId;
		this.quantidade = quantidade;
	}

	public Aparicao(int id, int filmeId, int planetaId, int quantidade) {
		this.id = id;
		this.filmeId = filmeId;
		this.planetaId = planetaId;
		this.quantidade = quantidade;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the filmeId
	 */
	public int getFilmeId() {
		return filmeId;
	}

	/**
	 * @return the planetaId
	 */
	public int getPlanetaId() {
		return PlanetaId;
	}

	/**
	 * @return the quantidade
	 */
	public int getQuantidade() {
		return quantidade;
	}

	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}


	
}
