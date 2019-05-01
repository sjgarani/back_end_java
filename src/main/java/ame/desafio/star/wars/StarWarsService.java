package ame.desafio.star.wars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ame.desafio.star.wars.entity.Planeta;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.cassandra.CassandraClient;
import io.vertx.reactivex.cassandra.CassandraRowStream;
import io.vertx.reactivex.cassandra.Mapper;
import io.vertx.reactivex.cassandra.MappingManager;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.json.Json;

public class StarWarsService extends AbstractVerticle {

	CassandraClient cassandraClient;

	@Override
	public void start() {

		CassandraClientOptions options = new CassandraClientOptions().addContactPoint("localhost")
				.setKeyspace("desafio");
		;
		cassandraClient = CassandraClient.createShared(vertx, options);

		Router router = Router.router(vertx);
		router.route("/api/planeta*").handler(BodyHandler.create());
		router.get("/api/planetas").handler(this::getPlanetas);
		router.post("/api/planeta").handler(this::addPlaneta);
		// router.get("/api/planetas/swapi").handler(this::getPlanetasFromAPI);
		router.get("/api/planetas/:id").handler(this::getPlanetasById);

		vertx.createHttpServer().requestHandler(router).listen(8080);
	}

	private void addPlaneta(RoutingContext rc) {

		final Planeta planeta = Json.decodeValue(rc.getBodyAsString(), Planeta.class);

		MappingManager mappingManager = MappingManager.create(cassandraClient);
		Mapper<Planeta> mapper = mappingManager.mapper(Planeta.class);

		mapper.save(planeta, handler -> {
			rc.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(planeta));
		});
	}

	private void getPlanetas(RoutingContext rc) {

		HttpServerResponse response = rc.response();

		List<Planeta> planetas = new ArrayList<Planeta>();

		cassandraClient.rxQueryStream("SELECT * FROM planetas").flatMapPublisher(CassandraRowStream::toFlowable)
				.subscribe(row -> {
					planetas.add(new Planeta(row.getInt("id"), row.getString("nome"), row.getString("clima"),
							row.getString("terreno")));
				}, t -> {
					t.printStackTrace();
					response.setStatusCode(500).end("Unable to execute the query");
				}, () -> {
					response.putHeader("content-type", "application/json;charset=utf-8")
							.end(Json.encodePrettily(planetas));
				});
	}

	/*
	 * private void getPlanetasFromAPI(RoutingContext rc) {
	 * 
	 * client = WebClient.create(vertx);
	 * 
	 * HttpRequest<JsonObject> request = client.get(443,
	 * "https://swapi.co/api/planets", "") .putHeader("Accept",
	 * "application/json").as(BodyCodec.jsonObject()); request.send(ar -> { if
	 * (ar.failed()) { rc.fail(ar.cause()); } else {
	 * rc.response().putHeader(HttpHeaders.CONTENT_TYPE,
	 * "application/json").end(ar.result().body().encode()); } });
	 * 
	 * }
	 */

	private void getPlanetasById(RoutingContext rc) {

		int id = Integer.parseInt(rc.request().getParam("id"));

		HttpServerResponse response = rc.response();

		MappingManager mappingManager = MappingManager.create(cassandraClient);
		Mapper<Planeta> mapper = mappingManager.mapper(Planeta.class);

		mapper.get(Collections.singletonList(id), handler -> {
			response.putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(handler.result()));
		});

	}

	private void getPlanetasByNome(RoutingContext rc) {

		HttpServerResponse response = rc.response();

		String nome = rc.request().getParam("nome");

		System.out.println(nome);

		List<Planeta> planetas = new ArrayList<Planeta>();

		cassandraClient.rxQueryStream("SELECT * FROM planetas WHERE nome = " + nome).flatMapPublisher(CassandraRowStream::toFlowable)
				.subscribe(row -> {
					planetas.add(new Planeta(row.getInt("id"), row.getString("nome"), row.getString("clima"),
							row.getString("terreno")));
				}, t -> {
					t.printStackTrace();
					response.setStatusCode(500).end("Unable to execute the query");
				}, () -> {
					response.putHeader("content-type", "application/json;charset=utf-8")
							.end(Json.encodePrettily(planetas));
				});

	}
}
