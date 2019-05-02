# Desafio Técnico: Back-end JAVA - Sandro José Garani

Para montar o banco basta rodar o arquivo desafio.db

Para executar o cógido basta executar o cammando na raiz: mvn compile vertx:run

Para executar o teste basta executar o comando na raiz: mvn test

As Rests do desafío:
- Adicionar um planeta (com nome, clima e terreno): POST http://localhost:8080/api/planeta
- Listar planetas do banco de dados: GET http://localhost:8080/api/planeta
- Listar planetas da API do Star Wars: GET http://localhost:8080/api/planeta/swapi
- Buscar por nome no banco de dados: GET http://localhost:8080/api/planeta?nome=<Nome>
- Buscar por ID no banco de dados: GET http://localhost:8080/api/planeta/<Id>
- Remover planeta: DELETE http://localhost:8080/api/planeta/<Id>
