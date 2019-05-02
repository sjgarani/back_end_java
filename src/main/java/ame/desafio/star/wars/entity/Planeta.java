package ame.desafio.star.wars.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace = "desafio", name = "planetas")
public class Planeta {

	private static final AtomicInteger COUNTER = new AtomicInteger();

	@PartitionKey
	private final int id;
	private String nome;
	private String clima;
	private String terreno;
	@Transient
	private List<Aparicao> aparicoes = new ArrayList<Aparicao>();
	
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

	public List<Aparicao> getAparicoes() {
		return aparicoes;
	}

	public void setAparicoes(List<Aparicao> aparicoes) {
		this.aparicoes = aparicoes;
	}



}
