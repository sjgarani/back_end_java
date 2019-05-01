package ame.desafio.star.wars.entity;

import java.util.concurrent.atomic.AtomicInteger;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "desafio", name = "planetas")
public class Planeta {

	private static final AtomicInteger COUNTER = new AtomicInteger();

	@PartitionKey
	private final int id;
	private String nome;
	private String clima;
	private String terreno;
	//private List<Aparicao> aparicoes;
	
	public Planeta() {	
		id = COUNTER.getAndIncrement();
	}
	
	public Planeta(String nome, String clima, String terreno) {
		id = COUNTER.getAndIncrement();
		this.nome = nome;
		this.clima = clima;
		this.terreno = terreno;
	}

	public Planeta(int id, String nome, String clima, String terreno) {
		this.id = id;
		this.nome = nome;
		this.clima = clima;
		this.terreno = terreno;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getClima() {
		return clima;
	}

	public void setClima(String clima) {
		this.clima = clima;
	}

	public String getTerreno() {
		return terreno;
	}

	public void setTerreno(String terreno) {
		this.terreno = terreno;
	}

}
