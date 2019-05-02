package ame.desafio.star.wars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ame.desafio.star.wars.entity.Planeta;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.cassandra.CassandraClient;
import io.vertx.reactivex.cassandra.CassandraRowStream;
import io.vertx.reactivex.cassandra.Mapper;
import io.vertx.reactivex.cassandra.MappingManager;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClientOptions;

public class StarWarsService extends AbstractVerticle {

	private CassandraClient cassandraClient;

	@Override
	public void start() {

		CassandraClientOptions options = new CassandraClientOptions().addContactPoint("localhost")
				.setKeyspace("desafio");
		cassandraClient = CassandraClient.createShared(vertx, options);

		Router router = Router.router(vertx);
		router.route("/api/planeta*").handler(BodyHandler.create());
		router.get("/api/planeta").handler(this::getPlanetas);
		router.post("/api/planeta").handler(this::addPlaneta);
		router.get("/api/planeta/swapi").handler(this::getPlanetasFromAPI);
		router.get("/api/planeta/:id").handler(this::getPlanetaById);
		router.delete("/api/planeta/:id").handler(this::deletePlaneta);

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
					String nome = rc.request().getParam("nome");
					if (nome != null && !nome.isEmpty()) {
						response.putHeader("content-type", "application/json;charset=utf-8")
								.end(Json.encodePrettily(
										planetas.stream().filter(predicate -> predicate.getNome().equals(nome))
												.collect(Collectors.toList())));
					} else {
						response.putHeader("content-type", "application/json;charset=utf-8")
								.end(Json.encodePrettily(planetas));
					}
				});
	}

	private void getPlanetasFromAPI(RoutingContext rc) {

		WebClientOptions options = new WebClientOptions().setKeepAlive(false);
		WebClient client = WebClient.create(vertx, options);

		client.getAbs("https://swapi.co/api/planets").send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				rc.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(response.body());
			} else {
				System.out.println("Something went wrong " + ar.cause().getMessage());
			}
		});
	}

	private void getPlanetaById(RoutingContext rc) {

		int id = Integer.parseInt(rc.request().getParam("id"));

		HttpServerResponse response = rc.response();

		MappingManager mappingManager = MappingManager.create(cassandraClient);
		Mapper<Planeta> mapper = mappingManager.mapper(Planeta.class);

		mapper.get(Collections.singletonList(id), handler -> {

			if (handler.result() == null) {
				response.putHeader("content-type", "application/json; charset=utf-8")
					.end();
			} else {
				response.putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(handler.result()));
			}
		});

	}

	private void deletePlaneta(RoutingContext rc) {

		int id = Integer.parseInt(rc.request().getParam("id"));

		HttpServerResponse response = rc.response();

		MappingManager mappingManager = MappingManager.create(cassandraClient);
		Mapper<Planeta> mapper = mappingManager.mapper(Planeta.class);

		mapper.delete(Collections.singletonList(id), handler -> {
			response.setStatusCode(204).end();
		});

	}
}
