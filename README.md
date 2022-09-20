# **Introdução**

O repositório DijkstraAirport possui a pasta ***src*** como principal, e contém os arquivos essenciais para a resolução do seguinte problema: considerando os aeroportos internacionais do Brasil, implemente uma aplicação em Java que calcule a **menor rota** entre dois destes aeroportos.

Dentre as restrições impostas ao problema, destaca-se a integração com um banco de dados ***MySQL***, a utilização do ***algoritmo de Dijkstra*** e a necessidade de haver no mínimo uma escala em qualquer rota.

A seguir, será dada as devidas explicações do código-fonte elaborado.

# Caminho *src/server/*

Na pasta ***server***, há apenas o arquivo ***Connect.java***.

Esse arquivo serve, em suma, para definir a função ***connect()***, que é responsável pela conexão e integração de todo o conjunto com o ***MySQL***:

```java
public class Connect {
    private String server, db, user, password;
    private int port;

    public Connect(int status) {
        if(status == 0) {
            this.server = "127.0.0.1";
            this.db = "airport";
            this.user = "root";
            this.password = "pass123";
            this.port = 3306;
        }
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.db;
        return DriverManager.getConnection(url, this.user, this.password);
    }
}
```

O método ***DriverManager.getConnection*** é quem estabelece a conexão com o banco de dados, fazendo isso por meio da URL que representa o servidor, usuário e senha.

Código completo do arquivo ***Connect.java***:

```java
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//****Java class that defines the function responsible for connecting and integrating the software with MySQL****

public class Connect {
    private String server, db, user, password;
    private int port;

    public Connect(int status) {
        if(status == 0) {
            this.server = "127.0.0.1";
            this.db = "airport";
            this.user = "root";
            this.password = "pass123";
            this.port = 3306;
        }
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.db;
        return DriverManager.getConnection(url, this.user, this.password);
    }
}
```

# Caminho src/model/

Na pasta ***model***, temos as três modelagens que são necessárias para o nosso projeto: ***Airport.java, AirportDAO.java, WeightedGraph.java***

### **Airport.java**

O arquivo ***Airport.java*** descreve os atributos e métodos que cada aeroporto tem.

Para o nosso algoritmo, é essencial que tenhamos o **nome** do aeroporto (para conseguirmos diferenciá-los), **latitude** e **longitude** (para o cálculo das distâncias). Além destes, para respeitar outros requisitos impostos, são também considerados atributos: **iata, estado** e **cidade**.

Por questões de praticidade, foi-se definido um outro construtor personalizado:

```java
public class Airport {
    private String iata, name, city, state;
    private double latitude, longitude;

    //custom constructor
    public Airport(String iata, String name, String city, String state, double latitude, double longitude) {
        this.iata = iata;
        this.name = name;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }
```

Como, por questões de boas práticas, os atributos foram considerados privados, em certo momento foi necessário estabelecer os ***getters*** e ***setters***:

```java
public String getState() {
        return this.state;
    }

    public String getCity() {
        return this.city;
    }

    public String getIata() {
        return this.iata;
    }

    public String getName() {
        return this.name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
```

Na modelagem da classe criamos também a função ***distance(airport)*** para determinarmos a distância entre dois aeroportos. Nela, o método escolhido para os cálculos foi por meio da ***fórmula de haversine***, que fornece a distância entre dois pontos de uma esfera a partir de suas latitudes e longitudes. Fica claro, portanto, que consideramos aqui a Terra como uma esfera perfeita:

```java
//function to convert degrees to radians
    public double degToRad(double deg) {
        return deg*Math.PI/180;
    }

    public double distance(Airport airport) {
        double dLat = degToRad(airport.latitude - this.latitude);
        double dLon = degToRad(airport.longitude - this.longitude);
        double r = 6378;

        //haversine formula
        double h = Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLon/2), 2)*Math.cos(degToRad(this.latitude))*Math.cos(degToRad(airport.latitude));

        return 2*r*Math.asin(Math.sqrt(h));
    }
```

Além disso, como podemos notar, há também a criação de uma função auxiliar ***degToRad(deg)*** que converte um dado ângulo em graus para radianos.

Código completo do arquivo ***Airport.java***:

```java
package model;

public class Airport {
    private String iata, name, city, state;
    private double latitude, longitude;

    //custom constructor
    public Airport(String iata, String name, String city, String state, double latitude, double longitude) {
        this.iata = iata;
        this.name = name;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getState() {
        return this.state;
    }

    public String getCity() {
        return this.city;
    }

    public String getIata() {
        return this.iata;
    }

    public String getName() {
        return this.name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    //function to convert degrees to radians
    public double degToRad(double deg) {
        return deg*Math.PI/180;
    }

    public double distance(Airport airport) {
        double dLat = degToRad(airport.latitude - this.latitude);
        double dLon = degToRad(airport.longitude - this.longitude);
        double r = 6378;

        //haversine formula
        double h = Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLon/2), 2)*Math.cos(degToRad(this.latitude))*Math.cos(degToRad(airport.latitude));

        return 2*r*Math.asin(Math.sqrt(h));
    }
}
```

### AirportDAO.java

A sigla ***DAO*** significa ***Data Access Object***. O papel central desse arquivo é descrever métodos que interagem diretamente com o banco de dados a fim de extrair ou inserir informações.

Nele, temos um construtor que realiza a conexão com o banco ***MySQL***:

```java
private Connection database;

    public AirportDAO() throws SQLException {
        database = (new Connect(0)).connect();
    }
```

E dois métodos que retornam um ***ResultSet*** - uma tabela de dados que representa um conjunto de resultados de um banco de dados. Eles são retornados após o código executar uma ***query*** no banco MySQL. Importante mencionar que o acesso aos dados de um ResultSet se dá através de um cursor que aponta para uma linha da tabela, algo que veremos na prática mais adiante.

O primeiro método, ***getAllAirports()***, recupera todos os aeroportos existentes no BD; já o segundo, ***getAirportsFrom(city, state)***, recupera apenas os aeroportos que estão localizados em um dado par (cidade, estado).

Código completo do arquivo ***AirportDAO.java***:

```java
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.Connect;

public class AirportDAO {
    private Connection database;

    public AirportDAO() throws SQLException {
        database = (new Connect(0)).connect();
    }

    public ResultSet getAllAirports() throws SQLException {
        String stmt = "SELECT * FROM airportData ORDER BY 1;";
        PreparedStatement pStmt = database.prepareStatement(stmt);
        return pStmt.executeQuery();
    }

    public ResultSet getAirportsFrom(String city, String state) throws SQLException {
        String stmt = "SELECT iata, name FROM airportData WHERE city = ? AND state = ? ORDER BY 1;";
        PreparedStatement pStmt = database.prepareStatement(stmt);
        pStmt.setString(1, city);
        pStmt.setString(2, state);
        return pStmt.executeQuery();
    }
}
```

### WeightedGraph.java

É o arquivo fundamental do projeto, pois nele está contida toda a estrutura do grafo que usaremos, e também o ***algoritmo de Dijkstra***.

Para esse projeto, um grafo foi definido a partir das suas ***listas de adjacência***, que é do tipo **HashMap<Airport, LinkedList<Pair>>***.* Um **Pair**, por sua vez, nada mais é do que um par (aeroporto, peso), e é necessário porque estamos lidando com um grafo com pesos, de forma para cada aeroporto devemos representar não apenas as adjacências mas também o peso das arestas:

```java
private HashMap<Airport, LinkedList<Pair>> adj = new HashMap<>();

    static class Pair implements Comparable<Pair>{
        Airport airport;
        double weight;

        public Pair(Airport airport, double weight) {
            this.airport = airport;
            this.weight = weight;
        }

        @Override
        public int compareTo(Pair p) {
            if(this.weight < p.weight)
                return -1;
            else if(this.weight > p.weight)
                return 1;
            return 0;
        }
    }
```

O método ***compareTo(p)***, que é uma sobrescrita, simplifica o código para comparar dois pares (é claro que queremos comparar os pesos desses pares).

Em seguida, temos as funções que adicionam um aeroporto ao conjunto de vértices, ***addAirport(dest)***, e que adicionam uma aresta ao conjunto de arestas, ***addPath(a, b, weight)***. Como o grafo é não-direcional, adicionamos um aeroporto na lista de adjacência do outro, e também o contrário. Há também o método que recupera o conjunto de todos os aeroportos do grafo, ***getAirports()***:

```java
public void addAirport(Airport dest) {
        adj.put(dest, new LinkedList<>());
    }

    public void addPath(Airport a, Airport b, double weight) {
        if(!adj.containsKey(a))
            addAirport(a);
        if(!adj.containsKey(b))
            addAirport(b);
        adj.get(a).add(new Pair(b, weight));
        adj.get(b).add(new Pair(a, weight));
    }

    public Set<Airport> getAirports() {
        return adj.keySet();
    }
```

Por fim, vemos o ***algoritmo de Dijkstra***:

```java
public void dijkstra(Airport src, Airport dest) {
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        HashMap<Airport, Double> distance = new HashMap<>();
        HashMap<Airport, Airport> parent = new HashMap<>();

        for(Airport airport : adj.keySet())
            distance.put(airport, Double.POSITIVE_INFINITY);

        pq.add(new Pair(src, 0));
        distance.put(src, 0.0);

        while(!pq.isEmpty()) {
            Airport currentAirport = pq.peek().airport;
            pq.remove();

            for(Pair pair : adj.get(currentAirport)) {
                double weight = pair.weight;
                Airport child = pair.airport;

                if(child.getIata().equals(dest.getIata()) && currentAirport.getIata().equals(src.getIata()))
                    continue;

                if(distance.get(child) > distance.get(currentAirport) + weight) {
                    distance.put(child, distance.get(currentAirport) + weight);
                    pq.add(new Pair(child, distance.get(child)));
                    parent.put(child, currentAirport);
                }
            }
        }
        System.out.println("Shortest path length: " + distance.get(dest) +
                " with a connecting flight in " + parent.get(dest).getIata());
    }
}
```

Para realizar o algoritmo, optamos pela abordagem através de uma ***priority queue*** formada pelos pares, ou seja, **PriorityQueue<Pair>**. O ***HashMap*** **HashMap<Airport, Double> distance** também faz parte do algoritmo, e é utilizado para guardar o tamanho do menor caminho do aeroporto ***src*** (parâmetro da função) a cada um dos outros.

O **HashMap<Airport, Airport> parent** foi um acréscimo ao algoritmo para conseguirmos recuperar o aeroporto onde é feita a conexão antes de chegar ao destino. 

O algoritmo funciona da seguinte forma:

1. Percorre-se todos os vértices do grafo (*adj.keySet()*) e considera o peso do caminho sendo “infinito”;
    
    ```java
    for(Airport airport : adj.keySet())
    	  distance.put(airport, Double.POSITIVE_INFINITY);
    ```
    
2. Na fila de prioridade e no hash de distâncias mínimas, adicionamos a única informação que sabemos de imediato: o custo do caminho início-início é nulo;
    
    ```java
    pq.add(new Pair(src, 0));
    distance.put(src, 0.0);
    ```
    
3. Escolhemos o primeiro da fila de prioridade (maior prioridade) e percorremos cada aeroporto adjacente ao que escolhemos. Se o par de aeroportos que estão sob análise são justamente o inicial e o final, pulamos para o próximo aeroporto na sequência (pois queremos pelo menos uma conexão);
    
    ```java
    for(Pair pair : adj.get(currentAirport)) {
    	  double weight = pair.weight;
    	  Airport child = pair.airport;
        if(child.getIata().equals(dest.getIata()) && currentAirport.getIata().equals(src.getIata()))
            continue;
    ```
    
4. Se o custo armazenado para ir ao aeroporto adjacente for maior do que o custo com conexão, atualizamos o custo armazenado para este último, acrescentamos esse par na fila e atualizamos qual é a conexão que proporciona o menor custo;
    
    ```java
    if(distance.get(child) > distance.get(currentAirport) + weight) {
    	  distance.put(child, distance.get(currentAirport) + weight);
        pq.add(new Pair(child, distance.get(child)));
        parent.put(child, currentAirport);
    }
    ```
    
5. Por fim, como queremos saber para um destino específico (é parâmetro), o algoritmo imprime qual o menor custo partindo do aeroporto inicial fornecido e onde a conexão deve ser feita.

Código completo do arquivo ***WeightedGraph.java***:

```java
package model;

import java.util.*;

public class WeightedGraph {
    private HashMap<Airport, LinkedList<Pair>> adj = new HashMap<>();

    static class Pair implements Comparable<Pair>{
        Airport airport;
        double weight;

        public Pair(Airport airport, double weight) {
            this.airport = airport;
            this.weight = weight;
        }

        @Override
        public int compareTo(Pair p) {
            if(this.weight < p.weight)
                return -1;
            else if(this.weight > p.weight)
                return 1;
            return 0;
        }
    }

    public void addAirport(Airport dest) {
        adj.put(dest, new LinkedList<>());
    }

    public void addPath(Airport a, Airport b, double weight) {
        if(!adj.containsKey(a))
            addAirport(a);
        if(!adj.containsKey(b))
            addAirport(b);
        adj.get(a).add(new Pair(b, weight));
        adj.get(b).add(new Pair(a, weight));
    }

    public Set<Airport> getAirports() {
        return adj.keySet();
    }

    public void dijkstra(Airport src, Airport dest) {
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        HashMap<Airport, Double> distance = new HashMap<>();
        HashMap<Airport, Airport> parent = new HashMap<>();

        for(Airport airport : adj.keySet())
            distance.put(airport, Double.POSITIVE_INFINITY);

        pq.add(new Pair(src, 0));
        distance.put(src, 0.0);

        while(!pq.isEmpty()) {
            Airport currentAirport = pq.peek().airport;
            pq.remove();

            for(Pair pair : adj.get(currentAirport)) {
                double weight = pair.weight;
                Airport child = pair.airport;

                if(child.getIata().equals(dest.getIata()) && currentAirport.getIata().equals(src.getIata()))
                    continue;

                if(distance.get(child) > distance.get(currentAirport) + weight) {
                    distance.put(child, distance.get(currentAirport) + weight);
                    pq.add(new Pair(child, distance.get(child)));
                    parent.put(child, currentAirport);
                }
            }
        }
        System.out.println("Shortest path length: " + distance.get(dest) +
                " with a connecting flight in " + parent.get(dest).getIata());
    }
}
```

## Caminho src/view/

Nesse caminho, há apenas o ***main.java***, onde a função ***public static void main(String[] args)*** será executada.

Extrai-se, no início, os aeroportos e seus atributos do banco de dados *MySQL*:

```java
AirportDAO DAO = new AirportDAO();
	  ResultSet table = DAO.getAllAirports();
	  WeightedGraph graph = new WeightedGraph();
	  while(table.next()) {
	      Airport airport = new Airport(table.getString("iata"), table.getString("name"),
	              table.getString("city"), table.getString("state"),
	              table.getDouble("latitude"), table.getDouble("longitude"));
	      graph.addAirport(airport);
	  }
```

Logo em seguida, cria-se um grafo completo com os aeroportos, ou seja, existe um caminho direto entre quaisquer dois aeroportos:

```java
for(Airport src : graph.getAirports()) {
	  for(Airport dest : graph.getAirports()) {
	      if(src.distance(dest) != 0)
	          graph.addPath(src,dest, src.distance(dest));
	  }
}
```

A partir daí, temos uma interação com o usuário por meio de menus e, no fim, executamos o algoritmo de Dijkstra:

```java
Scanner sc = new Scanner(System.in);
    System.out.println("Do you want to do an airport search? (Y/N)");
    String city, state, iata1, iata2;
    while(sc.nextLine().equalsIgnoreCase("Y")) {
        //prints states list
        HashMap<String, Integer> hash = new HashMap<>();
        for(Airport airport : graph.getAirports()) {
            if(!hash.containsKey(airport.getState())) {
                System.out.println(airport.getState());
                hash.put(airport.getState(), 1);
            }
        }
        System.out.print("Please select the state from the list above: ");
        state = sc.nextLine();
        //prints cities list
        for(Airport airport : graph.getAirports()) {
            if(airport.getState().equalsIgnoreCase(state) && !hash.containsKey(airport.getCity())) {
                System.out.println(airport.getCity());
                hash.put(airport.getCity(), 1);
            }
        }
        System.out.print("Now, please select the city from the list above: ");
        city = sc.nextLine();
        //prints airport list
        for(Airport airport : graph.getAirports()) {
            if(airport.getCity().equalsIgnoreCase(city) && airport.getState().equalsIgnoreCase(state))
                System.out.println(airport.getIata() + " - " + airport.getName());
        }
        System.out.println();
        System.out.println("Would you like to do another search? (Y/N)");
    }

    System.out.println();
    System.out.print("Please enter the IATA code (3 letters) of the source airport: ");
    iata1 = sc.nextLine();
    System.out.print("Please enter the IATA code (3 letters) of the destination airport: ");
    iata2 = sc.nextLine();
    for(Airport airport1 : graph.getAirports())
        if(airport1.getIata().equalsIgnoreCase(iata1)) {
            for(Airport airport2 : graph.getAirports()) {
                if(airport2.getIata().equalsIgnoreCase(iata2))
                    graph.dijkstra(airport1,airport2);
            }
    }
```

Código completo do arquivo ***main.java***:

```java
package view;

import model.Airport;
import model.AirportDAO;
import model.WeightedGraph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws SQLException {
        //**import data from SQL and create graph in memory**
        AirportDAO DAO = new AirportDAO();
        ResultSet table = DAO.getAllAirports();
        WeightedGraph graph = new WeightedGraph();
        while(table.next()) {
            Airport airport = new Airport(table.getString("iata"), table.getString("name"),
                    table.getString("city"), table.getString("state"),
                    table.getDouble("latitude"), table.getDouble("longitude"));
            graph.addAirport(airport);
        }
        for(Airport src : graph.getAirports()) {
            for(Airport dest : graph.getAirports()) {
                if(src.distance(dest) != 0)
                    graph.addPath(src,dest, src.distance(dest));
            }
        }
        //**import data from SQL and create graph in memory**

        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to do an airport search? (Y/N)");
        String city, state, iata1, iata2;
        while(sc.nextLine().equalsIgnoreCase("Y")) {
            //prints states list
            HashMap<String, Integer> hash = new HashMap<>();
            for(Airport airport : graph.getAirports()) {
                if(!hash.containsKey(airport.getState())) {
                    System.out.println(airport.getState());
                    hash.put(airport.getState(), 1);
                }
            }
            System.out.print("Please select the state from the list above: ");
            state = sc.nextLine();
            //prints cities list
            for(Airport airport : graph.getAirports()) {
                if(airport.getState().equalsIgnoreCase(state) && !hash.containsKey(airport.getCity())) {
                    System.out.println(airport.getCity());
                    hash.put(airport.getCity(), 1);
                }
            }
            System.out.print("Now, please select the city from the list above: ");
            city = sc.nextLine();
            //prints airport list
            for(Airport airport : graph.getAirports()) {
                if(airport.getCity().equalsIgnoreCase(city) && airport.getState().equalsIgnoreCase(state))
                    System.out.println(airport.getIata() + " - " + airport.getName());
            }
            System.out.println();
            System.out.println("Would you like to do another search? (Y/N)");
        }

        System.out.println();
        System.out.print("Please enter the IATA code (3 letters) of the source airport: ");
        iata1 = sc.nextLine();
        System.out.print("Please enter the IATA code (3 letters) of the destination airport: ");
        iata2 = sc.nextLine();
        for(Airport airport1 : graph.getAirports())
            if(airport1.getIata().equalsIgnoreCase(iata1)) {
                for(Airport airport2 : graph.getAirports()) {
                    if(airport2.getIata().equalsIgnoreCase(iata2))
                        graph.dijkstra(airport1,airport2);
                }
            }
    }
}
```
