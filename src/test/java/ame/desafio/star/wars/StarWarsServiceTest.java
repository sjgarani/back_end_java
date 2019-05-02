package ame.desafio.star.wars;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Desafio Ame")
@ExtendWith(VertxExtension.class)
public class StarWarsServiceTest {

  @Test
  @DisplayName("Adicionar um planeta (com nome, clima e terreno)")
  void addPlaneta(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      webClient.post(8080, "localhost", "/api/planeta").sendJsonObject(new JsonObject()
      .put("nome", "Jupter")
      .put("clima", "Insuportavel")
      .put("terreno", "Fala serio!"),testContext.succeeding(resp -> {
        testContext.verify(() -> {
          assertThat(resp.statusCode()).isEqualTo(201);
          requestCheckpoint.flag();
        });
      }));

    }));
  }

  @Test
  @DisplayName("Listar planetas do banco de dados")
  void getPlanetas(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      for (int i = 0; i < 1; i++) {
        webClient.get(8080, "localhost", "/api/planeta").as(BodyCodec.string()).send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("Jupter");
            requestCheckpoint.flag();
          });
        }));
      }
    }));
  }

  @Test
  @DisplayName("Listar planetas da API do Star Wars")
  void getPlanetasFromAPI(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      for (int i = 0; i < 1; i++) {
        webClient.get(8080, "localhost", "/api/planeta/swapi").as(BodyCodec.string()).send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("swapi");
            assertThat(resp.body()).contains("results");
            requestCheckpoint.flag();
          });
        }));
      }
    }));
  }

  @Test
  @DisplayName("Buscar por nome no banco de dados")
  void getPlanetasByNome(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      for (int i = 0; i < 1; i++) {
        webClient.get(8080, "localhost", "/api/planeta?nome=Jupter").as(BodyCodec.string()).send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("Jupter");
            requestCheckpoint.flag();
          });
        }));
      }
    }));
  }

  @Test
  @DisplayName("Buscar por ID no banco de dados")
  void getPlanetasById(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      for (int i = 0; i < 1; i++) {
        webClient.get(8080, "localhost", "/api/planeta/0").as(BodyCodec.string()).send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("Jupter");
            requestCheckpoint.flag();
          });
        }));
      }
    }));
  }

  @Test
  @DisplayName("Remover planeta")
  void deletePlanetas(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx);
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint requestCheckpoint = testContext.checkpoint(1);

    vertx.deployVerticle(new StarWarsService(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();

      for (int i = 0; i < 1; i++) {
        webClient.delete(8080, "localhost", "/api/planeta/0").send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(204);
            requestCheckpoint.flag();
          });
        }));
      }
    }));
  }

}