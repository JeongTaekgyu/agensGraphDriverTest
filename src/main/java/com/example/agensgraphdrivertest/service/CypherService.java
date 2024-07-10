package com.example.agensgraphdrivertest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bitnine.agensgraph.graph.Edge;
import net.bitnine.agensgraph.graph.GraphId;
import net.bitnine.agensgraph.graph.Path;
import net.bitnine.agensgraph.graph.Vertex;
import net.bitnine.agensgraph.jdbc.AgConnection;
import net.bitnine.agensgraph.jdbc.AgPreparedStatement;
import net.bitnine.agensgraph.jdbc.AgResultSet;
import net.bitnine.agensgraph.util.Jsonb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CypherService {

    @Autowired
    @Qualifier("agensGraphJdbcTemplate")
    private final JdbcTemplate agensGraphJdbcTemplate;

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper; // Jackson ObjectMapper 추가

    public Connection getConnection(Connection connection) throws ClassNotFoundException, SQLException {
        Class.forName("net.bitnine.agensgraph.Driver");
        String connectionString = "jdbc:agensgraph://localhost:6439/postgres";
        String username = "agens";
        String password = "agens";
        Connection conn = DriverManager.getConnection(connectionString, username, password);
        return conn;
    }

    public Map<String, Object> getCypherByAgensGraphByPsmt(String query) throws SQLException, ClassNotFoundException {

        // graphPath는 doEtl할 때 저장한 graphPath를 그대로 가져오면 된다.
        String setGraphPathQuery = "set graph_path = test_football;";
//        String setGraphPathQuery = "set graph_path = test_bracket;";
        String wrapperCypher = "Test1 wrapperCypher";
        System.out.println("~~~ query : " + query);
        Connection conn = null;

        try{
            conn = getConnection(conn);

            try {
                PreparedStatement setGraphPathStmt = conn.prepareStatement(setGraphPathQuery);
                setGraphPathStmt.execute();
            } catch (SQLException e) {
                System.out.println("~~~~~~~~~~ 여기 오나");
                e.printStackTrace();
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            try{
                PreparedStatement psmt = conn.prepareStatement(query);
                AgResultSet agRs = (AgResultSet) psmt.executeQuery();

                ResultSetMetaData metaData = agRs.getMetaData();

                while (agRs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        // 기존 parseValue 사용
//                        String columnName = metaData.getColumnLabel(i);
//                        String value = agRs.getObject(i).toString();
//
//                        Object obj = agRs.getObject(i);
//
//                        Map<String, Object> parseMap = parseValue(value);
//                        row.put(columnName, parseMap);

                        // AgResultSet 사용으로 agRs 사용!!
                        String columnName = metaData.getColumnLabel(i);
                        Object obj = agRs.getObject(i);

                        if (obj instanceof Vertex) {
                            Vertex vertex = (Vertex) obj;
                            String type = vertex.getType();
                            String label = vertex.getLabel();
                            String vertexId = vertex.getVertexId().toString();
                            Object value = vertex.getValue();
                            Object properties = vertex.getProperties();
                            System.out.println("~~~~ Vertex");
                            System.out.println("Type: " + type);
                            System.out.println("Label: " + label);
                            System.out.println("Vertex ID: " + vertexId);
                            System.out.println("Properties: " + properties);
                        } else if (obj instanceof Edge) {
                            Edge edge = (Edge) obj;
                            String type = edge.getType();
                            String label = edge.getLabel();
                            String edgeId = edge.getEdgeId().toString();
                            String startId = edge.getStartVertexId().toString();
                            String endId = edge.getEndVertexId().toString();
                            Object value = edge.getValue();
                            Object properties = edge.getProperties();
                            System.out.println("~~~~ Edge");
                            System.out.println("Type: " + type);
                            System.out.println("Label: " + label);
                            System.out.println("edge ID: " + edgeId);
                            System.out.println("Start ID: " + startId);
                            System.out.println("End ID: " + endId);
                            System.out.println("Properties: " + properties);
                        } else if (obj instanceof Path) {
                            Path path = (Path) obj;
                            String type = path.getType();
                            Object value = path.getValue();
                            System.out.println("~~~~ graphpath");
                            System.out.println("Type: " + type);
                            System.out.println("Value: " + value);
                            // Path 객체에 label 필드가 없으므로 label은 출력하지 않음
                        } else if (obj instanceof Jsonb) {
                            Jsonb jsonb = (Jsonb) obj;
                            String type = jsonb.getType();
                            Object value = jsonb.getValue();
                            System.out.println("~~~~ Jsonb");
                            System.out.println("Type: " + type);
                            System.out.println("Value: " + value);
                            // Jsonb 객체에 label 필드가 없으므로 label은 출력하지 않음
                        } else if (obj instanceof GraphId) {
                            GraphId graphId = (GraphId) obj;
                            String type = graphId.getType();
                            Object value = graphId.getValue();
                            System.out.println("~~~~ GraphId");
                            System.out.println("Type: " + type);
                            System.out.println("Value: " + value);
                            // GraphId 객체에 label 필드가 없으므로 label은 출력하지 않음
                        }

                        // Continue with your existing logic
                        String value = obj.toString();
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            List<String> columns = new ArrayList<>();
            int rowCount = rows.size();

            // rows가 비어있지 않으면 subGraph의 cypher를 업데이트하고 columns Name을 추출한다.
            if (!rows.isEmpty()) {
                // 첫번째 행에서 컬럼명을 추출한다.
                Map<String, Object> firstRow = rows.get(0);
                for (String column : firstRow.keySet()) {
                    columns.add(column);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("rows", rows);
            result.put("columns", columns);
            result.put("rowCount", rowCount);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getCypherByAgensGraph(String query) throws SQLException, ClassNotFoundException {

//        String graphPath = graphService.getGraphPath(graphId);
//        String wrapperCypher = cypherWrapper(query, graphPath);
        // graphPath는 doEtl할 때 저장한 graphPath를 그대로 가져오면 된다.
        String setGraphPathQuery = "set graph_path = test_football;";
//        String setGraphPathQuery = "set graph_path = test_bracket;";
        String wrapperCypher = "Test1 wrapperCypher";
        System.out.println("~~~ query : " + query);

        agensGraphJdbcTemplate.execute(setGraphPathQuery);
        System.out.println("~~~ setGraphPathQuery : " + setGraphPathQuery);

        List<Map<String, Object>> rows = agensGraphJdbcTemplate.query(query, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                AgResultSet agRs = (AgResultSet) rs;
                Map<String, Object> row = new LinkedHashMap<>();
                ResultSetMetaData metaData = agRs.getMetaData();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    // 기존 방식
//                    String columnName = metaData.getColumnLabel(i);
//                    String value = agRs.getObject(i).toString();
//                    Object obj = agRs.getObject(i);
//
//                    Map<String, Object> parseMap = parseValue(value);
//
//                    row.put(columnName, parseMap);


                    // AgResultSet 사용으로 agRs 사용!!
                    String columnName = metaData.getColumnLabel(i);
                    Object obj = agRs.getObject(i);

                    if (obj instanceof Vertex) {
                        Vertex vertex = (Vertex) obj;
                        String type = vertex.getType();
                        String label = vertex.getLabel();
                        String vertexId = vertex.getVertexId().toString();
                        Object value = vertex.getValue();
                        Object properties = vertex.getProperties();
                        System.out.println("~~~~ Vertex");
                        System.out.println("Type: " + type);
                        System.out.println("Label: " + label);
                        System.out.println("Vertex ID: " + vertexId);
                        System.out.println("Properties: " + properties);
                    } else if (obj instanceof Edge) {
                        Edge edge = (Edge) obj;
                        String type = edge.getType();
                        String label = edge.getLabel();
                        String edgeId = edge.getEdgeId().toString();
                        String startId = edge.getStartVertexId().toString();
                        String endId = edge.getEndVertexId().toString();
                        Object value = edge.getValue();
                        Object properties = edge.getProperties();
                        System.out.println("~~~~ Edge");
                        System.out.println("Type: " + type);
                        System.out.println("Label: " + label);
                        System.out.println("edge ID: " + edgeId);
                        System.out.println("Start ID: " + startId);
                        System.out.println("End ID: " + endId);
                        System.out.println("Properties: " + properties);
                    } else if (obj instanceof Path) {
                        Path path = (Path) obj;
                        String type = path.getType();
                        String name = path.getClass().getName();
                        Object value = path.getValue();
                        path.vertices().forEach(vertex -> {
                            System.out.println("~~~~ vertex : " + vertex);
                            System.out.println("~~~~ vertex.getType() : " + vertex.getType());
                            System.out.println("~~~~ vertex.getLabel() : " + vertex.getLabel());
                            System.out.println("~~~~ vertex.getVertexId() : " + vertex.getVertexId());
                            System.out.println("~~~~ vertex.getProperties() : " + vertex.getProperties());
                        });
                        path.edges().forEach(edge -> {
                            System.out.println("~~~~ edge : " + edge);
                            System.out.println("~~~~ edge.getType() : " + edge.getType());
                            System.out.println("~~~~ edge.getLabel() : " + edge.getLabel());
                            System.out.println("~~~~ edge.getEdgeId() : " + edge.getEdgeId());
                            System.out.println("~~~~ edge.getStartVertexId() : " + edge.getStartVertexId());
                            System.out.println("~~~~ edge.getEndVertexId() : " + edge.getEndVertexId());
                            System.out.println("~~~~ edge.getProperties() : " + edge.getProperties());
                        });
                        System.out.println("~~~~ graphpath");
                        System.out.println("Name: " + name);
                        System.out.println("path.getType().getClass().getName() : " + path.getType().getClass().getName());
                        System.out.println("Type: " + type);
                        System.out.println("Value: " + value);
                        // Path 객체에 label 필드가 없으므로 label은 출력하지 않음
                    } else if (obj instanceof Jsonb) {
                        Jsonb jsonb = (Jsonb) obj;
                        String type = jsonb.getType();
                        Object value = jsonb.getValue();
                        System.out.println("~~~~ Jsonb");
                        System.out.println("Type: " + type);
                        System.out.println("Value: " + value);
                        // Jsonb 객체에 label 필드가 없으므로 label은 출력하지 않음
                    } else if (obj instanceof GraphId) {
                        GraphId graphId = (GraphId) obj;
                        String type = graphId.getType();
                        Object value = graphId.getValue();
                        System.out.println("~~~~ GraphId");
                        System.out.println("Type: " + type);
                        System.out.println("Value: " + value);
                        // GraphId 객체에 label 필드가 없으므로 label은 출력하지 않음
                    }

                    // Continue with your existing logic
                    String value = obj.toString();
                    row.put(columnName, value);
                }
                System.out.println("~~~ row : \n" + row);
                return row;
            }
        });

        System.out.println("~~~~~ rows : \n" + rows);

        List<String> columns = new ArrayList<>();
        int rowCount = rows.size();

        // rows가 비어있지 않으면 subGraph의 cypher를 업데이트하고 columns Name을 추출한다.
        if (!rows.isEmpty()) {
            // 첫번째 행에서 컬럼명을 추출한다.
            Map<String, Object> firstRow = rows.get(0);
            for (String column : firstRow.keySet()) {
                columns.add(column);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("columns", columns);
        result.put("rowCount", rowCount);

        return result;
    }

    private Map<String, Object> parseValue(String value) {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        try {
            // Extract label
            int labelEndIndex = value.indexOf('[');
            String label = value.substring(0, labelEndIndex);
            resultMap.put("label", label);

            // Extract ID (ex , player[8.1] ) -> labelEndIndex = 6
            int idStartIndex = labelEndIndex + 1;
            int idEndIndex = value.indexOf(']', idStartIndex);
            String idString = value.substring(idStartIndex, idEndIndex);
            Double id = Double.parseDouble(idString);
            resultMap.put("id", id);

            // Check for start_id and end_id
            int nextBracketIndex = value.indexOf('[', idEndIndex);
            // 엣지일 때
            if (nextBracketIndex != -1) {
                int startIdStartIndex = nextBracketIndex + 1;
                int startIdEndIndex = value.indexOf(',', startIdStartIndex);
                String startIdString = value.substring(startIdStartIndex, startIdEndIndex);
                Double startId = Double.parseDouble(startIdString);
                resultMap.put("start_id", startId);

                int endIdStartIndex = startIdEndIndex + 1;
                int endIdEndIndex = value.indexOf(']', endIdStartIndex);
                String endIdString = value.substring(endIdStartIndex, endIdEndIndex);
                Double endId = Double.parseDouble(endIdString);
                resultMap.put("end_id", endId);

                // Extract properties
                int propertiesStartIndex = value.indexOf('{');
                if (propertiesStartIndex != -1) {
                    String propertiesJson = value.substring(propertiesStartIndex);
                    Map<String, Object> propertiesMap = objectMapper.readValue(propertiesJson, Map.class);
                    resultMap.put("properties", propertiesMap);
                }
            } else { // 노드일 때
                // Extract properties for cases without start_id and end_id
                int propertiesStartIndex = value.indexOf('{');
                if (propertiesStartIndex != -1) {
                    String propertiesJson = value.substring(propertiesStartIndex);
                    Map<String, Object> propertiesMap = objectMapper.readValue(propertiesJson, Map.class);
                    resultMap.putAll(propertiesMap);
                }
            }
        } catch (Exception e) {
            log.info("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return resultMap;
    }

    public Map<String, Object> getCypher(String query) {
//        String graphPath = graphService.getGraphPath(graphId);
//        String wrapperCypher = cypherWrapper(query, graphPath);

        String setGraphPathQuery = "set graph_path = test_football;";

        String wrapperCypher = "Test1 wrapperCypher";
        agensGraphJdbcTemplate.execute(setGraphPathQuery);

        List<Map<String, Object>> rows = agensGraphJdbcTemplate.query(query, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> row = new LinkedHashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getObject(i).toString();
                    Object obj = rs.getObject(i);

                    System.out.println("~~~~~ obj type : " + obj.getClass());
                    System.out.println("~~~~~ obj type : " + obj.getClass().getName());

                    Map<String, Object> parseMap = parseValue(value);

                    System.out.println("~~~ columnName : " + columnName);
                    System.out.println("~~~ value : " + value);
                    row.put(columnName, parseMap);
                }
                System.out.println("~~~ row : \n" + row);
                return row;
            }
        });

        System.out.println("~~~~~ rows : \n" + rows);

        List<String> columns = new ArrayList<>();
        int rowCount = rows.size();

        // rows가 비어있지 않으면 subGraph의 cypher를 업데이트하고 columns Name을 추출한다.
        if (!rows.isEmpty()) {
            // 첫번째 행에서 컬럼명을 추출한다.
            Map<String, Object> firstRow = rows.get(0);
            for (String column : firstRow.keySet()) {
                columns.add(column);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("columns", columns);
        result.put("rowCount", rowCount);
        result.put("command", findFirstWord(wrapperCypher));

        return result;
    }

    public static String findFirstWord(String str) {
        // 문자열 앞부분의 공백 제거
        str = str.trim();

        // 문자열을 공백을 기준으로 분할
        int index = str.indexOf(' ');

        // 공백을 찾지 못하면 전체 문자열을 반환
        if (index == -1) {
            return str;
        }

        // 첫 번째 공백 이전의 문자열 반환
        return str.substring(0, index);
    }

}
